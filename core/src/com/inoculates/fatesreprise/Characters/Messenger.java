package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;

public class Messenger extends Character {

    Animation idle;
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("idle1"), FD2 = atlas.findRegion("idle2");

    // This class is merely made for the cut scenes in the game. Used for exposition purposes.
    public Messenger(GameScreen screen, TiledMap map, TextureAtlas atlas) {
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
            Timer timer = new Timer();
            for (float i = 0; i <= 1; i += 0.1) {
                final float o = i;
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setAlpha(o);
                    }
                }, i);
            }
            timer.start();
        }
        // Opposite of previous.
        else {
            setAlpha(1);
            Timer timer = new Timer();
            for (float i = 1; i >= 0; i -= 0.1) {
                final float o = i;
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setAlpha(o);
                    }
                }, 1 - i);
            }
            timer.start();
        }
    }
}
