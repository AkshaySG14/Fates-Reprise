
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
        super(screen, map, atlas, 4, 0);
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);

        if (checkTime > 1 && !isDead() && !following) {
            checkFollow();
            checkTime = 0;
        }
        if (!isDead() && following) {
            follow();
            checkContinue();
        }
    }

    // Checks if Daur is close enough to follow.
    private void checkFollow() {
        // Gets the distance components for the distance between the statue and the Daur.
        float dX = Math.abs(screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
        float dY = Math.abs(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2);
        // Gets the distance itself by using the square root method.
        float distance = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY,2));

        // If the TOTAL distance is less than 1.2 times the width, begins following.
        if (distance < getWidth() * 1.5f) {
            // Starts to follow.
            following = true;
            return;
        }

        following = false;
    }

    // Checks if Daur is close enough to CONTINUE to follow.
    private void checkContinue() {
        // Resets if Daur is too far away.
        if (getDistance(getCX(), getCY(), screen.daur.getCX(), screen.daur.getCY()) > getWidth() * 4)
            reset();
    }

    // Same as the pantomime method.
    private void follow() {
        // Continuously sets state to running.
        setState(RUNNING, true);
        float angle = (float) Math.atan2(screen.daur.getY() + getHeight() / 2 - (getY() + getHeight() / 2),
                screen.daur.getX() + getWidth() / 2 - (getX() + getWidth() / 2));
        SVX((float) Math.cos(angle) / 2);
        SVY((float) Math.sin(angle) / 2);

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
        following = false;
        freeze();
        checkTime = 0;
    }

    // Overrides super method to prevent being moved and to follow immediately after being attacked.
    public void damageCollision(Sprite sprite, int dmg) {
        if (armor >= dmg || isDead())
            return;

        loseHealth(dmg);

        // If not dead start following.
        if (!isDead()) {
            unStun();
            following = true;
            follow();
        }
        // Stun and immobilize if dead.
        else {
            stun();
            freeze();
        }
    }

    // Overridden for the same reason as the above method.
    public void stunCollision(Sprite sprite, float time) {
        if (invulnerability || transparent || isDead())
            return;

        stun();
        vel.x = 0;
        vel.y = 0;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
            }
        }, time);
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
        else if (state == FALLING)
            anim = fall;
        else if (state == DROWNING)
            anim = drown;
        else
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
