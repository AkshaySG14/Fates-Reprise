package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;

public class Splash extends Effect {
    Animation anim;
    Character owner;

    // Splash effect for drowning or jumping.
    public Splash(GameScreen screen, TiledMap map, TextureAtlas atlas, Character owner) {
        super(screen, map, atlas, true);
        this.owner = owner;
        final GameScreen fScreen = screen;
        final Splash splash = this;
        setPosition(owner.getCX() - getWidth() / 2, owner.getCY() - getHeight() / 2);
        // Removes self after animation is complete (0.45 seconds).
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                fScreen.effects.remove(splash);
            }
        }, 0.3f);
    }

    protected void update(float deltaTime) {
        chooseSprite();
        updateTime(deltaTime);
    }

    protected void chooseSprite() {
        createAnimations();
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }

    // Splash animation
    protected void createAnimations() {
        anim = new Animation(0.1f, atlas.findRegion("splash1"), atlas.findRegion("splash2"),
                atlas.findRegion("splash3"));
    }

    public void setAnimationTime(float time) {
        animationTime = time;
    }

}
