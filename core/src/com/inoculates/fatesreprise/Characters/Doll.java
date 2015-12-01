
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Projectiles.MudShot;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Mud doll class. This is an enemy that shoots mud balls to damage Daur. The majority of the methods and variables are
// very similar to the beetle class, and thus the beetle class should be looked at first.
public class Doll extends Enemy {
    protected static final int SHOOTING = 6;
    boolean cooldown = false;

    TextureAtlas.AtlasRegion FU1 = atlas.findRegion("dollU1"), FU2 = atlas.findRegion("dollU2"),
            FD1 = atlas.findRegion("dollD1"), FD2 = atlas.findRegion("dollD2"), FR1 = atlas.findRegion("dollR1"),
            FR2 = atlas.findRegion("dollR2"), FL1 = atlas.findRegion("dollL1"), FL2 = atlas.findRegion("dollL2"),
            SU = atlas.findRegion("dollSU"), SD = atlas.findRegion("dollSD"), SR = atlas.findRegion("dollSR"),
            SL = atlas.findRegion("dollSL");

    Animation shoot;

    float moveTime = 0, checkTime = 0;

    int movementDirection;

    public Doll(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(RUNNING, false);
        checkMove();
        move();
    }

    protected void update(float deltaTime) {
        if (vel.x == 0 && vel.y == 0 && state == RUNNING)
            setState(IDLE, false);
        if (!cooldown)
            checkShoot();
        if (checkTime > 1)
            checkMove();
        if (moveTime > 1.5)
            move();
    }

    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        if (state != SHOOTING) {
            moveTime += deltaTime;
            checkTime += deltaTime;
        }
    }

    // Checks if Daur is within shooting range of the mud doll.
    private void checkShoot() {
        switch (dir) {
            case RIGHT:
                // Daur is to the right of the mud doll, and is no more than the doll's height in distance away on the
                // y-axis.
                if (screen.daur.getX() > getX() && Math.abs(screen.daur.getY() + screen.daur.getHeight()
                        - getY() - getHeight()) < getHeight())
                    shoot();
                return;
            case LEFT:
                // Daur is to the left of the mud doll, and same y-axis distance requirement as above.
                if (screen.daur.getX() < getX() && Math.abs(screen.daur.getY() + screen.daur.getHeight()
                        - getY() - getHeight()) < getHeight())
                    shoot();
                return;
            case UP:
                // Daur is to the top of the mud doll, and is no more than the doll's width away on the x-axis.
                if (screen.daur.getY() > getY() && Math.abs(screen.daur.getX() + screen.daur.getHeight()
                        - getX() - getWidth()) < getWidth())
                    shoot();
                return;
            case DOWN:
                // Daur is to the bottom of the mud doll, and same x-axis distance requirement as above.
                if (screen.daur.getY() < getY() && Math.abs(screen.daur.getX() + screen.daur.getWidth()
                        - getX() - getWidth()) < getWidth())
                    shoot();
        }
    }

    // Creates and launches a mud ball at Daur.
    private void shoot() {
        // Sets state to shooting.
        setState(SHOOTING, false);
        // Creates the mud ball with a specific direction.
        MudShot shot = new MudShot(screen, map, screen.daurAtlases.get(5), this, dir);
        // Adds the projectile to the render list.
        screen.projectiles.add(shot);
        // Sets the cooldown to true, to avoid shooting too many balls in too short a time.
        cooldown = true;
        // After 0.5 seconds of being stunned, the doll will move again. After 2 seconds, the doll will be able to shoot
        // again.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(RUNNING, true);
            }
        }, 0.5f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cooldown = false;
            }
        }, 2);
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
            setState(RUNNING, false);
        }
        checkTime = 0;
    }

    private void move() {
        if (!cooldown) {
            cooldown = true;
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 1);
        }

        movementDirection = ((int) (Math.random() * 16)) / 4;
        moveTime = 0;
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    // The doll cannot move if it is currently shooting.
    protected boolean canMove() {
        return state != SHOOTING;
    }

    protected void createAnimations() {
        if (dir == UP) {
            run = new Animation(0.25f, FU1, FU2);
            idle = new Animation(0.5f, FU1);
            shoot = new Animation(0.5f, SU);
        }
        else if (dir == DOWN) {
            run = new Animation(0.25f, FD1, FD2);
            idle = new Animation(0.5f, FD1);
            shoot = new Animation(0.5f, SD);
        }
        else if (dir == RIGHT) {
            run = new Animation(0.25f, FR1, FR2);
            idle = new Animation(0.5f, FR1);
            shoot = new Animation(0.5f, SR);
        }
        else if (dir == LEFT) {
            run = new Animation(0.25f, FL1, FL2);
            idle = new Animation(0.5f, FL1);
            shoot = new Animation(0.5f, SL);
        }
    }

    protected boolean overrideCheck() {
        return (state == SHOOTING || state == DEAD);
    }

    protected boolean priorities(int cState)
    {
        return state == DEAD;
    }

    protected void chooseSprite()
    {
        Animation anim = idle;

        if (state == IDLE || state == DEAD)
            anim = idle;
        if (state == FALLING)
            anim = fall;
        if (state == DROWNING)
            anim = drown;
        if (state == RUNNING)
            anim = run;
        if (state == SHOOTING)
            anim = shoot;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
