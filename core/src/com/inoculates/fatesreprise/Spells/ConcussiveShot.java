package com.inoculates.fatesreprise.Spells;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Characters.Character;

// Concussive shot spell.
public class ConcussiveShot extends Spell {
    Animation anim;
    Character target;

    boolean exploding = false;

    public ConcussiveShot(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, int direction) {
        super(screen, map, atlas, character);
        screen.spells.add(this);
        setDirection(direction);
        createAnimations();
        chooseSprite();
    }

    //
    private void setDirection(int direction) {
        // Sets the velocity depending on the four cardinal directions.
        switch (direction) {
            case 0:
                vel.x = 0.75f;
                break;
            case 1:
                vel.y = 0.75f;
                break;
            case 2:
                vel.x = -0.75f;
                break;
            case 3:
                vel.y = -0.75f;
                break;
        }
        accelerate();
    }

    private void accelerate() {
        // Creates a loop that schedules tasks for the screen.globalTimer, incrementing the time every loop.
        for (float delta = 0; delta < 0.5; delta += 0.05)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    // Increases the velocity by every iteration by a small, proportional amount.
                    vel.x *= 1.15;
                    vel.y *= 1.15;
                }
            }, delta);
    }

    // Offsets the position based on the direction of the concussive shot.
    public void setInitialPosition(int direction) {
        switch (direction) {
            case 1:
                setPosition(owner.getX() + owner.getWidth(), owner.getY() + owner.getHeight() / 2 - getRadius() / 2);
                break;
            case 2:
                setPosition(owner.getX() + owner.getWidth() / 2 - getRadius() / 2, owner.getY() + owner.getHeight() - 5);
                break;
            case -1:
                setPosition(owner.getX() - getRadius(), owner.getY() + owner.getHeight() / 2 - getRadius() / 2);
                break;
            case -2:
                setPosition(owner.getX() + owner.getWidth() / 2 - (owner.getWidth() * 3 / 4) / 2, owner.getY());
        }
    }

    protected void update(float deltaTime) {
        chooseSprite();
        updateTime(deltaTime);
        if (vel.x != 0 || vel.y != 0)
            tryMove();
    }

    protected void chooseSprite() {
        createAnimations();
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 3 / 4, anim.getKeyFrame(animationTime, true).getRegionHeight() * 3 / 4);
    }

    // This is just the pulsing animation the shot has for non-exploding frames. If the shot is exploding, it will have
    // a semi-shrinking animation.
    protected void createAnimations() {
        if (!exploding) {
            if (vel.x > 0)
                anim = new Animation(0.2f, atlas.findRegion("concussiveshotR1"), atlas.findRegion("concussiveshotR2"));
            if (vel.x < 0)
                anim = new Animation(0.2f, atlas.findRegion("concussiveshotL1"), atlas.findRegion("concussiveshotL2"));
            if (vel.y > 0)
                anim = new Animation(0.2f, atlas.findRegion("concussiveshotU1"), atlas.findRegion("concussiveshotU2"));
            if (vel.y < 0)
                anim = new Animation(0.2f, atlas.findRegion("concussiveshotD1"), atlas.findRegion("concussiveshotD2"));
        }
        else
            anim = new Animation(0.25f, atlas.findRegion("concussiveshotE1"), atlas.findRegion("concussiveshotE2"),
                    atlas.findRegion("concussiveshotE3"), atlas.findRegion("concussiveshotE4"));
    }

    protected void explode() {
        explodeTile();
        final Spell spell = this;
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
                screen.spells.remove(spell);
            }
        }, 1);
        exploding = true;
        animationTime = 0;
    }

    // Stuns the target if it is an enemy and is not invulnerable (due to injury, death, or natural invulnerability).
    protected void effects() {
        if (target == null || !target.isEnemy())
            return;
        final Enemy enemy = (Enemy) target;
        if (!enemy.isTransparent() && !enemy.isDead())
            enemy.stunCollision(this, 2);
    }

    public float getRadius() {
        return 12;
    }

    private void tryMove() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft() || collidesInteractable();
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesInteractable();

        if (collisionX) {
            explode();
            setX(oldX);
            vel.x = 0;
        }

        if (collidesCharacter()) {
            explode();
            setX(oldX);
            vel.x = 0;
        }

        if (checkOutOfBounds()) {
            explode();
            setX(oldX);
            vel.x = 0;
        }

        setY(getY() + vel.y);

        if (vel.y < 0) {
            collisionY = collidesBottom() || collidesInteractable();
        } else if (vel.y > 0)
            collisionY = collidesTop() || collidesInteractable();

        if (collisionY) {
            explode();
            setY(oldY);
            vel.y = 0;
        }

        if (collidesCharacter()) {
            explode();
            setY(oldY);
            vel.y = 0;
        }

        if (checkOutOfBounds()) {
            explode();
            setY(oldY);
            vel.y = 0;
        }
    }

    protected boolean collidesCharacter() {
        for (Character character : screen.charIterator) {
            if (character != owner)
                for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < getHeight(); step2 += layer.getTileHeight() / 16)
                        if (character.getBoundingRectangle().contains(getX()  + step, getY()+ step2) && !character.isTransparent()) {
                            target = character;
                            effects();
                            return true;
                        }
        }
        return false;
    }
}
