
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.inoculates.fatesreprise.Screens.GameScreen;

import java.util.ArrayList;

// Similar to the static villager class, the dynamic villager is a character that can talk to Daur, but also moves.
public class DynamicVillager extends Villager {
    //Animations of the dynamic villager, consisting only of the idle and run animation.
    Animation idle, run;
    // Only state applicable.
    public static final int RUNNING = 2;
    // The stage of a preset run pattern that tells the program which way the villager should move.
    private int stage = 0;
    // The stops in location that tell the villager to change direction.
    private float[] stops;

    //The regions that serve as frames for the animations of the villager.
    TextureAtlas.AtlasRegion idle1, idle2, FU1, FU2, FD1, FD2, FR1, FR2, FL1, FL2;

    public DynamicVillager(GameScreen screen, TiledMap map, TextureAtlas atlas, ArrayList<Rectangle> paths, int villager) {
        super(screen, map, atlas, villager);

        // This gets the stop points.
        stops = new float[paths.size()];
        // Creates the x-axis stop points, meaning when the villager is beyond the x-point of the stop.
        for (int i = 1; i < paths.size(); i += 2)
            stops[i] = (int) (paths.get(i).getX() / layer.getTileWidth()) * layer.getTileWidth();
        // Creates the y-axis stop points, meaning when the villager is beyond the y-point of the stop.
        for (int i = 0; i < paths.size(); i += 2)
            stops[i] = (int) (paths.get(i).getY() / layer.getTileHeight()) * layer.getTileHeight();

        // Starts the infinite movement.
        move();
    }

    // Creates the regions depending on the villager, since this class is responsible for multiple villagers.
    protected void createRegions() {
        switch (villager) {
            case 4:
                idle1 = atlas.findRegion("villager5idle1");
                idle2 = atlas.findRegion("villager5idle2");
                FU1 = atlas.findRegion("villager5runU1");
                FU2 = atlas.findRegion("villager5runU2");
                FD1 = atlas.findRegion("villager5runD1");
                FD2 = atlas.findRegion("villager5runD2");
                FR1 = atlas.findRegion("villager5runR1");
                FR2 = atlas.findRegion("villager5runR2");
                FL1 = atlas.findRegion("villager5runL1");
                FL2 = atlas.findRegion("villager5runL2");
                break;
            case 7:
                idle1 = atlas.findRegion("villager8idle1");
                idle2 = atlas.findRegion("villager8idle2");
                FU1 = atlas.findRegion("villager8runU1");
                FU2 = atlas.findRegion("villager8runU2");
                FD1 = atlas.findRegion("villager8runD1");
                FD2 = atlas.findRegion("villager8runD2");
                FR1 = atlas.findRegion("villager8runR1");
                FR2 = atlas.findRegion("villager8runR2");
                FL1 = atlas.findRegion("villager8runL1");
                FL2 = atlas.findRegion("villager8runL2");
                break;
        }
    }

    //Method responsible for periodically making the villager act.
    protected void update(float deltaTime) {
        // Checks if the villager has passed a checkpoint.
        checkStage();

        if ((vel.x != 0 || vel.y != 0) && !stun) {
            tryMove();
            setState(RUNNING, true);
        }
        else {
            setState(IDLE, true);
            if (!screen.daur.getBoundingRectangle().overlaps(getBoundingRectangle()))
                move();
        }
    }

    // Same as the character class.
    private void tryMove() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft();
        else if (vel.x > 0)
            collisionX = collidesRight();

        if (collisionX || collidesCharacter()) {
            setX(oldX);
            vel.x = 0;
        }

        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = collidesBottom();

        else if (vel.y > 0)
            collisionY = collidesTop();

        if ((collisionY) || collidesCharacter()) {
            setY(oldY);
            vel.y = 0;
        }
    }

    //Creates all the animations of the villager with their corresponding frames.
    protected void createAnimations() {
        if (dir == UP) {
            run = new Animation(0.25f, FU1, FU2);
            idle = new Animation(0.5f, FU1);
        }
        else if (dir == DOWN) {
            run = new Animation(0.25f, FD1, FD2);
            idle = new Animation(0.5f, FD1);
        }
        else if (dir == RIGHT) {
            run = new Animation(0.25f, FR1, FR2);
            idle = new Animation(0.5f, FR1);
        }
        else if (dir == LEFT) {
            run = new Animation(0.25f, FL1, FL2);
            idle = new Animation(0.5f, FL1);
        }
    }

    protected boolean overrideCheck() {
        return false;
    }

    protected boolean priorities(int cState) {
        return false;
    }

    protected void chooseSprite()
    {
        Animation anim = idle;

        if (state == IDLE || state == DEAD)
            anim = idle;
        if (state == RUNNING)
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 8 / 7, anim.getKeyFrame(animationTime, true).getRegionHeight() * 8 / 7);
    }

    // If this is the 4th villager (the one around the flowers).
    private void move() {
        if (villager == 4) {
            switch (stage) {
                // First checkpoint. Makes villager move right.
                case 0:
                    vel.x = 0.5f;
                    vel.y = 0;
                    dir = RIGHT;
                    break;
                // Second checkpoint. Makes villager move down.
                case 1:
                    vel.x = 0;
                    vel.y = -0.5f;
                    dir = DOWN;
                    break;
                // Etc.
                case 2:
                    vel.x = -0.5f;
                    vel.y = 0;
                    dir = LEFT;
                    break;
                case 3:
                    vel.x = 0;
                    vel.y = 0.5f;
                    dir = UP;
                    break;
            }
        }
        // Else moves for villager 7.
        else {
            switch (stage) {
                case 0:
                    vel.x = 0.5f;
                    vel.y = 0;
                    dir = RIGHT;
                    break;
                case 1:
                    vel.x = 0;
                    vel.y = -0.5f;
                    dir = DOWN;
                    break;
                case 2:
                    vel.x = -0.5f;
                    vel.y = 0;
                    dir = LEFT;
                    break;
                case 3:
                    vel.x = 0;
                    vel.y = -0.5f;
                    dir = DOWN;
                    break;
                case 4:
                    vel.x = 0.5f;
                    vel.y = 0;
                    dir = RIGHT;
                    break;
                case 5:
                    vel.x = 0;
                    vel.y = 0.5f;
                    dir = UP;
                    break;
                case 6:
                    vel.x = -0.5f;
                    vel.y = 0;
                    dir = LEFT;
                    break;
                case 7:
                    vel.x = 0;
                    vel.y = 0.5f;
                    dir = UP;
                    break;
            }
        }
    }

    // This changes the stage based on the position of the villager relative to the checkpoints.
    private void checkStage() {
        // If the 4th villager.
        if (villager == 4) {
            switch (stage) {
                case 0:
                    // If the villager is beyond the x-point of the first checkpoint, increments stage.
                    if (getX() > stops[1]) {
                        stage = 1;
                        move();
                    }
                    break;
                case 1:
                    // If the villager is less than the y-point of the second checkpoint, increments stage again.
                    if (getY() + getHeight() < stops[2] + layer.getTileHeight()) {
                        stage = 2;
                        move();
                    }
                    break;
                case 2:
                    // Etc.
                    if (getX() + getWidth() < stops[3] + layer.getTileWidth()) {
                        stage = 3;
                        move();
                    }
                    break;
                case 3:
                    // If the villager is beyond the y-point of the last checkpoint, sets stage to zero, restarting the cycle.
                    if (getY() > stops[0]) {
                        stage = 0;
                        move();
                    }
                    break;
            }
        }
        // Same but for villager 7, which is a slightly more complicated path.
        else {
            switch (stage) {
                case 0:
                    if (getX() > stops[1]) {
                        stage = 1;
                        move();
                    }
                    break;
                case 1:
                    if (getY() + getHeight() < stops[2] + layer.getTileHeight()) {
                        stage = 2;
                        move();
                    }
                    break;
                case 2:
                    if (getX() + getWidth() < stops[3] + layer.getTileWidth()) {
                        stage = 3;
                        move();
                    }
                    break;
                case 3:
                    if (getY() + getHeight() < stops[4] + layer.getTileHeight()) {
                        stage = 4;
                        move();
                    }
                    break;
                case 4:
                    if (getX() > stops[5]) {
                        stage = 5;
                        move();
                    }
                    break;
                case 5:
                    if (getY() > stops[2] - layer.getTileHeight()) {
                        stage = 6;
                        move();
                    }
                    break;
                case 6:
                    if (getX() + getWidth() < stops[3] + layer.getTileWidth()) {
                        stage = 7;
                        move();
                    }
                    break;
                case 7:
                    if (getY() > stops[0]) {
                        stage = 0;
                        move();
                    }
                    break;
            }
        }
    }
}
