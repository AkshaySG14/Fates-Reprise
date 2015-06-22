package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the red enemy death effect that occurs after each enemy dies.
public class EnemyDeath extends Effect {
    Animation anim;
    Enemy owner;

    // Note the usage of the owner to get the enemy that is dying.
    public EnemyDeath(GameScreen screen, TiledMap map, TextureAtlas atlas, Enemy enemy) {
        super(screen, map, atlas, false);
        owner = enemy;
        // The texture the sprite is created on is too large, so it is shrunk for the effect.
        setSize(anim.getKeyFrame(animationTime).getRegionWidth() / 5, anim.getKeyFrame(animationTime).getRegionHeight() / 5);
        fadeAway();
    }

    // This method creates the disappearing effect for the death effect. Note that it also sets the transparency
    private void fadeAway() {
        final EnemyDeath DEATH = this;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.9f);
                setAlpha(0.9f);
            }
        }, 0.1f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.8f);
                setAlpha(0.8f);
            }
        }, 0.2f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.7f);
                setAlpha(0.7f);
            }
        }, 0.3f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.6f);
                setAlpha(0.6f);
            }
        }, 0.4f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.5f);
                setAlpha(0.5f);
            }
        }, 0.5f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.4f);
                setAlpha(0.4f);
            }
        }, 0.6f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.3f);
                setAlpha(0.3f);
            }
        }, 0.7f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.2f);
                setAlpha(0.2f);
            }
        }, 0.8f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                owner.setAlpha(0.1f);
                setAlpha(0.1f);
            }
        }, 0.9f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.effects.remove(DEATH);
            }
        }, 1);
        timer.start();
    }

    protected void update(float deltaTime) {
        chooseSprite();
        updateTime(deltaTime);
        resetPosition();
    }

    protected void chooseSprite() {
        setRegion(anim.getKeyFrame(animationTime));
        setSize(anim.getKeyFrame(animationTime).getRegionWidth() / 5, anim.getKeyFrame(animationTime).getRegionHeight() / 5);
    }

    protected void createAnimations() {
        anim = new Animation(0.16666667f, atlas.findRegion("death1"), atlas.findRegion("death2"), atlas.findRegion("death3"),
                atlas.findRegion("death4"), atlas.findRegion("death5"), atlas.findRegion("death6"));
    }

    private void resetPosition() {
        setX(owner.getX() + owner.getWidth() / 2 - getWidth() / 2);
        setY(owner.getY() + owner.getHeight() / 2 - getHeight() / 2);
    }
}
