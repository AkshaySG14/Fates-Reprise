package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is a bush block that is used to cordon off the room Daur is in once he has activated a trigger.
public class BushBlock extends Interactable {

    public BushBlock(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, float spawnX, float spawnY) {
        super(screen, map, atlas, storage);
        chooseSprite();
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        setX(spawnX - getWidth() / 2);
        setY(spawnY - getHeight() / 2);
        fadeIn();
    }

    // Fades into view.
    private void fadeIn() {
        Timer timer = new Timer();
        setAlpha(0);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.1f);
            }
        }, 0.1f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.2f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.3f);
            }
        }, 0.3f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.4f);
            }
        }, 0.4f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.5f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.6f);
            }
        }, 0.6f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.7f);
            }
        }, 0.7f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.8f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.9f);
            }
        }, 0.9f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(1);
            }
        }, 1);
        timer.start();
    }

    // Removes self after fading away.
    public void fade() {
        final BushBlock bBlock = this;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.9f);
            }
        }, 0.1f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.2f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.7f);
            }
        }, 0.3f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.6f);
            }
        }, 0.4f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.5f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.4f);
            }
        }, 0.6f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.3f);
            }
        }, 0.7f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.8f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.1f);
            }
        }, 0.9f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.interactables.remove(bBlock);
            }
        }, 1);
        timer.start();
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected boolean overrideCheck() {
        return true;
    }

    protected void tryMove() {

    }

    protected void update(float deltaTime) {

    }

    protected void createAnimations() {}

    protected void chooseSprite() {
        setRegion(atlas.findRegion("bushblock"));
        setSize(getRegionWidth(), getRegionHeight());
    }
}
