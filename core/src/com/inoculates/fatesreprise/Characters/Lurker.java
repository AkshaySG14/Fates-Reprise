
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Projectiles.Beam;
import com.inoculates.fatesreprise.Projectiles.MudShot;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Lurker class that fires blasts at Daur while hiding in holes..
public class Lurker extends Enemy {
    protected static final int SHOOTING = 5;

    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("lurker1"), FD2 = atlas.findRegion("lurker2"),
            SD = atlas.findRegion("lurker3");

    Animation shoot;

    float checkTime = 0;
    // Whether the lurker is idle or not.
    boolean acting = false;

    public Lurker(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(IDLE, false);
        createAnimations();
        chooseSprite();
        // Note that the lurker is invulnerable and invisible until he shoots.
        invulnerability = true;
        transparent = true;
        setAlpha(0);
        grounded = false;
    }

    protected void update(float deltaTime) {
        // Begins the phase of shooting every three seconds.
        if (inRange() && checkTime > 3 && !acting) {
            materialize();
            acting = true;
        }
    }

    protected void updateTime(float deltaTime) {
        if (state != SHOOTING)
            checkTime += deltaTime;
        animationTime += deltaTime;
    }

    // Same as the wizard method.
    private void materialize() {
        // If dead cannot materialize.
        if (isDead())
            return;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.25f);
                resetDirection();
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
                resetDirection();
            }
        }, 0.4f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.75f);
                resetDirection();
            }
        }, 0.6f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(1);
                resetDirection();
                invulnerability = false;
                transparent = false;
                delayShoot();
            }
        }, 0.8f);
    }

    // Delays the shot of the lurker by a random amount.
    private void delayShoot() {
        // Cannot delay shot if dead.
        if (isDead())
            return;
        float randomTime = (float) Math.random() * 3 + 2;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                shoot();
            }
        }, randomTime);
    }

    // Launches a unstoppable beam at Daur.
    private void shoot() {
        // Cannot shoot if dead.
        if (isDead())
            return;
        // Sets state accordingly.
        setState(SHOOTING, true);
        // Sets the angle of the shot to the angle between the lurker and Daur..
        float shootAngle = (float) Math.atan2(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2,
                screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
        // Creates the beam and sends it towards Daur.
        Beam beam = new Beam(screen, map, screen.daurAtlases.get(5), this, shootAngle);
        screen.projectiles.add(beam);
        // Starts dematerialization after 0.5 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                dematerialize();
            }
        }, 0.5f);
    }

    // Opposite of the materialization method.
    private void dematerialize() {
        // If dead cannot dematerialize.
        if (isDead())
            return;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.75f);
                resetDirection();
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
                resetDirection();
            }
        }, 0.4f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.25f);
                resetDirection();
            }
        }, 0.6f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0);
                resetDirection();
                invulnerability = true;
                transparent = true;
                setState(IDLE, true);
                acting = false;
            }
        }, 0.8f);
    }

    // Sets direction/orientation of the lurker based on the arctangent between the lurker and Daur.
    private void resetDirection() {
        setOriginCenter();
        float angle = (float) Math.atan2(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2,
                screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
        // Note that the angle is always either 90, 180, 270 or 360.
        setRotation(90 * (int) (angle / 90));

    }

    // If Daur is within a certain distance of the lurker, it will shoot.
    private boolean inRange() {
        return Math.sqrt(Math.pow(getX() - screen.daur.getX(), 2) + Math.pow(getY() - screen.daur.getY(), 2)) < 100;
    }

    protected void createAnimations() {
        idle = new Animation(0.5f, FD1, FD2);
        shoot = new Animation(0.5f, SD);
    }

    protected void tryMove() {

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

        if (state == SHOOTING)
            anim = shoot;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
