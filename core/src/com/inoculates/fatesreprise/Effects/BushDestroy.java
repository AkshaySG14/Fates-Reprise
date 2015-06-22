package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the effect that spawns when a bush is destroyed by Daur.
public class BushDestroy extends Effect {
    Animation anim;
    int type = 0;

    public BushDestroy(GameScreen screen, TiledMap map, TextureAtlas atlas, int type) {
        super(screen, map, atlas, false);
        this.type = type;
        destroy();
    }

    // This is the method that slowly increases the transparency of the effect until it is finally removed.
    private void destroy() {
        final BushDestroy self = this;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.75f);
            }
        }, 0.125f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.25f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.25f);
            }
        }, 0.375f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.effects.remove(self);
            }
        }, 0.5f);
        timer.start();
    }

    protected void chooseSprite() {
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }

    // Note that the animation is dependent on the type of bush that was destroyed. The only animation this effect has is
    // the destruction animation.
    protected void createAnimations() {
        switch (type) {
            case 0:
                anim = new Animation(0.125f, atlas.findRegion("springbushdestroy1"), atlas.findRegion("springbushdestroy2"), atlas.findRegion("springbushdestroy3"), atlas.findRegion("springbushdestroy4"));
                break;
            case 1:
                anim = new Animation(0.125f, atlas.findRegion("summerbushdestroy1"), atlas.findRegion("summerbushdestroy2"), atlas.findRegion("summerbushdestroy3"), atlas.findRegion("summerbushdestroy4"));
                break;
            case 2:
                anim = new Animation(0.125f, atlas.findRegion("winterbushdestroy1"), atlas.findRegion("winterbushdestroy2"), atlas.findRegion("winterbushdestroy3"), atlas.findRegion("winterbushdestroy4"));
                break;
            case 3:
                anim = new Animation(0.125f, atlas.findRegion("winterbushdestroy1"), atlas.findRegion("winterbushdestroy2"), atlas.findRegion("winterbushdestroy3"), atlas.findRegion("winterbushdestroy4"));
                break;
        }
    }
}
