
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;

import java.awt.*;

// This is an enemy class that upon being seen, will dash away.
public class PlantStalker extends Enemy {
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("plantstalkerD1"), FD2 = atlas.findRegion("plantstalkerD2"),
    FD3 = atlas.findRegion("plantstalkerD3"), FR1 = atlas.findRegion("plantstalkerR1"),
            FR2 = atlas.findRegion("plantstalkerR2"), FR3 = atlas.findRegion("plantstalkerR3"),
            FL1 = atlas.findRegion("plantstalkerL1"), FL2 = atlas.findRegion("plantstalkerL2"), FL3 = atlas.findRegion("plantstalkerL3"),
            FU1 = atlas.findRegion("plantstalkerU1"), FU2 = atlas.findRegion("plantstalkerU2"), FU3 = atlas.findRegion("plantstalkerU3");
    float moveTime = 0;
    float checkTime = 0;

    int movementDirection;
    boolean running = false;

    // The point to which the plant stalker runs.
    Point runningPoint;

    public PlantStalker(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(RUNNING, true);
        checkMove();
        move();
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);
        // Every one second, changes movement.
        if (checkTime > 1) {
            if (!running) {
                checkMove();
                checkRun();
            }
            checkTime = 0;
        }
        // Moves in a random direction, or not at all.
        if (moveTime > 1.5f && !running)
            move();
        // Attempts to scurry away from Daur, maybe hitting him in the process.
        if (!isDead() && running)
            run();
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
    }

    private void move() {
        movementDirection = ((int) (Math.random() * 16)) / 4;
        moveTime = 0;
    }

    // Checks if the stalker is inside a rectangle of vision, which depends on the direction Daur is facing.
    private void checkRun() {
        // Gets the center of the stalker.
        float dX = getX() + getWidth() / 2;
        float dY = getY() + getHeight() / 2;

        // If Daur is facing up or down.
        if (screen.daur.getDirection() == UP || screen.daur.getDirection() == DOWN)
            // This creates a rectangle base by using Daur's width.
            for (float x1 = screen.daur.getX(); x1 > screen.daur.getX() - screen.daur.getWidth() * 4; x1 --)
                for (float x2 = screen.daur.getX() + screen.daur.getWidth(); x2 < screen.daur.getX() + screen.daur.getWidth() * 5; x2 ++) {
                    // If the x-point is within the rectangle.
                    if (dX > x1 && dX < x2) {
                        // If Daur is facing up and the stalker is above.
                        if (screen.daur.dir == UP && dY > screen.daur.getY() + screen.daur.getHeight()) {
                            fadeOut();
                            return;
                        }
                        // If Daur is facing down and the stalker is below.
                        else if (screen.daur.dir == DOWN && dY < screen.daur.getY()) {
                            fadeOut();
                            return;
                        }
                    }
                }

        // If Daur is looking left or right.
        if (screen.daur.dir == RIGHT || screen.daur.dir == LEFT)
            // This creates a rectangle base by using Daur's height.
            for (float y1 = screen.daur.getY(); y1 > screen.daur.getY() - screen.daur.getHeight() * 4; y1 --)
                for (float y2 = screen.daur.getY() + screen.daur.getHeight(); y2 < screen.daur.getY() + screen.daur.getHeight() * 5; y2 ++) {
                    // If the y-point is within the rectangle.
                    if (dY > y1 && dY < y2) {
                        // If Daur is facing to the right and the stalker is to the right of Daur.
                        if (screen.daur.dir == RIGHT && dX > screen.daur.getX() + screen.daur.getWidth()) {
                            fadeOut();
                            return;
                        }
                        // If facing to the left and Daur is to the left of the pantomime.
                        else if (screen.daur.dir == LEFT && dX < screen.daur.getX()) {
                            fadeOut();
                            return;
                        }
                    }
                }

        running = false;
    }

    // Fades out of view, so that the stalker can run correctly. Then runs behind Daur, depending on his orientation.
    private void fadeOut() {
        // Fades out over the course of 0.5 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.9f);
            }
        }, 0.05f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.1f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.7f);
            }
        }, 0.15f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.6f);
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.25f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.4f);
            }
        }, 0.3f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.3f);
            }
        }, 0.35f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.4f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.1f);
            }
        }, 0.45f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0f);
            }
        }, 0.5f);

        // The variables that are responsible for the setting up of the running point.
        float randX;
        float randY;
        int pX;
        int pY;
        // Goes to a random point depending on the way Daur is facing.
        switch (screen.daur.getDirection()) {
            // Daur is facing right.
            case 1:
                randX = (float) Math.random() * 40;
                randY = (float) Math.random() * 40 - 20;
                pX = (int) (screen.daur.getX() + screen.daur.getWidth() / 2 - randX);
                pY = (int) (screen.daur.getY() + screen.daur.getHeight() / 2 + randY);
                runningPoint = new Point(pX, pY);
                break;
            // Daur is facing left.
            case -1:
                randX = (float) Math.random() * 40;
                randY = (float) Math.random() * 40 - 20;
                pX = (int) (screen.daur.getX() + screen.daur.getWidth() / 2 + randX);
                pY = (int) (screen.daur.getY() + screen.daur.getHeight() / 2 + randY);
                runningPoint = new Point(pX, pY);
                break;
            // Daur is facing up.
            case 2:
                randX = (float) Math.random() * 40 - 20;
                randY = (float) Math.random() * 40;
                pX = (int) (screen.daur.getX() + screen.daur.getWidth() / 2 + randX);
                pY = (int) (screen.daur.getY() + screen.daur.getHeight() / 2 - randY);
                runningPoint = new Point(pX, pY);
                break;
            // Daur is facing down.
            case -2:
                randX = (float) Math.random() * 40 - 20;
                randY = (float) Math.random() * 40;
                pX = (int) (screen.daur.getX() + screen.daur.getWidth() / 2 + randX);
                pY = (int) (screen.daur.getY() + screen.daur.getHeight() / 2 + randY);
                runningPoint = new Point(pX, pY);
                break;
        }

        // Sets running to true.
        running = true;
        // After 2 seconds, fades back in.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                fadeIn();
            }
        }, 2);
    }

    // Fades back in to view.
    private void fadeIn() {
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.1f);
            }
        }, 0.05f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.1f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.3f);
            }
        }, 0.15f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.4f);
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.25f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.6f);
            }
        }, 0.3f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.7f);
            }
        }, 0.35f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.4f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.9f);
            }
        }, 0.45f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(1f);
            }
        }, 0.5f);

        running = false;
    }

    // Constantly acquires an angle and uses it to set the velocity.
    private void run() {
        // Continually sets state to running.
        setState(RUNNING, true);
        // Gets angle between the stalker and the point.
        float angle = (float) Math.atan2(runningPoint.getY() - (getY() + getHeight() / 2),
                runningPoint.getX() - (getX() + getWidth() / 2));
        // Sets the velocity based on the cosine and sine of the angle.
        SVX((float) Math.cos(angle));
        SVY((float) Math.sin(angle));

        // Sets the direction based on velocity of the stalker.
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
            run = new Animation(0.25f, FU2, FU3);
        }
        else if (dir == DOWN) {
            idle = new Animation(0.5f, FD1);
            run = new Animation(0.25f, FD2, FD3);
        }
        else if (dir == RIGHT) {
            idle = new Animation(0.5f, FR1);
            run = new Animation(0.25f, FR2, FR3);
        }
        else if (dir == LEFT) {
            idle = new Animation(0.5f, FL1);
            run = new Animation(0.25f, FL2, FL3);
        }
    }

    // Overrides the superclass method so that the stalker immediately runs after being attacked.
    public void loseHealth(int h) {
        if (!invulnerability) {
            health -= (h - armor);
            invulnerability = true;
            flickerSprite();
            // Starts following player if not dead.
            if (health != 0 && !running)
                fadeIn();
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    invulnerability = false;
                    inverted = false;
                }
            }, 0.6f);

            if (health == 0)
                death();
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
