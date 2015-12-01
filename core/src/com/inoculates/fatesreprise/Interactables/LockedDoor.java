package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// These are locked doors that Daur opens with keys..
public abstract class LockedDoor extends Interactable {
    private int doorNumber;

    public LockedDoor(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, int doorNumber) {
        super(screen, map, atlas, storage);
        this.doorNumber = doorNumber;
    }

    // Moves the door in a direction to simulate unlocking, then removes the door from the game.
    public void open(int direction) {
        final LockedDoor door = this;
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
        storage.setUnlocked(doorNumber);
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
        return storage.lockedDoors[doorNumber];
    }

    // Returns true if Daur has at least one key of the dungeon type.
    public boolean canOpen() {
        switch (storage.dungeon) {
            default:
                return false;
            case 0:
                return storage.GHKeys > 0;
        }
    }

    abstract void createAnimations();

    abstract void chooseSprite();
}
