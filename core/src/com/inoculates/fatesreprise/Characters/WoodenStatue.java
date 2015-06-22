
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;

public class WoodenStatue extends Enemy {
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("woodenknightD1"), FD2 = atlas.findRegion("woodenknightD2");
    float moveTime = 0;
    float checkTime = 0;

    boolean following = false;

    public WoodenStatue(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 3, 0);
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);

        if (checkTime > 1 && !isDead() && !following)
            checkFollow();
        if (!isDead() && following)
            follow();
    }

    // Checks if Daur is close enough to follow.
    private void checkFollow() {
        // Gets the distance components for the distance between the statue and the Daur.
        float dX = Math.abs(screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
        float dY = Math.abs(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2);
        // Gets the distance itself by using the square root method.
        float distance = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY,2));

        // If the TOTAL distance is less than three times the width, begins following.
        if (distance < getWidth() * 3) {
            // Starts to follow and resets after 5 seconds (stops following).
            following = true;
            reset();
            return;
        }


        following = false;
    }

    // Same as the pantomime method.
    private void follow() {
        float angle = (float) Math.atan2(screen.daur.getY() + getHeight() / 2 - (getY() + getHeight() / 2),
                screen.daur.getX() + getWidth() / 2 - (getX() + getWidth() / 2));
        vel.x = (float) Math.cos(angle) / 2;
        vel.y = (float) Math.sin(angle) / 2;

        if (Math.abs(vel.x) > Math.abs(vel.y)) {
            if (vel.x > 0)
                dir = RIGHT;
            else
                dir = LEFT;
        }
        else {
            if (vel.y > 0)
                dir = UP;
            else
                dir = DOWN;
        }

    }

    // Sets following false, and sets state back to normal.
    private void reset() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (isDead())
                    return;
                following = false;
                vel.x = 0;
                vel.y = 0;
                checkTime = 0;
            }
        }, 5);
        timer.start();
    }

    public void damageCollision(Sprite sprite, int dmg) {
        if (armor >= dmg)
            return;

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
    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        moveTime += deltaTime;
        checkTime += deltaTime;
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    protected void createAnimations() {
        idle = new Animation(0.5f, FD1);
        run = new Animation(0.25f, FD1, FD2);
    }

    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft() || getX() < (getCellX() - 1) * layer.getTileWidth() * 10;
        else if (vel.x > 0)
            collisionX = collidesRight() || getX() + getWidth() > getCellX() * layer.getTileWidth() * 10;

        if (collisionX) {
            setX(oldX);
            vel.x = 0;
            vel.y = 0;
        }

        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = collidesBottom() || getY() < (getCellY() - 1) * layer.getTileHeight() * 10;

        else if (vel.y > 0)
            collisionY = collidesTop() || getY() + getHeight() > getCellY() * layer.getTileHeight() * 10;

        if ((collisionY)) {
            setY(oldY);
            vel.x = 0;
            vel.y = 0;
        }
    }

    protected boolean overrideCheck() {
        return state == DEAD;
    }

    protected boolean priorities(int cState) {
        return state == DEAD;
    }

    protected void chooseSprite()
    {
        Animation anim;
        if (state == IDLE)
            anim = idle;
        else
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
