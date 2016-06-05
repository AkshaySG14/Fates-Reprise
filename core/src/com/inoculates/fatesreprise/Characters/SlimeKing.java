
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Effects.Shadow;
import com.inoculates.fatesreprise.MeleeWeapons.BasicSword;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is a fast-moving enemy that skitters towards Daur to achieve an impact.
public class SlimeKing extends Enemy {
    TextureAtlas.AtlasRegion D1 = atlas.findRegion("slimekingD1"), D2 = atlas.findRegion("slimekingD2");
    float checkTime = 1.5f;
    float spawnTime = 0;

    boolean spawning = true;
    boolean bouncing = false;

    Shadow shadow;

    public SlimeKing(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 12);
        setState(RUNNING, true);
        // Stuns the King Slime to prevent any premature movement.
        stun();
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                spawn();
            }
        }, 0.5f);
        grounded = false;
    }

    // Special animation of the King Slime where he drops down on the floor
    public void spawn() {
        // Creates and then moves the shadow. Also adds to the rendering list.
        shadow = new Shadow(screen, map, screen.miscAtlases.get(1), this, getCX(), getCY(), 1.5f);
        shadow.setPosition(getCX() - shadow.getWidth() / 2, getY() - 100);
        screen.effects.add(shadow);
        // Moves the King Slime downwards.
        vel.y = -2;
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    // Freezes the King Slime, sets spawning to false (allows for collision), and removes the shadow
                    // from the rendering list.
                    freeze();
                    unStun();
                    spawning = false;
                    screen.effects.remove(shadow);
                    // Makes the screen shake.
                    screen.shakeScreen(8, 0.05f, false);
                    // Knocks Daur out shortly.
                    screen.daur.knockOut();
                    // Plays the large bounce sound.
                    storage.sounds.get("bigbounce").play(1.0f);
                    // Starts the miniboss music.
                    screen.storage.music.get("minibossmusic").play();
                    screen.storage.music.get("minibossmusic").setVolume(0.75f);
                    screen.storage.music.get("minibossmusic").setLooping(true);
                }
            }, 0.8f);
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);
        // Checks which direction to charge.
        if (checkTime > 3) {
            checkTime = 0;
            checkCharge();
        }
        // Spawns a slime every 8 cycles.
        if (spawnTime > 8) {
            spawnTime = 0;
            spawnSlime();
        }
        if (bouncing) {
            shadow.setPosition(getCX() - shadow.getWidth() / 2, getY() - 10);
            vel.y -= 0.1f;
        }
    }

    // Gets the angle between the King Slime and then charges accordingly.
    private void checkCharge() {
        double angle = Math.atan2(screen.daur.getCY() - getCY(), screen.daur.getCX() - getCX());
        charge(angle);
    }

    // Charges by setting a high velocity depending on the angle given.
    private void charge(final double angle) {
        // Starts off with a low initial speed.
        vel.x = (float) Math.cos(angle) * 0.5f;
        vel.y = (float) Math.sin(angle) * 0.5f;
        // Plays the charge sound.
        storage.sounds.get("charge").play(1.0f);
        // Speeds up after 0.15 and 0.3 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (isDead())
                    return;
                vel.x = (float) Math.cos(angle);
                vel.y = (float) Math.sin(angle);
            }
        }, 0.15f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (isDead())
                    return;
                vel.x = (float) Math.cos(angle) * 3;
                vel.y = (float) Math.sin(angle) * 3;
            }
        }, 0.4f);
    }

    // When the King Slime hits a wall, he bounces back to his original position.
    private void bounce() {
        // Sets bouncing to true so that the King Slime's velocity is continually decreased, and his shadow follows him.
        bouncing = true;
        // Sets the shadow below him and add it to the rendering list.
        shadow.setPosition(getCX() - shadow.getWidth() / 2, getY() - 10);
        screen.effects.add(shadow);
        // Sets the x velocity to half the opposite of his previous one, so that it appears as though he is bouncing
        // backwards from impact.
        vel.x *= -0.5f;
        vel.y = vel.y * -0.65f + 2;
        // Plays the large bounce sound.
        storage.sounds.get("bigbounce").play(1.0f);
        // After 0.6 seconds, completes the bounce by resetting the slime's velocity, sets bouncing to false, and
        // removes the shadow from the rendering list
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                freeze();
                bouncing = false;
                screen.effects.remove(shadow);
            }
        }, 0.6f);
    }

    // Spawns a slime every 10 seconds by dropping it from a ceiling.
    private void spawnSlime() {
        // Creates and sets the slime to spawning.
        final Slime slime = new Slime(screen, map, atlas);
        final Shadow slimeShadow = new Shadow(screen, map, screen.miscAtlases.get(1), slime, getCX(), getCY(), 0.75f);
        slime.setSpawning(true);
        slime.ignoreCamera();
        // Moves the slime downwards after 0.05 seconds, and adds him to the rendering list.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Stuns and freezes the slime, so as to prevent movement.
                slime.freeze();
                slime.stun();
                // Sets the proper downward projectory,
                slime.SVY(-2f);
                slime.setSpawn(screen.daur.getCX(), screen.daur.getCY());
                slime.setPosition(screen.daur.getCX() - slime.getWidth() / 2, screen.daur.getY() + 100);
                screen.characters2.add(slime);
                // Creates a shadow for the slime.
                slimeShadow.setPosition(screen.daur.getX() + 3 - shadow.getWidth() / 2, screen.daur.getY());
                // Plays the fall sound.
                storage.sounds.get("throw").play(1.0f);
                screen.effects.add(slimeShadow);
            }
        }, 0.05f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Unstuns the slime and removes its shadow. Also tells the game that it can collide.
                slime.unStun();
                slime.freeze();
                screen.effects.remove(slimeShadow);
                slime.setSpawning(false);
                // Plays the landing sound.
                storage.sounds.get("landing").play(1.0f);
            }
        }, 0.9f);
    }

    protected void updateTime(float deltaTime) {
        animationTime += deltaTime;
        checkTime += deltaTime;
        spawnTime += deltaTime;
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    // Overrides super method to bounce upon collision.
    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft() || collidesHalfBlockLeft() || collidesInteractable() ||
                    getX() < (cellX - 1) * layer.getTileWidth() * 10 ;
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesHalfBlockRight() || collidesInteractable() ||
                    getX() + getWidth() > cellX * layer.getTileWidth() * 10;

        if (collisionX && !spawning) {
            setX(oldX);
            if (!bouncing)
                bounce();
        }

        vel.y += ace.y;
        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = collidesBottom() || collidesHalfBlockBottom() || collidesInteractable() ||
                    getY() < (cellY - 1) * layer.getTileHeight() * 10;

        else if (vel.y > 0)
            collisionY = collidesTop() || collidesHalfBlockTop() ||  collidesInteractable() ||
                    getY() + getHeight() > cellY * layer.getTileHeight() * 10;

        if (collisionY && !spawning) {
            setY(oldY);
            if (!bouncing)
                bounce();
        }
    }

    // Overrides the stun collision method to prevent stunning.
    public void stunCollision(Sprite sprite, float time) {

    }

    // Overrides the enemy collision method to only be damaged by Daur's sword. Also does not move or stun the King Slime.
    public void damageCollision(Sprite sprite, int dmg) {
        if (armor >= dmg || invulnerability || isDead() || !(sprite instanceof BasicSword))
            return;
        loseHealth(dmg);
    }

    // Overrides method to play different hurt sound.
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

            if (health == 0)
                death();
            else
                storage.sounds.get("bosshurt").play(0.75f);
            }
        }

    // Overrides death animation and also creates the chest that bestows Zephyr's Wisp.
    protected void death() {
        freeze();
        stun();

        final Enemy enemy = this;
        if (isDead())
            return;
        setState(DEAD, true);
        // Makes the King Slime slightly transparent.
        setAlpha(0.5f);
        // Plays death sound.
        storage.sounds.get("bossdeath").play(1.0f);
        // After two seconds performs the death animation.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                selfDestruct();
            }
        }, 2);
        // Clears self away after four seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Checks if this enemy a part of a triggering event.
                screen.checkClear(enemy);
                // Plays explosion sound.
                storage.sounds.get("boom1").play(1.0f);
                // Removes self from game.
                removeSelf();
            }
        }, 4);
    }

    // Note that all this method does is blink invert and revert the King Slime to create a different death animation.
    private void selfDestruct() {
        for (float time = 0.1f; time <= 2; time += 0.2f)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    destructing = true;
                    // Mini explosion sound.
                    storage.sounds.get("bossminiexplosion").play(1.0f);
                }
            }, time);
        for (float time = 0.2f; time <= 2; time += 0.2f)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    destructing = false;
                }
            }, time);
    }

    protected void createAnimations() {
        idle = new Animation(0.5f, D1);
        run = new Animation(0.5f, D1, D2);
    }

    protected boolean overrideCheck() {
        return state == DEAD;
    }

    protected boolean priorities(int cState) {
        return state == DEAD;
    }

    protected void chooseSprite()
    {
        Animation anim = run;
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 7 / 8, anim.getKeyFrame(animationTime, true).getRegionHeight() * 7 / 8);
    }


}
