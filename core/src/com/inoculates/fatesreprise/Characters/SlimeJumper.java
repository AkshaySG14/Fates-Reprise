
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Effects.Shadow;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is an aggressive enemy that will constantly jump at Daur in an attempt to collide with him..
public class SlimeJumper extends Enemy {
    protected static final int JUMPING = 6, FLYING = 7;

    TextureAtlas.AtlasRegion D1 = atlas.findRegion("slimejumperD1"), D2 = atlas.findRegion("slimejumperD2"),
            J1 = atlas.findRegion("slimejumperJ1"), J2 = atlas.findRegion("slimejumperJ2"),
            F1 = atlas.findRegion("slimejumperFL1"), F2 = atlas.findRegion("slimejumperFL2");

    float jumpTime = 0;

    private Animation jump, fly;
    private Shadow shadow;

    public SlimeJumper(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(IDLE, true);
        shadow = new Shadow(screen, map, screen.miscAtlases.get(1), this, getX(), getY(), 0.6f);
    }

    protected void update(float deltaTime) {
        // Checks to jump every one second.
        if (jumpTime > 1) {
            checkJump();
            jumpTime = 0;
        }
        // Ensures the shadow is under the jumper if in the air. Also reduces the y-velocity of the jumper over time
        // to simulate jumping.
        if (state == FLYING) {
            shadow.setPosition(getCX() - shadow.getWidth() / 2, getCY() - 10 - shadow.getHeight());
            vel.y -= 0.1f;
        }
    }

    protected void updateTime(float deltaTime) {
        animationTime += deltaTime;
        if (state == IDLE)
            jumpTime += deltaTime;
    }

    private void checkJump() {
        // If Daur is not too far away, the jumper will continually jump at him.
        if (getDistance(getCX(), getCY(), screen.daur.getCX(), screen.daur.getCY()) < 80) {
            setState(JUMPING, true);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (!isDead()) {
                        setState(FLYING, false);
                        jump();
                    }
                }
            }, 0.5f);
        }
    }

    // Jumps at Daur, creating a shadow beneath to simulate jumping.
    private void jump() {
        screen.effects.add(shadow);
        double angle = Math.atan2(screen.daur.getCY() - getCY(), screen.daur.getCX() - getCX());
        // Makes the jumper travel towards Daur by using the sine and cosine of the angle between them. Adds a bit of
        // y-velocity to help with jumping.
        SVX((float) Math.cos(angle));
        SVY((float) Math.sin(angle) + 2);
        // No matter how far the jumper has gone, after 0.75 seconds, stops jumping.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.effects.remove(shadow);
            }
        }, 0.6f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                freeze();
                setState(IDLE, true);
            }
        }, 0.75f);
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    protected void createAnimations() {
        idle = new Animation(0.5f, D1, D2);
        jump = new Animation(0.25f, J1, J2);
        fly = new Animation(0.25f, F1, F2);
    }

    protected boolean overrideCheck() {
        return state == DEAD;
    }

    protected boolean priorities(int cState) {
        return state == DEAD;
    }

    protected void chooseSprite() {
        Animation anim = idle;
        if (state == IDLE || isDead())
            anim = idle;
        else if (state == FALLING)
            anim = fall;
        else if (state == DROWNING)
            anim = drown;
        else if (state == JUMPING)
            anim = jump;
        else if (state == FLYING)
            anim = fly;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(),
                anim.getKeyFrame(animationTime, true).getRegionHeight());
    }

    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        // Accelerates the enemy accordingly.
        vel.x += ace.x;

        setX(getX() + vel.x);

        // Note that the enemy colliding with the edge of the current cell is counted as a collision. Meaning that the
        // enemy cannot wander farther than the edge of te cell.
        if (vel.x < 0)
            collisionX = getX() < (cellX - 1) * layer.getTileWidth() * 10 ;
        else if (vel.x > 0)
            collisionX = getX() + getWidth() > cellX * layer.getTileWidth() * 10;

        if (collisionX) {
            setX(oldX);
            vel.x = vel.x * -1;
            dir = -dir;
        }

        // Accelerates the enemy accordingly.
        vel.y += ace.y;

        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = getY() < (cellY - 1) * layer.getTileHeight() * 10;

        else if (vel.y > 0)
            collisionY = getY() + getHeight() > cellY * layer.getTileHeight() * 10;

        if ((collisionY)) {
            setY(oldY);
            vel.y = vel.y * -1;
            dir = -dir;
        }
    }
}
