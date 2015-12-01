
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;

import java.awt.*;

// This is an enemy class that follows Daur, damaging him via collision.
public class Bat extends Enemy {
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("bat1"), FD2 = atlas.findRegion("bat2");

    float moveTime = 0, roostTime = 0;
    boolean following;
    boolean roosting;

    private Point roost;

    public Bat(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 1);
        setState(RUNNING, true);
        following = true;
        grounded = false;
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel. y == 0)
            setState(IDLE, false);
        // Starts following the player after 10 seconds of roosting.
        if (roostTime > 7) {
            following = true;
            roosting = false;
            roostTime = 0;
        }
        // Roosts after following for five seconds.
        if (moveTime > 5) {
            roost();
            following = false;
            moveTime = 0;
        }
        // Attempts to attack the player if following.
        if (following) {
            follow();
            setState(RUNNING, true);
        }
        // Note that the bat will only try to move to the roost if it is more than a certain distance away from it.
        // Otherwise, it shall remain in the roost.
        else {
            if (outOfProximity())
                homeIn();
            else {
                roosting = true;
                setState(IDLE, true);
                vel.x = 0;
                vel.y = 0;
            }
        }
    }

    // Constantly acquires an angle and uses it to set the velocity. Simplified version of the pantomime method.
    private void follow() {
        // Continually sets state to running.
        setState(RUNNING, true);
        // Gets angle between the bat and Daur.
        float angle = (float) Math.atan2(screen.daur.getY() + getHeight() / 2 - (getY() + getHeight() / 2),
                screen.daur.getX() + getWidth() / 2 - (getX() + getWidth() / 2));
        // Sets the velocity based on the cosine and sine of the angle.
        SVX((float) Math.cos(angle) / 2);
        SVY((float) Math.sin(angle) / 2);
    }

    // Goes to a space for "rest".
    private void roost() {
        // Acquires a random tile in the cell the bat is currently in.
        int x = (int) (Math.random() * 8) + 1;
        int y = (int) (Math.random() * 8) + 1;

        // Gets the CELL of the bat.
        int cellX = (int) (getX() / layer.getTileWidth()) / 10;
        int cellY = (int) (getY() / layer.getTileHeight()) / 10;

        // Gets the roost using the cell origin added to the random tile.
        roost = new Point(cellX * 160 + x * 16, cellY * 160 + y * 16);
    }

    // Constantly acquires an angle and uses it to set the velocity. Unlike the follow method, this navigates the bat
    // to a random roosting position.
    private void homeIn() {
        // Continually sets state to running.
        setState(RUNNING, true);
        // Gets angle between the bat and the roost.
        float angle = (float) Math.atan2(roost.getY() + layer.getTileHeight() / 2 - (getY() + getHeight() / 2),
                roost.getX() + layer.getTileWidth() - (getX() + getWidth() / 2));
        // Sets the velocity based on the cosine and sine of the angle.
        SVX((float) Math.cos(angle) / 2);
        SVY((float) Math.sin(angle) / 2);
    }

    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        if (following)
            moveTime += deltaTime;
        else
            roostTime += deltaTime;
    }

    //This method moves the bat through both x and y velocities.
    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    // Overrides the super method to avoid bashing into walls or interactables.
    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        // Unlike the enemy super class, the bat may only collide with the edge of a cell.
        if (vel.x < 0)
            collisionX = getX() < (cellX - 1) * layer.getTileWidth() * 10;
        else if (vel.x > 0)
            collisionX = getX() + getWidth() > cellX * layer.getTileWidth() * 10;

        if (collisionX) {
            setX(oldX);
            vel.x = vel.x * -1;
            dir = -dir;
        }

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

    // Creates all the animations of the bat with their corresponding frames.
    protected void createAnimations() {
        idle = new Animation(0.5f, FD1);
        run = new Animation(0.2f, FD1, FD2);
    }

    private boolean outOfProximity() {
        return Math.sqrt(Math.pow(getX() + getWidth() / 2 - roost.getX() - layer.getTileWidth() / 2, 2) +
                Math.pow(getY() + getHeight() / 2 - roost.getY() - layer.getTileHeight() / 2, 2)) > 8;
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
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 3 / 5,
                anim.getKeyFrame(animationTime, true).getRegionHeight() * 3 / 5);
    }

    // Overrides the damage collision method to prevent movement when roosting.
    public void damageCollision(Sprite sprite, int dmg) {
        if (armor >= dmg || invulnerability || isDead())
            return;

        float angle = (float) Math.atan2(getY() - sprite.getY(), getX() - sprite.getX());
        // If roosting does NOT move. Note the bat does not move as fast as other enemies when hit.
        if (!roosting) {
            vel.x = (float) (2 * Math.cos(angle));
            vel.y = (float) (2 * Math.sin(angle));
            stun();
        }

        loseHealth(dmg);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
                unStun();
            }
        }, 0.1f);
    }

    // Overrides the stun collision method for the same reason.
    public void stunCollision(Sprite sprite, float time) {
        float angle = (float) Math.atan2(getY() - sprite.getY(), getX() - sprite.getX());
        if (!roosting) {
            vel.x = (float) (4 * Math.cos(angle));
            vel.y = (float) (4 * Math.sin(angle));
            stun();
        }
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
            }
        }, 0.1f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
            }
        }, time);
    }

}
