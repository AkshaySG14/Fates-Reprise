
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Projectiles.Electrosphere;
import com.inoculates.fatesreprise.Screens.GameScreen;

// JellyFish class that launches electricity at Daur. If killed, splits into two smaller versions of itself that merely
// move around.
public class JellyFish extends Enemy {
    boolean cooldown = true, notDrawn = true;
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("spitterD1"), FD2 = atlas.findRegion("spitterD2"),
            FD3 = atlas.findRegion("spitterD3"), FD4 = atlas.findRegion("spitterD4"),
            FM1 = atlas.findRegion("spitterM1"), FM2 = atlas.findRegion("spitterM2"),
            FM3 = atlas.findRegion("spitterM3"), FM4 = atlas.findRegion("spitterM4");

    float moveTime = 0;

    public JellyFish(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(RUNNING, false);
        grounded = false;
        createAnimations();
        chooseSprite();
    }

    // The Jellyfish is extremely similar to the mud doll, except that it it may shoot Daur at any time, under
    // any condition.
    protected void update(float deltaTime) {
        if (!cooldown)
            shoot();
        if (moveTime > 3) {
            preMove();
            moveTime = 0;
        }
        // If this is the first time updating, set cooldown to false. Otherwise ignore. This is to prevent the jellyfish
        // from discharging too early.
        if (notDrawn) {
            cooldown = false;
            notDrawn = false;
        }
        // Continually adjusts the angle of the Jellyfish.
        setAngle();
    }

    protected void updateTime(float deltaTime) {
            animationTime += deltaTime;
            moveTime += deltaTime;
    }

    // Shoots an electric ball at Daur.
    private void shoot() {
        // Turns yellow to notify the player that this Jellyfish is shooting.
        setColor(Color.YELLOW);
        // Creates an electric sphere with a specific direction.
        float angle = (float) Math.atan2(screen.daur.getCY() - getCY(), screen.daur.getCX() - getCX());
        Electrosphere eSphere = new Electrosphere(screen, map, screen.daurAtlases.get(5), this, angle);
        // Adds the projectile to the render list.
        screen.projectiles.add(eSphere);
        // Sets the cooldown to true, to avoid shooting too many spheres in too short a time.
        cooldown = true;
        // After 0.5 seconds, turns color back to normal.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setColor(Color.WHITE);
            }
        }, 0.5f);
        // Also resets cooldown after five seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cooldown = false;
            }
        }, 5);
        // Plays the shooting sound.
        screen.storage.sounds.get("jellyfishattack2").play(1.0f);
    }

    // Sets the state to running and then after 1.15 seconds begins movement. This is to mimic the movement of an actual
    // jellyfish.
    private void preMove() {
        // Freezes and sets state of Jellyfish.
        freeze();
        setState(RUNNING, false);
        // Creates random direction to move in.
        final int random = (int) (Math.random() * 4);
        // Starts moving after 1.15 seconds. This is to help simulate the movement of the Jellyfish.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                move(random);
            }
        }, 1.15f);
        // Sets direction based on the random integer.
        switch (random) {
            case 0:
                dir = RIGHT;
                break;
            case 1:
                dir = LEFT;
                break;
            case 2:
                dir = UP;
                break;
            case 3:
                dir = DOWN;
                break;
        }
    }

    private void move(int direction) {
        // Must be alive to move (this is due to the delay of this event).
        if (isDead())
            return;
        // Moves the JellyFish in the direction, then stops it after 0.35 seconds.
        switch (direction) {
            case 0:
                SVX(1.5f);
                SVY(0);
                break;
            case 1:
                SVX(-1.5f);
                SVY(0);
                break;
            case 2:
                SVX(0);
                SVY(1.5f);
                break;
            case 3:
                SVX(0);
                SVY(-1.5f);
                break;
        }
        setState(RUNNING, false);
        // Plays the movement sound.
        screen.storage.sounds.get("effect8").play(0.75f);
        // Stops moving after 0.35 seconds. This is to help simulate the movement of the Jellyfish.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                freeze();
                setState(IDLE, false);
                moveTime = 0;
            }
        }, 0.35f);
    }

    // Sets the angle depending on the direction of the Jellyfish.
    private void setAngle() {
        setOriginCenter();
        switch (dir) {
            case 1:
                setRotation(270);
                break;
            case -1:
                setRotation(90);
                break;
            case 2:
                setRotation(0);
                break;
            case -2:
                setRotation(180);
                break;
        }
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    protected void createAnimations() {
        idle = new Animation(0.2f, FD1, FD2, FD3, FD4, FD3, FD2);
        run = new Animation(0.35f, FM1, FM2, FM3, FM4);
    }

    // Overrides super method to spawn two MiniJellyfish.
    protected void death() {
        final Enemy enemy = this;
        if (isDead())
            return;
        setState(DEAD, true);
        fadeAway();
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Creates and moves the mini jellyfish.
                MiniJellyFish mJfish1 = new MiniJellyFish(screen, screen.map, atlas);
                mJfish1.setSpawn(getCX() - mJfish1.getWidth(), getCY() - mJfish1.getHeight() / 2);
                MiniJellyFish mJfish2 = new MiniJellyFish(screen, map, atlas);
                mJfish2.setSpawn(getCX() + mJfish2.getWidth() / 2, getCY() - mJfish2.getHeight() / 2);
                // Adds the mini jellyfish to the rendering list.
                if (map == screen.world1.getMap()) {
                    screen.characters1.add(mJfish1);
                    screen.characters1.add(mJfish2);
                }
                if (map == screen.world2.getMap()) {
                    screen.characters2.add(mJfish1);
                    screen.characters2.add(mJfish2);
                }
                if (map == screen.world3.getMap()) {
                    screen.characters3.add(mJfish1);
                    screen.characters3.add(mJfish2);
                }
                // Plays the fission sound.
                screen.storage.sounds.get("jellyfishattack1").play(1.0f);
                // Removes self as well.
                screen.checkClear(enemy);
                createConsumable();
                removeSelf();
            }
        }, 1);
    }

    protected boolean overrideCheck() {
        return false;
    }

    protected boolean priorities(int cState) {
        return (state == DEAD && cState != FALLING && cState != DROWNING) || state == FALLING || state == DROWNING;
    }

    protected void chooseSprite()
    {
        Animation anim = idle;

        if (state == IDLE || state == DEAD)
            anim = idle;
        if (state == FALLING)
            anim = fall;
        if (state == RUNNING)
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 8 / 9,
                anim.getKeyFrame(animationTime, true).getRegionHeight() * 8 / 9);
    }
}
