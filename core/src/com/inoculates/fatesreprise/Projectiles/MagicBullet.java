package com.inoculates.fatesreprise.Projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is an extremely fast moving projectile that's intent is to hit Daur.
public class MagicBullet extends Projectile {
    Character target;
    private Animation animate;
    private TextureAtlas.AtlasRegion proj1 = atlas.findRegion("magicbullet1"), proj2 = atlas.findRegion("magicbullet2");

    // Sets size and the angle of the Magic Bullet.
    public MagicBullet(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, float angle) {
        super(screen, map, atlas, character);
        setSize(10, 5);
        setOriginCenter();
        setRotation(angle * 180 / (float) Math.PI);
        setStart(angle);
        createAnimations();
        chooseSprite();
    }

    // Sets the position of the Magic Bullet, depending on the angle of the Magic Bullet.
    private void setStart(float angle) {
        // Note the use of the angle to offset the position.
        setPosition((float) (owner.getX() + owner.getWidth() / 2 - getWidth() / 2 + Math.cos(angle)),
                (float) (owner.getY() + owner.getHeight() / 2 - getHeight() / 2 + Math.sin(angle)));
        // Sets the velocity also according to the angle.
        vel.x = (float) (Math.cos(angle) * 3);
        vel.y = (float) (Math.sin(angle) * 3);
    }

    protected void update(float deltaTime) {
        createAnimations();
        chooseSprite();
        updateTime(deltaTime);
        if (vel.x != 0 || vel.y != 0)
            tryMove();
    }

    public void explode() {
        final Projectile projectile = this;
        vel.x = 0;
        vel.y = 0;
        // Fades the Magic Bullet out before removing it.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.125f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.25f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.375f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (screen.projectiles.contains(projectile))
                    screen.projectiles.remove(projectile);
            }
        }, 0.5f);
        animationTime = 0;
    }

    protected void chooseSprite() {
        setRegion(animate.getKeyFrame(animationTime, true));
    }

    protected void createAnimations() {
        animate = new Animation(0.1f, proj1, proj2);
    }

    protected void effects() {
        if (target == null || !target.equals(screen.daur))
            return;
        screen.daur.damageCollision(this);
    }

    protected boolean collidesCharacter() {
        for (Character character : screen.charIterator) {
            if (character != owner && !(character instanceof Enemy))
                for (float step = 0; step < getWidth() - 1; step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < getHeight() - 1; step2 += layer.getTileHeight() / 16)
                        if (character.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2) &&
                                isValidTarget(character)) {
                            target = character;
                            effects();
                            return true;
                        }
        }
        return false;
    }

    private void tryMove() {
        checkCollisions();
    }
}
