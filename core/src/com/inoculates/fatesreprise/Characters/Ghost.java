
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.inoculates.fatesreprise.Screens.GameScreen;

// The ghost enemy. This enemy essentially circles around a structure, and will harm the player should he stray too close.
// The ghost is invulnerable and not particularly aggressive.
public class Ghost extends Enemy {
    TextureAtlas.AtlasRegion FU1 = atlas.findRegion("ghostU1"), FU2 = atlas.findRegion("ghostU2"),
            FD1 = atlas.findRegion("ghostD1"), FD2 = atlas.findRegion("ghostD2"), FR1 = atlas.findRegion("ghostR1"),
            FR2 = atlas.findRegion("ghostR2"), FL1 = atlas.findRegion("ghostL1"), FL2 = atlas.findRegion("ghostL2");

    int movementDirection;

    public Ghost(GameScreen screen, TiledMap map, TextureAtlas atlas, int initialDirection) {
        super(screen, map, atlas, 2);
        // Sets the state to be running.
        setState(RUNNING, true);
        // The ghost is unkillable.
        invulnerability = true;
        // Sets the movement direction to its proper amount.
        movementDirection = initialDirection;
        // Initializes frame for collision purposes.
        createAnimations();
        chooseSprite();
        grounded = false;

        // Starts movement.
        switch (movementDirection) {
            // Sets velocity to the right.
            case 1:
                SVX(0.5f);
                vel.y = 0;
                dir = RIGHT;
                break;
            // Sets velocity to the left.
            case -1:
                SVX(-0.5f);
                vel.y = 0;
                dir = LEFT;
                break;
            // Sets velocity upwards.
            case 2:
                vel.x = 0;
                SVY(0.5f);
                dir = UP;
                break;
            // Sets velocity downwards.
            case -2:
                vel.x = 0;
                SVY(-0.5f);
                dir = DOWN;
                break;
        }
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);
        // Checks to see whether the ghost is in a new marker.
        checkMarker();    }

    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
    }

    // Moves the ghost in accordance with its direction.
    private void move() {
        // Switch and case expression which checks the movement.
        switch (movementDirection) {
            // Sets velocity to the right.
            case 1:
                SVX(0.5f);
                vel.y = 0;
                dir = RIGHT;
                break;
            // Sets velocity to the left.
            case -1:
                SVX(-0.5f);
                vel.y = 0;
                dir = LEFT;
                break;
            // Sets velocity upwards.
            case 2:
                vel.x = 0;
                SVY(0.5f);
                dir = UP;
                break;
            // Sets velocity downwards.
            case -2:
                vel.x = 0;
                SVY(-0.5f);
                dir = DOWN;
                break;
        }
    }

    // This method checks if the ghost has floated over a ghost marker, and if so, changes its direction.
    private void checkMarker() {
        // Direction that can possibly be the new direction of the ghost.
        int direction = movementDirection;
        // Iterates over all markers on the map, attempting to find a guidance ghost marker.
        for (MapObject object : map.getLayers().get("Markers").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                Rectangle marker = new Rectangle(x, y, layer.getTileWidth(), layer.getTileHeight());
                // Object is a ghost marker, and the ghost is inside of it. Note that whether the ghost is inside of the
                // marker is determined by the direction it is moving. This is to ensure that the ghost is fully inside
                // of the marker, and not simply a part of it. If the marker does not contain all of the ghost, returns
                // the method.
                if (object.getProperties().containsKey("ghostmarker"))
                    switch (movementDirection) {
                        case 1:
                            // If the marker contains the ghost, sets the new direction.
                            if (marker.contains(getX() - getWidth() / 4, getCY()))
                                direction = Integer.parseInt(rectObject.getProperties().get("ghostmarker").toString());
                            break;
                        case -1:
                            if (marker.contains(getRX() + getWidth() / 4, getCY()))
                                direction = Integer.parseInt(rectObject.getProperties().get("ghostmarker").toString());
                            break;
                        case 2:
                            if (marker.contains(getCX(), getY() - getHeight() / 4))
                                direction = Integer.parseInt(rectObject.getProperties().get("ghostmarker").toString());
                            break;
                        case -2:
                            if (marker.contains(getCX(), getRY() + getHeight() / 4))
                                direction = Integer.parseInt(rectObject.getProperties().get("ghostmarker").toString());
                            break;
                    }
                // Sets new direction (if it is different) and moves accordingly.
                movementDirection = direction;
                move();
            }
    }

    // This method moves the beetle depending on the x and y components of its velocity. Additionally, it checks for any
    // collisions, including those with the edge of the screen. The actual methods are in the super class Enemy.
    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    protected void createAnimations() {
        if (dir == UP)
            run = new Animation(0.25f, FU1, FU2);
        else if (dir == DOWN)
            run = new Animation(0.25f, FD1, FD2);
        else if (dir == RIGHT)
            run = new Animation(0.25f, FR1, FR2);
        else if (dir == LEFT)
            run = new Animation(0.25f, FL1, FL2);
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
        Animation anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 7 / 8,
                anim.getKeyFrame(animationTime, true).getRegionHeight() * 7 / 8);
    }
}
