
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Consumables.Bronze;
import com.inoculates.fatesreprise.Consumables.Copper;
import com.inoculates.fatesreprise.Consumables.Heart;
import com.inoculates.fatesreprise.Effects.Grass;
import com.inoculates.fatesreprise.Effects.Ripple;
import com.inoculates.fatesreprise.Effects.Splash;
import com.inoculates.fatesreprise.Screens.GameScreen;

import java.awt.*;

// Enemy superclass that closely resembles the Daur class.
public abstract class Enemy extends Character {
    protected static final int FALLING = 3, RUNNING = 4, DROWNING = 5;

    protected Animation idle, run, fall, drown;

    protected int health;
    protected boolean invulnerability = false, fallingHole = false, slowed = false, grounded = true, stuncooldown = false;

    protected Grass grass;
    protected Ripple ripple;

    protected int cellX, cellY;
    protected boolean falling = false, drowning = false, wadeCooldown = false, grassCooldown = false;
    private int wadeNum;
    private float maxSpeed;

    // Constructor if the enemy has armor.
    public Enemy(GameScreen screen, TiledMap map, TextureAtlas atlas, int health) {
        super(screen, map, atlas, screen.storage);
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        grass = new Grass(screen, map, screen.daurAtlases.get(3), this);
        ripple = new Ripple(screen, map, screen.daurAtlases.get(3), this);
        this.health = health;
        createUniversalAnimations();
    }

    // If the enemy does not.
    public Enemy(GameScreen screen, TiledMap map, TextureAtlas atlas, int health, int armor) {
        super(screen, map, atlas, screen.storage);
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        grass = new Grass(screen, map, screen.daurAtlases.get(3), this);
        ripple = new Ripple(screen, map, screen.daurAtlases.get(3), this);
        this.health = health;
        this.armor = armor;
        createUniversalAnimations();
    }

    // Creates the universal animations(all enemies have these) .
    private void createUniversalAnimations() {
        fall = new Animation(0.33333f, atlas.findRegion("enemyfall1"), atlas.findRegion("enemyfall2"),
                atlas.findRegion("enemyfall3"));
        drown = new Animation(1, atlas.findRegion("enemydrown"));
    }

    protected void periodicCheck(float deltaTime) {
        if (!incapacitated()) {
            updateTime(deltaTime);
            if (state != FALLING && state != DROWNING)
                update(deltaTime);
        }
        if ((vel.x != 0 || vel.y != 0 || ace.x != 0 || ace.y != 0) && canMove() && state != FALLING && state != DROWNING) {
            tryMove();
            // If the enemy is moving and on shallow water plays the wading sound. This is a constant alternation between two
            // sounds.
            if ((vel.x != 0 || vel.y != 0) && screen.effects.contains(ripple) && !wadeCooldown && grounded) {
                wadeCooldown = true;
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        wadeCooldown = false;
                    }
                }, 0.35f);

                switch (wadeNum) {
                    case 0:
                        storage.sounds.get("wade1").play(2.0f);
                        wadeNum = 1;
                        break;
                    case 1:
                        storage.sounds.get("wade2").play(2.0f);
                        wadeNum = 0;
                        break;
                }
            }

            // Same but for the grass sound.
            if ((vel.x != 0 || vel.y != 0) && screen.effects.contains(grass) && !grassCooldown) {
                grassCooldown = true;
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        grassCooldown = false;
                    }
                }, 0.25f);
                storage.sounds.get("grasswalk").play(1.0f);
            }

        }

        createAnimations();
        chooseSprite();
        checkFall();
        checkSpeed();
    }

    protected void checkFall() {
        // If the enemy is not on the ground, it cannot fall.
        if (!grounded)
            return;
        // If the enemy is currently in a hole, causes him to fall.
        if (detectHole() != null) {
            // Creates the boolean variable isFalling, to check if the enemy is close enough to fall, or only close enough
            // to gravitate.
            boolean isFalling = false;
            // Gets the middle of the hole cell.
            Point holePoint = detectHole();
            // Distance from the enemy to the middle of the hole cell.
            float distanceX = Math.abs(holePoint.x - getX() - getWidth() / 2);
            float distanceY1 = Math.abs(holePoint.y - getY() - getHeight() / 2);
            // If the enemy is sufficiently close in both the x and y respects, causes him to fall.
            if (distanceX < getWidth() / 2 && distanceY1 < getHeight() / 2) {
                fallHole(holePoint);
                isFalling = true;
            }
            // Otherwise simply gravitates him.
            if (!isFalling) {
                fallingHole = true;
                gravitateHole(holePoint);
            }
        }
        // Else if the enemy is not in a hole cell, but was falling previously, snaps him back to a normal state. Also reduces
        // his acceleration to zero, so he is no longer gravitating.
        else if (fallingHole) {
            fallingHole = false;
            ace.x = 0;
            ace.y = 0;
        }
    }

    // This method is responsible for constantly accelerating the enemy towards the center of the hole.
    private void gravitateHole(Point holePoint) {
        // Gets angle between the enemy and center of the hole.
        float angle = (float) Math.atan2(holePoint.y - getY() - getHeight() / 2, holePoint.x - getX() - getWidth() / 2);
        // Causes the enemy to accelerate towards the hole, but not by too much.
        ace.x = (float) Math.cos(angle) / 10;
        ace.y = (float) Math.sin(angle);
        // Sets the max speed to the enemy Daur does not fly into the hole at super speeds.
        maxSpeed = 0.75f;
    }

    // Ensures that the enemy is not going too fast.
    private void checkSpeed() {
        // Ensures that the enemy does not accelerate out of control by setting a maximum speed.
        if (ace.x != 0 && Math.abs(vel.x) > maxSpeed)
            vel.x = Math.signum(vel.x) * maxSpeed;

        if (ace.y != 0 && Math.abs(vel.y) > maxSpeed)
            vel.y = Math.signum(vel.y) * maxSpeed;
    }

    protected void setState(int cState, boolean override) {
        if (cState == state || (!override && overrideCheck()) || (override && priorities(cState)))
            return;

        state = cState;
        if (state != IDLE)
            animationTime = 0;
    }

    protected boolean overrideCheck() {
        return state == FALLING || state == DROWNING;
    }

    public void loseHealth(int h) {
        if (!invulnerability) {
            health -= (h - armor);
            invulnerability = true;
            flickerSprite();
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    invulnerability = false;
                    inverted = false;
                }
            }, 0.6f);

            if (health == 0) {
                death();
                // Plays death sound.
                storage.sounds.get("death2").play(1.0f);
            }
            else {
                // Plays hurt sound.
                int random = (int) (Math.random() * 2);
                switch (random) {
                    case 0:
                        storage.sounds.get("hurt1").play(1.0f);
                        break;
                    case 1:
                        storage.sounds.get("hurt2").play(1.0f);
                        break;
                }
            }
        }
    }

    // If the enemy is hit by a sword or by another damaging force.
    public void damageCollision(Sprite sprite, int dmg) {
        if (armor >= dmg || invulnerability || isDead())
            return;

        float angle = (float) Math.atan2(getY() - sprite.getY(), getX() - sprite.getX());
        vel.x = (float) (4 * Math.cos(angle));
        vel.y = (float) (4 * Math.sin(angle));
        stun();
        loseHealth(dmg);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
                unStun();
            }
        }, 0.1f);
    }

    // This is if the enemy is hit by an action that stuns but does NOT damage. For example, if hit by a concussive shot.
    // Note the lack of lose health.
    public void stunCollision(Sprite sprite, float time) {
        // Enemy cannot be stunned if invulnerable or transparent.
        if (transparent || isDead() || stuncooldown)
            return;

        float angle = (float) Math.atan2(getY() - sprite.getY(), getX() - sprite.getX());
        vel.x = (float) (4 * Math.cos(angle));
        vel.y = (float) (4 * Math.sin(angle));
        stun();
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
            }
        }, 0.1f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
            }
        }, time);
        screen.globalTimer.start();

        // Plays the stun collision sound.
        screen.storage.sounds.get("bounce").play(1.0f);
        // Sets stun cooldown to be true so that the enemy is not stunned overmuch.
        stuncooldown = true;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                stuncooldown = false;
            }
        }, 0.1f);
    }

    protected void flickerSprite() {
        // Returns if falling to ensure the sprite does NOT change color.
        if (state == FALLING)
            return;
        inverted = true;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (state == FALLING)
                    return;
                inverted = true;
            }
        }, 0.4f);
    }

    // Sets its state to dead, and removes itself and all effects.
    protected void death() {
        // Sets a constant version of self for a delayed event.
        final Enemy enemy = this;
        // If already dead, no need to die twice.
        if (isDead())
            return;
        // Sets state to dead.
        setState(DEAD, true);
        // Gives the appearance that the enemy is fading away.
        fadeAway();
        // Removes the sprite and all effects after one second.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Returns if falling or drowning, as a fall or a drown has a separate kill method.
                if (state == FALLING || state == DROWNING)
                    return;
                // Checks if this enemy a part of a triggering event.
                screen.checkClear(enemy);
                // Has a random chance of creating a consumable.
                createConsumable();
                // Removes self from game.
                removeSelf();
            }
        }, 1);
    }

    protected void removeSelf() {
        // Removes the grass and ripple effect, if the enemy has it.
        if (screen.effects.contains(grass))
            screen.effects.remove(grass);
        if (screen.effects.contains(ripple))
            screen.effects.remove(ripple);

        // Removes the character based on which rendering list it is on.
        if (screen.characters1.contains(this))
            screen.characters1.remove(this);
        if (screen.characters2.contains(this))
            screen.characters2.remove(this);
        if (screen.characters3.contains(this))
            screen.characters3.remove(this);
    }

    // Note that this method only applies to LESSER enemies. Greater enemies have a different consumable pattern.
    protected void createConsumable() {
        // Creates a random number between 0 and 20, inclusive.
        int random = (int) (Math.random() * 20);
        // Bronze has a 40% chance of spawning from a regular enemy. Copper has a 5% chance. A heart has a 25% chance.
        // Spawning for bronze.
        if (random >= 0 && random <= 7) {
            Bronze bronze = new Bronze(screen, map, screen.miscAtlases.get(1),
                    getX() + getWidth() / 2, getY() + getHeight() / 2);
            screen.consumables.add(bronze);
        }
        // For copper.
        if (random >= 8 && random <= 8) {
            Copper copper = new Copper(screen, map, screen.miscAtlases.get(1),
                    getX() + getWidth() / 2, getY() + getHeight() / 2);
            screen.consumables.add(copper);
        }
        // For a heart.
        if (random >= 14 && random <= 17) {
            Heart heart = new Heart(screen, map, screen.miscAtlases.get(1),
                    getX() + getWidth() / 2, getY() + getHeight() / 2);
            screen.consumables.add(heart);
        }
    }

    // This is what occurs if an enemy were to fall down a hole..
    protected void fallHole(Point hole) {
        // If the enemy is already falling, no need to make him fall down twice. Also, flying creatures cannot fall.
        if (falling || !grounded)
            return;
        // Sets the state to falling for the animation.
        setState(FALLING, true);
        falling = true;
        // Makes enemy invulnerable and transparent to prevent any damage or stun while falling.
        invulnerability = true;
        transparent = true;
        // Ensures the enemy is not inverted when falling.
        inverted = false;
        // Causes the enemy to be motionless and receive no input.
        freeze();
        stun();
        ace.x = 0;
        ace.y = 0;
        // This method adjusts the position of enemy's sprite to emulate the enemy falling down the center of the hole.
        setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
        // Constantly resets position over a short period.
        final Point fallHole = hole;
        for (float i = 0; i < 1; i += 0.01f)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    resetPosition(fallHole);
                }
            }, i);

        // After one second of falling, the enemy will be removed from the game.
        final Enemy enemy = this;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Checks if this enemy a part of a triggering event.
                screen.checkClear(enemy);
                // Removes self from game.
                removeSelf();
            }
        }, 1);

        // Plays falling sound.
        storage.sounds.get("fall1").play(1.0f);
    }

    // Same as Daur's method, except removes self from the game.
    private void drown() {
        if (drowning)
            return;
        setState(DROWNING, true);
        drowning = true;
        // Causes the enemy to be motionless and receive no input.
        freeze();
        stun();
        setPosition((int) ((getCX()) / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2 - getWidth() / 2,
                (int) ((getY() + 2) / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2 - getHeight() / 2);
        chooseSprite();
        setPosition((int) ((getCX()) / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2 - getWidth() / 2,
                (int) ((getY() + 2) / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2 - getHeight() / 2);
        // Adds the small splash effect.
        Splash splash = new Splash(screen, map, screen.daurAtlases.get(3), this);
        screen.effects.add(splash);
        // Makes enemy invulnerable and transparent to prevent any damage or stun while falling.
        invulnerability = true;
        transparent = true;

        // After 0.75 seconds  of drowning, the enemy will be removed from the game.
        final Enemy enemy = this;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Checks if this enemy a part of a triggering event.
                screen.checkClear(enemy);
                // Removes self from game.
                removeSelf();
            }
        }, 0.75f);

        // Plays drowning sound.
        storage.sounds.get("drown").play(1.0f);
    }

    // With every new frame, the enemy's position is reset due to the difference in frame sizes of the falling
    // animation.
    protected void resetPosition(final Point hole) {
        chooseSprite();
        setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
    }

    // This method creates the disappearing effect for the death effect. Note that it  sets the transparency in 0.1
    // intervals.
    protected void fadeAway() {
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.9f);
            }
        }, 0.1f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.7f);
            }
        }, 0.3f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.6f);
            }
        }, 0.4f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.5f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.4f);
            }
        }, 0.6f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.3f);
            }
        }, 0.7f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.8f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.1f);
            }
        }, 0.9f);
    }

    protected void detectConditions() {
        if (detectSlow() && grounded) {
            slowed = true;
            setModifier(0.35f, 0.35f);
        }
        else if (slowed) {
            slowed = false;
            resetModifier(0.35f, 0.35f);
        }

        if (detectGrass() != 4) {
            grass.setType(detectGrass());
            if (!screen.effects.contains(grass)) {
                screen.effects.add(grass);
                grass.setPosition(getX(), getY());
                grass.setAnimationTime(animationTime);
            }
        }
        else if (screen.effects.contains(grass))
            screen.effects.remove(grass);

        if (detectShallowWater() && grounded) {
            if (!screen.effects.contains(ripple)) {
                screen.effects.add(ripple);
                ripple.setPosition(getX(), getY());
                ripple.setAnimationTime(animationTime);
            }
        }
        else if (screen.effects.contains(ripple))
            screen.effects.remove(ripple);

        // If the enemy is in normal or deep water, it drowns..
        if ((detectWater() || detectDeepWater()) && grounded)
            drown();

    }

    // A complex method that detects whether the enemy is near enough a hole to fall down it.
    protected Point detectHole() {
        // The point of the hole (has both x and y components).
        Point holePoint;
        // Iterates through the enemy's width (from the x origin to the right end of the character).
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            // Iterates through the character's width (from the x origin to the right end of the character).
            if (isCellHole(getX() + 1 + step, getY() + 1)) {
                // These complex-looking lines of code simply get the middle of the cell with the hole in it.
                float holeX = (int) ((getX() + 1 + step) / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() * 0.5f;
                float holeY = (int) ((getY() + 1) / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() * 0.5f;
                // Sets the middle of the cell as the hole point and returns it.
                holePoint = new Point((int) holeX, (int) holeY);
                return holePoint;
            }
        return null;
    }


    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        // Accelerates the enemy accordingly.
        vel.x += ace.x;

        setX(getX() + vel.x);

        // Note that the enemy colliding with the edge of the current cell is counted as a collision. Meaning that the
        // enemy cannot wander farther than the edge of the cell.
        if (vel.x < 0)
            collisionX = collidesLeft() || collidesHalfBlockLeft() || collidesInteractable() ||
                    getX() < (cellX - 1) * layer.getTileWidth() * 10 ;
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesHalfBlockRight() || collidesInteractable() ||
                    getX() + getWidth() > cellX * layer.getTileWidth() * 10;

        if (collisionX) {
            setX(oldX);
            vel.x = vel.x * -1;
            dir = -dir;
        }

        // Accelerates the enemy accordingly.
        vel.y += ace.y;

        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = collidesBottom() || collidesHalfBlockBottom() || collidesInteractable() ||
                    getY() < (cellY - 1) * layer.getTileHeight() * 10;

        else if (vel.y > 0)
            collisionY = collidesTop() || collidesHalfBlockTop() ||  collidesInteractable() ||
                    getY() + getHeight() > cellY * layer.getTileHeight() * 10;

        if ((collisionY)) {
            setY(oldY);
            vel.y = vel.y * -1;
            dir = -dir;
        }
    }

    // This is the pathing method that checks whether the enemy can reach the player from its position.
    protected boolean pathingClear() {
        // Angle between the enemy and Daur from the enemy's perspective.
        double angle = Math.atan2(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2,
                screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);

        // Player is to the right of the enemy.
        if (Math.cos(angle) > 0) {
            for (float x = getX() + getWidth() / 2; x < screen.daur.getX() + screen.daur.getWidth() / 2; x ++) {
                // Player is on top of the enemy.
                if (Math.sin(angle) > 0) {
                    // Gets every point between the enemy and Daur. If any of these points is blocked, returns false.
                    for (float y = getY() + getHeight() / 2; y < screen.daur.getY() + screen.daur.getHeight() / 2; y ++)
                        if (isCellBlocked(x, y) || ((isCellHole(x, y) || isCellWater(x, y) || isCellDeepWater(x, y)) &&
                                grounded))
                            return false;
                }
                // Player is below the enemy.
                else {
                    for (float y = getY() + getHeight() / 2; y > screen.daur.getY() + screen.daur.getHeight() / 2; y --)
                        if (isCellBlocked(x, y) || ((isCellHole(x, y) || isCellWater(x, y) || isCellDeepWater(x, y)) &&
                                grounded))
                            return false;
                }
            }
        }
        // Player is to the left of the enemy.
        else {
            for (float x = getX() + getWidth() / 2; x > screen.daur.getX() + screen.daur.getWidth() / 2; x --) {
                if (Math.sin(angle) > 0) {
                    for (float y = getY() + getHeight() / 2; y < screen.daur.getY() + screen.daur.getHeight() / 2; y++)
                        if (isCellBlocked(x, y) || ((isCellHole(x, y) || isCellWater(x, y) || isCellDeepWater(x, y)) &&
                                grounded))
                            return false;
                } else {
                    for (float y = getY() + getHeight() / 2; y > screen.daur.getY() + screen.daur.getHeight() / 2; y--)
                        if (isCellBlocked(x, y) || ((isCellHole(x, y) || isCellWater(x, y) || isCellDeepWater(x, y)) &&
                                grounded))
                            return false;
                }
            }
        }

        return true;
    }

    // Overrides super methods to prevent falling into a hole. Enemies do not fall into holes unless stunned beforehand
    // and grounded.
    protected boolean collidesRight() {
        for (float step = 0; step < getHeight() - 2; step += layer.getTileHeight() / 16)
            if (isCellBlocked(getX() + getWidth() - 1, getY() + 1 + step) ||
                    isCellPost(getX() + getWidth() - 6, getY() + 1 + step) ||
                    isCellShrub(getX() + getWidth() - 4, getY() + 1 + step) ||
                    ((isCellHole(getX() + getWidth() - 1, getY() + 1 + step) ||
                            isCellWater(getX() + getWidth() - 1, getY() + 1 + step) ||
                            isCellDeepWater(getX() + getWidth() - 1, getY() + 1 + step)) &&
                            !stun && !fallingHole && grounded))
                return true;
        return false;
    }

    protected boolean collidesLeft() {
        for (float step = 0; step < getHeight() - 2; step += layer.getTileHeight() / 16)
            if (isCellBlocked(getX() + 1, getY() + 1 + step) ||
                    isCellPost(getX() + 6, getY() + 1 + step) ||
                    isCellShrub(getX() + 4, getY() + 1 + step) ||
                    ((isCellHole(getX() + 1, getY() + 1 + step) ||
                            isCellWater(getX() + 1, getY() + 1 + step) ||
                            isCellDeepWater(getX() + 1, getY() + 1 + step)) &&
                            !stun && !fallingHole && grounded))
                return true;
        return false;
    }

    protected boolean collidesTop() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16) {
            if (isCellBlocked(getX() + 1 + step, getY() + getHeight() - 1) ||
                    ((isCellHole(getX() + 1 + step, getY() + getHeight() - 1) ||
                            isCellWater(getX() + 1 + step, getY() + getHeight() - 1) ||
                            isCellDeepWater(getX() + 1 + step, getY() + getHeight() - 1)) &&
                            !stun && !fallingHole && grounded))
                return true;
        }
        for (float step = 0; step < getWidth() - 8; step += layer.getTileWidth() / 16)
            if (isCellShrub(getX() + 4 + step, getY() + getHeight() - 1))
                return true;
        for (float step = 0; step < getWidth() - 12; step += layer.getTileWidth() / 16)
            if (isCellPost(getX() + 6 + step, getY() + getHeight() - 1))
                return true;
        return false;
    }

    protected boolean collidesBottom() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellBlocked(getX() + 1 + step, getY() + 1) ||
                    ((isCellHole(getX() + 1 + step, getY() + 1) ||
                            isCellWater(getX() + 1 + step, getY() + 1) ||
                            isCellDeepWater(getX() + 1 + step, getY() + 1)) &&
                            !stun && !fallingHole && grounded))
                return true;
        for (float step = 0; step < getWidth() - 8; step += layer.getTileWidth() / 16)
            if (isCellShrub(getX() + 4 + step, getY() + 1))
                return true;
        for (float step = 0; step < getWidth() - 12; step += layer.getTileWidth() / 16)
            if (isCellPost(getX() + 6 + step, getY() + 1))
                return true;
        return false;
    }

    // Overrides the super method for basic enemy dimensions.
    protected boolean collidesHalfBlockRight() {
        // For half blocks to the left and right.
        for (float step = 0; step < getHeight() - 2; step += layer.getTileHeight() / 16)
            if (isCellHalfBlockedL(getX() + getWidth() - 1, getY() + 1 + step) ||
                    isCellHalfBlockedR(getX() + getWidth() - 8, getY() + 1 + step))
                return true;
        // For half blocks to the top.
        for (float step = 0; step < getHeight() - 10; step += layer.getTileHeight() / 16)
            if (isCellHalfBlockedTL(getX() + getWidth() - 1, getY() + 1 + step) ||
                    isCellHalfBlockedT(getX() + getWidth() - 1, getY() + 1 + step) ||
                    isCellHalfBlockedTR(getX() + getWidth() - 8, getY() + 1 + step))
                return true;
        // For half blocks to the bottom.
        for (float step = 0; step < getHeight() - 10; step += layer.getTileHeight() / 16)
            if (isCellHalfBlockedBL(getX() + getWidth() - 1, getY() + 8 + step) ||
                    isCellHalfBlockedBR(getX() + getWidth() - 8, getY() + 8 + step) ||
                    isCellHalfBlockedB(getX() + getWidth() - 1, getY() + 8 + step))
                return true;
        return false;
    }

    protected boolean collidesHalfBlockLeft() {
        for (float step = 0; step < getHeight() - 2; step += layer.getTileHeight() / 16)
            if (isCellHalfBlockedR(getX() + 1, getY() + 1 + step) ||
                    isCellHalfBlockedL(getX() + 8, getY() + 1 + step))
                return true;
        for (float step = 0; step < getHeight() - 10; step += layer.getTileHeight() / 16)
            if (isCellHalfBlockedTR(getX() + 1, getY() + 1 + step) ||
                    isCellHalfBlockedT(getX() + 1, getY() + 1 + step) ||
                    isCellHalfBlockedTL(getX() + 8, getY() + 1 + step))
                return true;
        for (float step = 0; step < getHeight() - 10; step += layer.getTileHeight() / 16)
            if (isCellHalfBlockedB(getX() + 1, getY() + 8 + step) ||
                    isCellHalfBlockedBR(getX() + 1, getY() + 8 + step) ||
                    isCellHalfBlockedBL(getX() + 8, getY() + 8 + step))
                return true;
        return false;
    }

    protected boolean collidesHalfBlockTop() {
        // For bottom and top.
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellHalfBlockedB(getX() + 1 + step, getY() + getHeight() - 1) ||
                    isCellHalfBlockedT(getX() + 1 + step, getY() + getHeight() - 8))
                return true;
        // For right.
        for (float step = 0; step < getWidth() - 10; step += layer.getTileWidth() / 16)
            if (isCellHalfBlockedR(getX() + 1 + step, getY() + getHeight() - 1) ||
                    isCellHalfBlockedTR(getX() + 1 + step, getY() + getHeight() - 8) ||
                    isCellHalfBlockedBR(getX() + 1 + step, getY() + getHeight() - 1))
                return true;
        // For left.
        for (float step = 0; step < getWidth() - 10; step += layer.getTileWidth() / 16)
            if (isCellHalfBlockedL(getX() + 8 + step, getY() + getHeight() - 1) ||
                    isCellHalfBlockedTL(getX() + 8 + step, getY() + getHeight() - 8) ||
                    isCellHalfBlockedBL(getX() + 8 + step, getY() + getHeight() - 1))
                return true;
        return false;
    }

    protected boolean collidesHalfBlockBottom() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellHalfBlockedB(getX() + 1 + step, getY() + 8) ||
                    isCellHalfBlockedT(getX() + 1 + step, getY() + 1))
                return true;
        for (float step = 0; step < getWidth() - 10; step += layer.getTileWidth() / 16)
            if (isCellHalfBlockedR(getX() + 1 + step, getY() + 1) ||
                    isCellHalfBlockedTR(getX() + 1 + step, getY() + 1) ||
                    isCellHalfBlockedBR(getX() + 1 + step, getY() + 8))
                return true;
        for (float step = 0; step < getWidth() - 10; step += layer.getTileWidth() / 16)
            if (isCellHalfBlockedL(getX() + 8 + step, getY() + 1) ||
                    isCellHalfBlockedTL(getX() + 8 + step, getY() + 1) ||
                    isCellHalfBlockedBL(getX() + 8 + step, getY() + 8))
                return true;
        return false;
    }

    // Overrides the method to prevent unstunning while falling or drowning.
    public void unStun() {
        if (!falling && !drowning)
            stun = false;
    }

    public boolean isGrounded() {
        return grounded;
    }

    protected boolean incapacitated() {
        return (isDead() || stun) && state != FALLING && state != DROWNING;
    }

    // Sets the spawn point for collision purposes.
    public void setSpawn(float spawnX, float spawnY) {
        cellX = (int) (spawnX / (layer.getTileWidth() * 10) + 1);
        cellY = (int) (spawnY / (layer.getTileHeight() * 10) + 1);
        setPosition(spawnX, spawnY);
    }

    public boolean isDead() {
        return state == DEAD;
    }

    protected int getCellX() {
        return cellX;
    }

    protected int getCellY() {
        return cellY;
    }

    protected boolean canMove() {
        return true;
    }

    abstract void tryMove();
}
