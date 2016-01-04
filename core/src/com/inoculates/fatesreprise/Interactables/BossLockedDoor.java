package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// These are boss doors doors that Daur opens with boss keys..
public abstract class BossLockedDoor extends Interactable {
    public BossLockedDoor(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage) {
        super(screen, map, atlas, storage);
    }

    // Moves the door in a direction to simulate unlocking, then removes the door from the game.
    public void open(int direction) {
        final BossLockedDoor door = this;
        final int dir = direction;
        // Uses a for loop to push the door open oer the course of 0.1 seconds.
        for (float deltaTime = 0; deltaTime <= 0.1; deltaTime += 0.01)
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                displaceDoor(dir);
            }
        }, deltaTime);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.interactables.remove(door);
            }
        }, 0.1f);
        // Informs the persistent data storage that this door has been unlocked.
        storage.setBossUnlocked();
    }

    // Moves the door in accordance with the direction given.
    private void displaceDoor(int direction) {
        switch (direction) {
            case 1:
                setX(getX() + 1);
                break;
            case -1:
                setX(getX() - 1);
                break;
            case 2:
                setY(getY() + 1);
                break;
            case -2:
                setY(getY() - 1);
                break;
        }
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected boolean overrideCheck() {
        return true;
    }

    protected void tryMove() {

    }

    // Tells the game whether this door is unlocked by using the persistent data storage.
    public boolean isUnlocked() {
        return storage.bossDoors[storage.dungeon];
    }

    abstract void createAnimations();

    abstract void chooseSprite();
}
