package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// These are locked doors that Daur opens with keys..
public abstract class ClosedDoor extends Interactable {
    private int doorNumber;

    public ClosedDoor(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, int direction) {
        super(screen, map, atlas, storage);
    }

    // Moves the door in a direction to simulate opening, then removes the door from the game.
    public void open(int direction) {
        final ClosedDoor door = this;
        final int dir = direction;
        // Uses a for loop to push the door open over the course of 0.1 seconds.
        for (float deltaTime = 0; deltaTime <= 0.16; deltaTime += 0.01)
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
    }

    // Spawns the door, then moves the door in a direction to simulate closing.
    public void close(int direction) {
        screen.interactables.add(this);
        final int dir = direction;
        for (float deltaTime = 0; deltaTime <= 0.16; deltaTime += 0.01)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    displaceDoor(dir);
                }
            }, deltaTime);
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

    abstract void createAnimations();

    abstract void chooseSprite();
}
