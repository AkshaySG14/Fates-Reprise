
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Effects.EnemyDeath;
import com.inoculates.fatesreprise.Effects.Grass;
import com.inoculates.fatesreprise.Effects.Ripple;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Enemy superclass that closely resembles the Daur class.
public abstract class Enemy extends Character {
    protected static final int ATTACKING = 3, RUNNING = 4;

    protected static final float JUMP_VELOCITY = 2.8f;

    protected Animation idle, run, attack, runAttack, jump;

    protected int health;
    protected boolean attacking = false;
    protected boolean invulnerability = false;

    protected boolean slowed = false;

    protected Grass grass;
    protected Ripple ripple;

    private int cellX, cellY;

    // Constructor if the enemy has armor.
    public Enemy(GameScreen screen, TiledMap map, TextureAtlas atlas, int health) {
        super(screen, map, atlas, screen.storage);
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        grass = new Grass(screen, map, screen.daurAtlases.get(3), this);
        ripple = new Ripple(screen, map, screen.daurAtlases.get(3), this);
        this.health = health;
    }

    // If the enemy does not.
    public Enemy(GameScreen screen, TiledMap map, TextureAtlas atlas, int health, int armor) {
        super(screen, map, atlas, screen.storage);
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        grass = new Grass(screen, map, screen.daurAtlases.get(3), this);
        ripple = new Ripple(screen, map, screen.daurAtlases.get(3), this);
        this.health = health;
        this.armor = armor;
    }

    protected void periodicCheck(float deltaTime) {
        if (!incapacitated()) {
            updateTime(deltaTime);
            update(deltaTime);
        }
        if ((vel.x != 0 || vel.y != 0) && canMove())
            tryMove();

        createAnimations();
        chooseSprite();
    }

    protected void setState(int cState, boolean override) {
        if (cState == state || (!override && overrideCheck()) || (override && priorities(cState)))
            return;

        state = cState;
        if (state != IDLE)
            animationTime = 0;
    }

    public void loseHealth(int h) {
        if (!invulnerability) {
            health -= (h - armor);
            invulnerability = true;
            flickerSprite();
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    invulnerability = false;
                    inverted = false;
                }
            }, 0.6f);
            timer.start();

            if (health == 0)
                death();
        }
    }

    // If the enemy is hit by a sword or by another damaging force.
    public void damageCollision(Sprite sprite, int dmg) {
        if (armor >= dmg || invulnerability)
            return;

        float angle = (float) Math.atan2(getY() - sprite.getY(), getX() - sprite.getX());
        vel.x = (float) (4 * Math.cos(angle));
        vel.y = (float) (4 * Math.sin(angle));
        stun();
        loseHealth(dmg);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
                unStun();
            }
        }, 0.1f);
        timer.start();
    }

    // This is if the enemy is hit by an action that stuns but does NOT damage. For example, if hit by a concussive shot.
    // Note the lack of lose health.
    public void stunCollision(Sprite sprite, float time) {
        float angle = (float) Math.atan2(getY() - sprite.getY(), getX() - sprite.getX());
        vel.x = (float) (4 * Math.cos(angle));
        vel.y = (float) (4 * Math.sin(angle));
        stun();
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
            }
        }, 0.1f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
            }
        }, time);
        timer.start();
    }

    private void flickerSprite() {
        inverted = true;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.2f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = true;
            }
        }, 0.4f);
        timer.start();
    }

    // Sets its state to dead, and removes itself and all effects.
    protected void death() {
        // If already dead, no need to die twice.
        if (isDead())
            return;
        // Sets state to dead.
        setState(DEAD, true);
        // Creates the red death animation.
        EnemyDeath death = new EnemyDeath(screen, map, screen.daurAtlases.get(3), this);
        // Sets the animation's position to the enemy's position.
        death.setPosition(getX() + getWidth() / 2 - death.getWidth() / 2, getY() + getHeight() / 2 - death.getHeight() / 2);
        // Adds the death animation to the effect rendering list.
        screen.effects.add(death);
        // Removes the sprite and all effects after one second.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                removeSelf();
            }
        }, 1f);
        timer.start();
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

    protected void detectConditions() {
        if (detectSlow()) {
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

        if (detectShallowWater()) {
            if (!screen.effects.contains(ripple)) {
                screen.effects.add(ripple);
                ripple.setPosition(getX(), getY());
                ripple.setAnimationTime(animationTime);
            }
        }

        else if (screen.effects.contains(ripple))
            screen.effects.remove(ripple);
    }

    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        // Note that the enemy colliding with the edge of the current cell is counted as a collision. Meaning that the
        // enemy cannot wander farther than the edge of te cell.
        if (vel.x < 0)
            collisionX = collidesLeft() || getX() < (cellX - 1) * layer.getTileWidth() * 10 || collidesInteractable();
        else if (vel.x > 0)
            collisionX = collidesRight() || getX() + getWidth() > cellX * layer.getTileWidth() * 10 || collidesInteractable();

        if (collisionX) {
            setX(oldX);
            vel.x = vel.x * -1;
            dir = -dir;
        }

        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = collidesBottom() || getY() < (cellY - 1) * layer.getTileHeight() * 10 || collidesInteractable();

        else if (vel.y > 0)
            collisionY = collidesTop() || getY() + getHeight() > cellY * layer.getTileHeight() * 10 - 16 || collidesInteractable();

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
                        if (isCellBlocked(x, y))
                            return false;
                }
                // Player is below the enemy.
                else {
                    for (float y = getY() + getHeight() / 2; y > screen.daur.getY() + screen.daur.getHeight() / 2; y --)
                        if (isCellBlocked(x, y))
                            return false;
                }
            }
        }
        // Player is to the left of the enemy.
        else {
            for (float x = getX() + getWidth() / 2; x > screen.daur.getX() + screen.daur.getWidth() / 2; x --) {
                if (Math.sin(angle) > 0) {
                    for (float y = getY() + getHeight() / 2; y < screen.daur.getY() + screen.daur.getHeight() / 2; y++)
                        if (isCellBlocked(x, y))
                            return false;
                } else {
                    for (float y = getY() + getHeight() / 2; y > screen.daur.getY() + screen.daur.getHeight() / 2; y--)
                        if (isCellBlocked(x, y))
                            return false;
                }
            }
        }

        return true;
    }

    protected boolean incapacitated() {
        return isDead() || stun;
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
