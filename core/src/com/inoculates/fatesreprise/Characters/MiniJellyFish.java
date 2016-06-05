
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Projectiles.Electrosphere;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Mini version of the JellyFish class. Does NOT shoot.
public class MiniJellyFish extends Enemy {
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("minispitterD1"), FD2 = atlas.findRegion("minispitterD2"),
            FD3 = atlas.findRegion("minispitterD3"), FD4 = atlas.findRegion("minispitterD4"),
            FM1 = atlas.findRegion("minispitterM1"), FM2 = atlas.findRegion("minispitterM2"),
            FM3 = atlas.findRegion("minispitterM3"), FM4 = atlas.findRegion("minispitterM4");

    float moveTime = 0;

    public MiniJellyFish(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 1);
        setState(RUNNING, false);
        preMove();
        grounded = false;
        createAnimations();
        chooseSprite();
    }

    protected void update(float deltaTime) {
        if (moveTime > 3) {
            preMove();
            moveTime = 0;
        }
        setAngle();
    }

    protected void updateTime(float deltaTime) {
            animationTime += deltaTime;
            moveTime += deltaTime;
    }

    private void preMove() {
        freeze();
        setState(RUNNING, false);
        final int random = (int) (Math.random() * 4);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                move(random);
            }
        }, 1.15f);
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
        if (isDead())
            return;

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
        // Plays the movement sound.
        screen.storage.sounds.get("effect8").play(0.1f);
        setState(RUNNING, false);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                freeze();
                setState(IDLE, false);
                moveTime = 0;
            }
        }, 0.35f);
    }

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
