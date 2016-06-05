package com.inoculates.fatesreprise.Projectiles;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the mud ball shot by a mud doll.
public class MudShot extends Projectile {
    Character target;

    boolean exploding = false;
    final float RADIUS = 5;
    float angle = 0;

    public MudShot(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, int direction) {
        super(screen, map, atlas, character);
        setSize(7.5f, 7.5f);
        setDirection(direction);
        setStart(direction);
        createAnimations();
        chooseSprite();
    }

    // Sets the position depending on the direction of the owner.
    private void setStart(int direction) {
        switch (direction) {
            // Doll's direction is to the right, sets the ball slightly to the right of the doll.
            case 1:
                setPosition(owner.getX() + owner.getWidth() + 1, owner.getY() + owner.getHeight() / 2 - getHeight() / 2);
                break;
            // Doll's direction is to the left, sets the ball slightly to the left of the doll.
            case -1:
                setPosition(owner.getX() - 1, owner.getY() + owner.getHeight() / 2 - getHeight() / 2);
                break;
            // Etc.
            case 2:
                setPosition(owner.getX() + owner.getWidth() / 2 - getWidth() / 2, owner.getY() + owner.getHeight() + 1);
                break;
            case -2:
                setPosition(owner.getX() + owner.getWidth() / 2 - getWidth() / 2, owner.getY() - 1);
                break;
        }
    }

    // Sets the velocity of the ball depending on the direction of the mud doll that fired it.
    private void setDirection(int direction) {
        switch (direction) {
            case 1:
                vel.x = 2;
                break;
            case -1:
                vel.x = -2;
                break;
            case 2:
                vel.y = 2;
                break;
            case -2:
                vel.y = -2;
                break;
        }
    }

    private float getCenterX() {
        return getX() + RADIUS;
    }

    private float getCenterY() {
        return getY() + RADIUS;
    }

    protected void update(float deltaTime) {
        chooseSprite();
        setAngle();
        updateTime(deltaTime);
        if (vel.x != 0 || vel.y != 0)
            tryMove();
    }

    protected void chooseSprite() {
        setRegion(atlas.findRegion("mudball"));
    }

    protected void createAnimations() {
    }

    // This is the method that, as it's constantly called, will revolve the ball.
    private void setAngle() {
        // If the ball is not moving does NOT rotate the ball.
        if (vel.x == 0 && vel.y == 0)
            return;

        // Otherwise if moving in a lateral direction, revolves backwards from the direction it is moving.
        if (vel.x != 0)
            angle += 10 * -Math.signum(vel.x);
            // Same but for vertical direction.
        else
            angle += 10 * -Math.signum(vel.y);

        // Ensures that the ball spins about its center.
        setOriginCenter();
        // Sets the rotation of the ball.
        setRotation(angle);
    }

    // Same as the fireball.
    public void explode() {
        if (exploding)
            return;
        explodeTile();
        final Projectile projectile = this;
        vel.x = 0;
        vel.y = 0;
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
        // Plays the explode sound.
        screen.storage.sounds.get("explode1").play(0.5f);
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
