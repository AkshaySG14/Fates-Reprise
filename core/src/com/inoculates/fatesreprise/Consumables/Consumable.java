package com.inoculates.fatesreprise.Consumables;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.AdvSprite;
import com.inoculates.fatesreprise.Effects.Shadow;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the consumable super class that serves as the backbones for every consumable (items that are dropped when enemies
// are killed / bushes are destroyed).

public class Consumable extends AdvSprite {
    protected TiledMapTileLayer layer;
    protected GameScreen screen;
    protected Vector2 vel = new Vector2();
    // Shadow that is used by every consumable.
    private Shadow shadow;

    // Renders the frame, and sets the size accordingly.
    public Consumable(GameScreen screen, TiledMap map, TextureRegion texture, float spawnX, float spawnY) {
        super(texture);
        this.layer = (TiledMapTileLayer) map.getLayers().get(2);
        this.screen = screen;
        setSize(texture.getRegionWidth() * 4 / 5, texture.getRegionHeight() * 4 / 5);
        setPosition(spawnX - getWidth() / 2, spawnY - getHeight() / 2);
        // Adds shadow so that it appears as though the consumable is in flight.
        shadow = new Shadow(screen, map, screen.miscAtlases.get(1), this, getX(), getY(), 0.4f);
        screen.effects.add(shadow);
        fall();
        // Sets a timer to fade the consumable away after 10 seconds. Note that it first signals that it is fading.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                fade();
            }
        }, 7);
    }

    // Moves the consumable upwards first, then down, so it appears as though it has been dropped by the enemy.
    private void fall() {
        // Sets the initial velocity upwards.
        vel.y = 1;
        // Sets the position of the shadow to the initial position.
        shadow.setPosition(getX() + getWidth() / 2 - shadow.getWidth() / 2, getY());
        // After about 0.1 seconds of travelling upwards, the consumable will fall down.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.y = -1;
            }
        }, 0.1f);
    }

    // Fades out of view after 10 seconds.
    private void fade() {
        final Consumable self = this;
        // Flashes alpha for 3 seconds, to warn the player that the consumable is about to fade.
        setAlpha(0.5f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(1);
            }
        }, 0.5f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 1);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(1);
            }
        }, 1.5f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 2);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(1);
            }
        }, 2.5f);
        // Removes the consumable from the game after the full 10 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.consumables.remove(self);
            }
        }, 3);
    }

    public void draw(Batch batch) {
        super.draw(batch);
        if (!screen.isPaused())
            update();
    }

    protected void update() {
        tryMove();
        // If the consumable is finished falling (has reached its shadow), stops the consumable from moving.
        if (getY() < shadow.getY()) {
            setY(shadow.getY());
            vel.x = 0;
            vel.y = 0;
            screen.effects.remove(shadow);
        }
    }

    protected boolean checkOutOfBounds() {
        if ((getX() + getWidth() > screen.camera.position.x + screen.camera.viewportWidth / 2 && vel.x > 0)
                || (getX() < screen.camera.position.x - screen.camera.viewportWidth / 2 && vel.x < 0))
            return true;
        if ((getY() + getHeight() > screen.camera.position.y + screen.camera.viewportHeight / 2 - 16 && vel.y > 0)
                || (getY() < screen.camera.position.y - screen.camera.viewportHeight / 2) && vel.y < 0) {
            return true;
        }
        else
            return false;
    }

    private void tryMove() {
        float oldX = getX(), oldY = getY();

        setX(getX() + vel.x);

        if (checkOutOfBounds()) {
            setX(oldX);
            vel.x = 0;
        }

        setY(getY() + vel.y);

        if (checkOutOfBounds()) {
            setY(oldY);
            vel.y = 0;
        }
    }

    // Removes the consumable and its shadow from the game.
    public void consume() {
        screen.effects.remove(shadow);
        screen.consumables.remove(this);
    }
}
