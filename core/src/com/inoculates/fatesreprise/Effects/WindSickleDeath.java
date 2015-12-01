package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Spells.WindSickle;

// This is the effect that spawns when a sickle collides with a wall..
public class WindSickleDeath extends Effect {
    Animation anim;

    public WindSickleDeath(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, false);
        destroy();
    }

    // This is the method that slowly increases the transparency of the effect until it is finally removed.
    private void destroy() {
        final WindSickleDeath self = this;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.75f);
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
                setAlpha(0.25f);
            }
        }, 0.375f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.effects.remove(self);
            }
        }, 0.5f);
    }

    protected void chooseSprite() {
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 3 / 5,
                anim.getKeyFrame(animationTime, true).getRegionHeight() * 3 / 5);
    }

    protected void createAnimations() {
        anim = new Animation(0.125f, atlas.findRegion("sickledeath1"), atlas.findRegion("sickledeath2"));
    }
}
