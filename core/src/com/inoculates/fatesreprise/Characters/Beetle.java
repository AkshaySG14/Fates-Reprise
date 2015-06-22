
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;

// The beetle enemy that is found in certain parts of the game.
public class Beetle extends Enemy {
    // Loads the texture regions that comprise the beetle's animations.
    TextureAtlas.AtlasRegion FU1 = atlas.findRegion("beetleU1"), FU2 = atlas.findRegion("beetleU2"),
            FD1 = atlas.findRegion("beetleD1"), FD2 = atlas.findRegion("beetleD2"), FR1 = atlas.findRegion("beetleR1"),
            FR2 = atlas.findRegion("beetleR2"), FL1 = atlas.findRegion("beetleL1"), FL2 = atlas.findRegion("beetleL2");

    // The floats that register when the beetle shall move.
    float moveTime = 0;
    float checkTime = 0;

    // Direction of movement.
    int movementDirection;

    // Class constructor that captures the values of the game screen, the map the beetle is on, and the beetle's texture
    // atlas (its texture dictionary). Also begins movement immediately.
    public Beetle(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        checkMove();
        move();
    }

    // This method checks whether the beetle is not moving (thus changing its state to idle), and also checks whether
    // the beetle needs to move.
    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);
        if (checkTime > 1)
            checkMove();
        if (moveTime > 1.5f)
            move();
    }

    // Updates the time in accordance with how much time has passed according to Gdx.getDeltaTime().
    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        moveTime += deltaTime;
        checkTime += deltaTime;
    }

    // Randomly picks a path to move. This occurs every ONE second.
    private void checkMove() {
        // Gets a random integer from one to four.
        int random = (int) (Math.random() * 5);
        // If the integer is zero, there is no movement. Thus there is a 25% chance of no movement, and a 75% chance of
        // movement.
        if (random == 0) {
            vel.x = 0;
            vel.y = 0;
            state = IDLE;
        }
        else {
            // Switch and case expression which checks the movement
            switch (movementDirection) {
                // If the movement is set to left, sets x component of velocity to 0.5, and the y component to zero.
                case 0:
                    vel.x = 0.5f;
                    vel.y = 0;
                    dir = RIGHT;
                    break;
                // If left, x = -0.5 and y = 0.
                case 1:
                    vel.x = -0.5f;
                    vel.y = 0;
                    dir = LEFT;
                    break;
                // If up, x = 0 and y = 0.5.
                case 2:
                    vel.x = 0;
                    vel.y = 0.5f;
                    dir = UP;
                    break;
                // If down, x = 0 and y = -0.5.
                case 3:
                    vel.x = 0;
                    vel.y = -0.5f;
                    dir = DOWN;
                    break;
            }
            // Sets state to running, as the beetle is now moving.
            state = RUNNING;
        }
        // Resets the check time.
        checkTime = 0;
    }

    // Sets the movement direction of the beetle every 1.5 seconds, regardless of whether the beetle is moving or not.
    private void move() {
        // Sets the movement direction to a number between one and four. The reason the random integer is generated this
        // way is to ensure more randomness.
        movementDirection = ((int) (Math.random() * 16)) / 4;
        // Resets move time.
        moveTime = 0;
    }

    // This method moves the beetle depending on the x and y components of its velocity. Additionally, it checks for any
    // collisions, including those with the edge of the screen. The actual methods are in the super class Enemy.
    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    //Creates all the animations of the beetle with their corresponding frames.
    protected void createAnimations() {
        // If facing up, makes the run and idle animations consist of the up-facing frames.
        if (dir == UP) {
            run = new Animation(0.25f, FU1, FU2);
            idle = new Animation(0.5f, FU1);
        }
        // Same but for the bottom direction.
        else if (dir == DOWN) {
            run = new Animation(0.25f, FD1, FD2);
            idle = new Animation(0.5f, FD1);
        }
        // Etc.
        else if (dir == RIGHT) {
            run = new Animation(0.25f, FR1, FR2);
            idle = new Animation(0.5f, FR1);
        }
        else if (dir == LEFT) {
            run = new Animation(0.25f, FL1, FL2);
            idle = new Animation(0.5f, FL1);
        }
    }

    // If returned true, the new state will not override the old.
    protected boolean overrideCheck() {
        return state == DEAD;
    }

    // If returned true, the current state has priority over the new one.
    protected boolean priorities(int cState) {
        return state == DEAD;
    }

    //This method periodically sets the frame of the beetle depending on both the state and the animationTime.
    protected void chooseSprite()
    {
        Animation anim = idle;

        if (state == IDLE || state == DEAD)
            anim = idle;
        if (state == RUNNING)
            anim = run;

        // Sets the actual frame.
        setRegion(anim.getKeyFrame(animationTime, true));
        // Sets the size of the beetle depending on the animation (to avoid having the same bounding rectangle).
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
