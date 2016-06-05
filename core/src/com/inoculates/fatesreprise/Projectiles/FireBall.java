package com.inoculates.fatesreprise.Projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the fire ball casted by a wizard.
public class FireBall extends Projectile {
    Character target;
    private Animation animate;
    private TextureAtlas.AtlasRegion proj1 = atlas.findRegion("fireball1"), proj2 = atlas.findRegion("fireball2");

    boolean exploding = false;
    final float RADIUS = 3.25f;

    // Sets size and the angle of the fireball.
    public FireBall(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, float angle) {
        super(screen, map, atlas, character);
        setSize(7.5f, 7.5f);
        setStart(angle);
        createAnimations();
        chooseSprite();
    }

    // Sets the position of the fireball, depending on the angle of the fireball.
    private void setStart(float angle) {
        // Note the use of the angle to offset the position.
        setPosition((float) (owner.getX() + owner.getWidth() / 2 - getWidth() / 2 + Math.cos(angle)),
                (float) (owner.getY() + owner.getHeight() / 2 - getHeight() / 2 + Math.sin(angle)));
        // Sets the velocity also according to the angle.
        vel.x = (float) (2 * Math.cos(angle));
        vel.y = (float) (2 * Math.sin(angle));
    }

    // These two centers are used for collision, and rely on the circular nature of the ball.
    private float getCenterX() {
        return getX() + RADIUS;
    }

    private float getCenterY() {
        return getY() + RADIUS;
    }

    protected void update(float deltaTime) {
        createAnimations();
        chooseSprite();
        updateTime(deltaTime);
        if (vel.x != 0 || vel.y != 0)
            tryMove();
    }

    protected void chooseSprite() {
        setRegion(animate.getKeyFrame(animationTime, true));
    }

    protected void createAnimations() {
        animate = new Animation(0.1f, proj1, proj2);
    }

    // When a fireball hits either a terrain object or Daur.
    public void explode() {
        // Returns if already exploding to avoid redundancy.
        if (exploding)
            return;
        // Explodes the terrain object, should it exist.
        explodeTile();
        final Projectile projectile = this;
        vel.x = 0;
        vel.y = 0;
        // Fades the fireball out before removing it.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.25f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.25f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.5f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.75f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (screen.projectiles.contains(projectile))
                    screen.projectiles.remove(projectile);
            }
        }, 1);
        exploding = true;
        animationTime = 0;
        // Plays the explosion sound.
        screen.storage.sounds.get("effect2").play(1.0f);
    }

    protected void effects() {
        if (target == null || !target.equals(screen.daur))
            return;
        screen.daur.damageCollision(this);
    }

    private void tryMove() {
        checkCollisions();
    }

    protected boolean collidesCharacter() {
        for (Character character : screen.charIterator) {
            if (character != owner)
                for (float step = 0; step < character.getWidth(); step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < character.getHeight(); step2 += layer.getTileHeight() / 16)
                        if (Math.abs(character.getX() + step - getCenterX()) < RADIUS &&
                                Math.abs(character.getY() + step2 - getCenterY()) < RADIUS && isValidTarget(character)) {
                            target = character;
                            effects();
                            return true;
                        }
        }
        return false;
    }
}
