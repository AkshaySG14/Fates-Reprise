
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is an enemy class that follows Daur, damaging him via collision.
public class Pantomime extends Enemy {
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("pantomimeD1"), FD2 = atlas.findRegion("pantomimeD2"),
            FR1 = atlas.findRegion("pantomimeR1"), FR2 = atlas.findRegion("pantomimeR2"), FL1 = atlas.findRegion("pantomimeL1"),
            FL2 = atlas.findRegion("pantomimeL2"), FU1 = atlas.findRegion("pantomimeU1"), FU2 = atlas.findRegion("pantomimeU2");
    float moveTime = 0;
    float checkTime = 0;

    int movementDirection;
    boolean following = false;

    public Pantomime(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(RUNNING, true);
        checkMove();
        move();
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);
        if (checkTime > 1) {
            if (!following)
                checkMove();
            // Checks if the pantomime should follow Daur (pathing is clear).
            if (pathingClear())
                checkFollow();
            else
                following = false;
        }
        if (moveTime > 1.5f && !following)
            move();
        // Follows Daur, attempting to bump into him.
        if (!isDead() && following)
            follow();
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

    // Checks if the player is inside a rectangle of vision, which depends on the direction the pantomime is facing.
    private void checkFollow() {
        // Gets the distance components between the pantomime and Daur.
        float dX = screen.daur.getX() + screen.daur.getWidth() / 2;
        float dY = screen.daur.getY() + screen.daur.getHeight() / 2;

        // If the pantomime is looking up or down.
        if (dir == UP || dir == DOWN)
            // This creates a rectangle base by using the pantomime width.
            for (float x1 = getX(); x1 > getX() - getWidth() * 4; x1 --)
                for (float x2 = getX() + getWidth(); x2 < getX() + getWidth() * 5; x2 ++) {
                    // If the x-point is within the rectangle.
                    if (dX > x1 && dX < x2) {
                        // If facing up and Daur is above the pantomime.
                        if (dir == UP && dY > getY() + getHeight()) {
                            following = true;
                            state = RUNNING;
                            return;
                        }
                        // If facing down and Daur is below the pantomime.
                        else if (dir == DOWN && dY < getY()) {
                            following = true;
                            state = RUNNING;
                            return;
                        }
                    }
                }

        // If pantomime is looking left or right.
        if (dir == RIGHT || dir == LEFT)
            // This creates a rectangle base by using the pantomime height.
            for (float y1 = getY(); y1 > getY() - getHeight() * 4; y1 --)
                for (float y2 = getY() + getHeight(); y2 < getY() + getHeight() * 5; y2 ++) {
                    // If the y-point is within the rectangle.
                    if (dY > y1 && dY < y2) {
                        // If facing to the right and Daur is to the right of the pantomime.
                        if (dir == RIGHT && dX > getX() + getWidth()) {
                            following = true;
                            state = RUNNING;
                            return;
                        }
                        // If facing to the left and Daur is to the left of the pantomime.
                        else if (dir == LEFT && dX < getX()) {
                            following = true;
                            state = RUNNING;
                            return;
                        }
                    }
                }

        following = false;
    }

    // Constantly acquires an angle and uses it to set the velocity.
    private void follow() {
        // Gets angle between the pantomime and Daur.
        float angle = (float) Math.atan2(screen.daur.getY() + getHeight() / 2 - (getY() + getHeight() / 2),
                screen.daur.getX() + getWidth() / 2 - (getX() + getWidth() / 2));
        // Sets the velocity based on the cosine and sine of the angle.
        vel.x = (float) Math.cos(angle) / 2;
        vel.y = (float) Math.sin(angle) / 2;

        // Sets the direction based on velocity of the pantomime.
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

    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        moveTime += deltaTime;
        checkTime += deltaTime;
    }

    //This method moves the pixel knight through both x and y velocities.
    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    //Creates all the animations of the knight with their corresponding frames.
    protected void createAnimations() {
        if (dir == UP) {
            idle = new Animation(0.5f, FU1);
            run = new Animation(0.25f, FU1, FU2);
        }
        else if (dir == DOWN) {
            idle = new Animation(0.5f, FD1);
            run = new Animation(0.25f, FD1, FD2);
        }
        else if (dir == RIGHT) {
            idle = new Animation(0.5f, FR1);
            run = new Animation(0.25f, FR1, FR2);
        }
        else if (dir == LEFT) {
            idle = new Animation(0.5f, FL1);
            run = new Animation(0.25f, FL1, FL2);
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

        if (state == IDLE || isDead())
            anim = idle;
        else
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
