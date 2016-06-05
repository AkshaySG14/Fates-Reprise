
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
    boolean spawning = false;

    public Slime(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 1);
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
        if (decision > 0 && !charging && pathingClear())
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
                    SVX(0.5f);
                    SVY(0);
                    dir = RIGHT;
                    break;
                case 1:
                    SVX(-0.5f);
                    SVY(0);
                    dir = LEFT;
                    break;
                case 2:
                    SVX(0);
                    SVY(0.5f);
                    dir = UP;
                    break;
                case 3:
                    SVX(0);
                    SVY(-0.5f);
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
            SVX(3 * Math.signum(screen.daur.getX() - getX()));
            vel.y = 0;
        }
        // Otherwise if the slime is facing up or down.
        else {
            // Same but for up or down.
            vel.x = 0;
            SVY(3 * Math.signum(screen.daur.getY() - getY()));
        }

        // Sets cooldown to avoid repeated charging.
        cooldown = true;
        // Informs the game the slime is charging to avoid the slime being called to walk normally.
        charging = true;
        // Sets charging false and immobilizes the slime.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                resetCharge();
            }
        }, 0.7f);
        // Plays a charge sound.
        storage.sounds.get("effect6").play(1.0f);
    }

    // Resets the charge to make the slime resume normal status.
    private void resetCharge() {
        // Sets charging false and makes the slime's velocity zero.
        charging = false;
        freeze();
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Resets cooldown after two seconds, to allow the slime to charge again.
                cooldown = false;
            }
        }, 4);
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

    // Overrides super method to cancel charge upon collision. Also, while spawning, cannot collide.
    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        // Accelerates the enemy accordingly.
        vel.x += ace.x;

        setX(getX() + vel.x);

        // Note that the enemy colliding with the edge of the current cell is counted as a collision. Meaning that the
        // enemy cannot wander farther than the edge of te cell.
        if (vel.x < 0)
            collisionX = collidesLeft() || collidesHalfBlockLeft() || collidesInteractable() ||
                    getX() < (cellX - 1) * layer.getTileWidth() * 10 ;
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesHalfBlockRight() || collidesInteractable() ||
                    getX() + getWidth() > cellX * layer.getTileWidth() * 10;

        if (collisionX && !spawning) {
            setX(oldX);
            if (!charging) {
                vel.x = vel.x * -1;
                dir = -dir;
            }
            else
                resetCharge();
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

        if (collisionY && !spawning) {
            setY(oldY);
            if (!charging) {
                vel.y = vel.y * -1;
                dir = -dir;
            }
            else
                resetCharge();
        }
    }

    protected void createAnimations() {
        idle = new Animation(0.5f, D1);
        run = new Animation(0.5f, D1, D2);
    }

    protected boolean overrideCheck() {
        return state == DEAD;
    }

    protected boolean priorities(int cState) {
        return (state == DEAD && cState != FALLING && cState != DROWNING) || state == FALLING || state == DROWNING;
    }

    protected void chooseSprite()
    {
        Animation anim = idle;
        if (state == IDLE || isDead())
            anim = idle;
        else if (state == FALLING)
            anim = fall;
        else if (state == DROWNING)
            anim = drown;
        else
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 7 / 8, anim.getKeyFrame(animationTime, true).getRegionHeight() * 7 / 8);
    }

    public void setSpawning(boolean spawning) {
        this.spawning = spawning;
        grounded = !spawning;
    }
}
