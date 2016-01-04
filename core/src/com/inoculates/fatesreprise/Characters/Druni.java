package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;

public class Druni extends Character {

    Animation idle;
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("druni1"), FD2 = atlas.findRegion("druni2");

    // This first sage of the game.
    public Druni(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, screen.storage);
        setSize(FD1.getRegionWidth() * 4 / 5, FD1.getRegionHeight() * 4 / 5);
    }

    protected void update(float deltaTime) {

    }

    protected void chooseSprite() {
        setRegion(idle.getKeyFrame(animationTime, true));
        setSize(idle.getKeyFrame(animationTime, true).getRegionWidth() * 4 / 5, idle.getKeyFrame(animationTime, true).getRegionHeight() * 4 / 5);
    }

    protected void createAnimations() {
        idle = new Animation(0.5f, FD1, FD2);
    }

    protected boolean priorities(int cState) {
        return false;
    }

    protected boolean overrideCheck() {
        return false;
    }

    // Method that fades in and out.
    public void fade(boolean in) {
        if (in) {
            // Sets messenger to transparent.
            setAlpha(0);
            // Increases messenger to opaque over a period of time.
            for (float i = 0; i <= 1; i += 0.1) {
                final float o = i;
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setAlpha(o);
                    }
                }, i);
            }
        }
        // Opposite of previous.
        else {
            setAlpha(1);
            for (float i = 1; i >= 0; i -= 0.1) {
                final float o = i;
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setAlpha(o);
                    }
                }, 1 - i);
            }
        }
    }
}
