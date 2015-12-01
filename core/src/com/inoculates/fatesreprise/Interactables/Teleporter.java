package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is a teleporter that teleports Daur to its corresponding teleporter.
public class Teleporter extends Interactable {
    private Object dungeonStorage;
    private Animation animation;
    private float animationTime = 0;

    public Teleporter(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, Object dungeonStorage) {
        super(screen, map, atlas, storage);
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.storage = storage;
        // Gets the dungeon storage (first dungeon, second dungeon, etc.) that this teleporter belongs to. This is so
        // that the teleporter will activate the proper dungeon.
        this.dungeonStorage = dungeonStorage;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        // Spawns using fading animation.
        fadeIn();
    }

    // Fade in animation for the teleporter.
    private void fadeIn() {
        // Fades in over the course of 0.5 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.1f);
            }
        }, 0.05f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.1f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.3f);
            }
        }, 0.15f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.4f);
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.25f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.6f);
            }
        }, 0.3f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.7f);
            }
        }, 0.35f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.4f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.9f);
            }
        }, 0.45f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(1);
            }
        }, 0.5f);
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected boolean overrideCheck() {
        return true;
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    protected void update(float time) {
        animationTime += time;
    }

    protected void createAnimations() {
        animation = new Animation(0.25f, atlas.findRegion("teleporter1"), atlas.findRegion("teleporter2"),
                atlas.findRegion("teleporter3"), atlas.findRegion("teleporter4"));
    }

    protected void chooseSprite() {
        setRegion(animation.getKeyFrame(animationTime, true));
        setSize(getRegionWidth(), getRegionHeight());
    }
}
