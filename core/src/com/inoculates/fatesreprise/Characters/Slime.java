
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is a fast-moving enemy that skitters towards Daur to achieve an impact.
public class Slime extends Enemy {
    TextureAtlas.AtlasRegion D1 = atlas.findRegion("slimeD1"), D2 = atlas.findRegion("slimeD2");
    float moveTime = 0;
    float checkTime = 0;

    int movementDirection;
    boolean charging = false;
    boolean cooldown = false;

    public Slime(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(RUNNING, true);
        checkMove();
        move();
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);
        // Checks which direction to charge.
        if (!cooldown && pathingClear())
            decideMove(inRange());
        // Moves normally.
        if (checkTime > 1 && !charging)
            checkMove();
        if (moveTime > 1.5f && !charging)
            move();
    }

    // If the slime can charge in a direction, does so.
    private void decideMove(int decision) {
        if (decision > 0 && !charging)
            charge(decision);
    }

    // Checks if Daur is within a certain range of the slime.
    private int inRange() {
        if (Math.abs(getY() + getHeight() / 2 - screen.daur.getY() - screen.daur.getHeight() / 2) < getHeight())
            return 1;
        if (Math.abs(getX() + getWidth() / 2 - screen.daur.getX() - screen.daur.getWidth() / 2) < getWidth())
            return 2;
        return 0;
    }

    private void checkMove() {
        int random = (int) (Math.random() * 5);
        if (random == 0) {
            vel.x = 0;
            vel.y = 0;
            state = IDLE;
        }
        else {
            switch (movementDirection) {
                case 0:
                    vel.x = 0.5f;
                    vel.y = 0;
                    dir = RIGHT;
                    break;
                case 1:
                    vel.x = -0.5f;
                    vel.y = 0;
                    dir = LEFT;
                    break;
                case 2:
                    vel.x = 0;
                    vel.y = 0.5f;
                    dir = UP;
                    break;
                case 3:
                    vel.x = 0;
                    vel.y = -0.5f;
                    dir = DOWN;
                    break;
            }
            state = RUNNING;
        }
        checkTime = 0;
    }

    private void move() {
        movementDirection = ((int) (Math.random() * 16)) / 4;
        moveTime = 0;
    }

    // Charges by setting a high velocity depending on the direction.
    private void charge(int direction) {
        // If the slime is facing left or right.
        if (direction == 1) {
            // Sets velocity in accordance with whether Daur is to the right or the left of the slime.
            vel.x = 3 * Math.signum(screen.daur.getX() - getX());
            vel.y = 0;
        }
        // Otherwise if the slime is facing up or down.
        else {
            // Same but for up or down.
            vel.x = 0;
            vel.y = 3 * Math.signum(screen.daur.getY() - getY());
        }

        // Sets cooldown to avoid repeated charging.
        cooldown = true;
        // Informs the game the slime is charging to avoid the slime being called to walk normally.
        charging = true;
        // Sets charging false and immobilizes the slime.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                resetCharge();
            }
        }, 0.7f);
        timer.start();
    }

    // Resets the charge to make the slime resume normal status.
    private void resetCharge() {
        // Sets charging false and makes the slime's velocity zero.
        charging = false;
        vel.x = 0;
        vel.y = 0;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Resets cooldown after two seconds, to allow the slime to charge again.
                cooldown = false;
            }
        }, 2);
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
        idle = new Animation(0.5f, D1);
        run = new Animation(0.5f, D1, D2);
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
        Animation anim = idle;
        if (state == IDLE || isDead())
            anim = idle;
        else
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 7 / 8, anim.getKeyFrame(animationTime, true).getRegionHeight() * 7 / 8);
    }
}
