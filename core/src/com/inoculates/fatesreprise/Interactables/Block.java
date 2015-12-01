package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// These are the blocks that can be pushed by Daur to solve puzzles.
public abstract class Block extends Interactable {
    private int[] dir;
    private boolean paused = false, limited, triggered = false;
    private int trigger = -1;

    public Block(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, String direction, boolean limited) {
        super(screen, map, atlas, storage);
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.storage = storage;
        // Sets direction in which the block can be pushed and whether or not the block can be pushed infinitely.
        this.dir = getDirection(direction);
        this.limited = limited;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
    }

    // Secondary constructor, as some blocks need to be pushed into a certain direction.
    public Block(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, String direction, boolean limited,
                 int trigger) {
        super(screen, map, atlas, storage);
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.storage = storage;
        // Sets direction in which the block can be pushed and whether or not the block can be pushed infinitely.
        this.dir = getDirection(direction);
        this.limited = limited;
        this.trigger = trigger;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected boolean overrideCheck() {
        return true;
    }

    // Moves the block towards the direction it is set to.
    public void move(int direction) {
        float deltaTime = 0;
        // Checks if Daur is facing in the same direction as the block moves. Also checks if the block is able to be
        // moved (paused).
        if (!isFacing(direction) || paused)
            return;

        paused = true;
        // If the block has no specific directional trigger, or has been pushed into the same direction it triggers,
        // sets triggered to true.
        if (trigger == -1 || trigger == direction)
            triggered = true;
        // If successfully triggered, checks if something has been triggered in the game.
        if (triggered)
            screen.checkClear(this);

        // Moves in accordance with the direction of Daur.
        switch (direction) {
            // Slowly moves the block over to the tile on the right.
            case RIGHT:
                // Increments the position of the block by a very marginal amount over a period of time to simulate
                // movement.
                for (float x = getX(); x <= getX() + layer.getTileWidth(); x++) {
                    final float newX = x;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            setX(newX);
                        }
                    }, deltaTime);
                    deltaTime += 0.035f;
                }
                break;
            // Same but for left.
            case LEFT:
                for (float x = getX(); x >= getX() - layer.getTileWidth(); x--) {
                    final float newX = x;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            setX(newX);
                        }
                    }, deltaTime);
                    deltaTime += 0.035f;
                }
                break;
            case UP:
                for (float y = getY(); y <= getY() + layer.getTileHeight(); y++) {
                    final float newY = y;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            setY(newY);
                        }
                    }, deltaTime);
                    deltaTime += 0.035f;
                }
                break;
            case DOWN:
                for (float y = getY(); y >= getY() - layer.getTileHeight(); y--) {
                    final float newY = y;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            setY(newY);
                        }
                    }, deltaTime);
                    deltaTime += 0.035f;
                }
                break;
        }

        // If the block has unlimited movement, sets it able to move again after one second.
        if (!limited) {
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    paused = false;
                }
            }, 1);
        }
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
    }

    // Checks if any of the directions the block moves in and the way Daur is facing are congruent.
    private boolean isFacing(int direction) {
        // Checks if any of the possible direction movements are equivalent to Daur's direction.
        for (int i = 0; i < dir.length; i ++)
            if (direction == dir[i])
                return orientationCheck(direction);

        return false;
    }

    // Checks if Daur is in the proper position to push (e.g. is to the left of the block and is facing right).
    private boolean orientationCheck(int direction) {
        switch (direction) {
            default:
                return false;
            case -1:
                // If Daur is facing left, he must also be to the right of the block.
                return screen.daur.getX() + screen.daur.getWidth() / 2 > getX() + getWidth();
            case 1:
                // If Daur is facing right, he must be to the left of the block.
                return screen.daur.getX() + screen.daur.getWidth() / 2 < getX();
            case -2:
                // Etc.
                return screen.daur.getY() + screen.daur.getHeight() / 2 > getY() + getHeight();
            case 2:
                return screen.daur.getY() + screen.daur.getHeight() / 2 < getY();

        }
    }

    // Gets the direction from the string list of directions.
    private int[] getDirection(String direction) {
        // Initializes loop counter and a blank array of possible directions.
        int count = 0;
        int[] dirList = {0, 0, 0, 0};

        // If the character is not a comma or a space, registers it as a proper character.
        for (int i = 0; i < direction.length(); i ++)
            if (direction.charAt(i) != ',' && direction.charAt(i) != ' ') {
                // Adds the numerical value of the character to the array.
                dirList[count] = Character.getNumericValue(direction.charAt(i));
                count ++;
            }


        // Creates a blank array that converts all the tile direction values into real ones.
        int[] dirFinal = new int[count];

        for (int i = 0; i < count; i ++)
            dirFinal[i] = getRealDirection(dirList[i]);

        return dirFinal;
    }

    private int getRealDirection(int dir) {
        switch (dir) {
            default:
                return 0;
            case 0:
                return 1;
            case 1:
                return -1;
            case 2:
                return 2;
            case 3:
                return -2;
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    abstract void createAnimations();

    abstract void chooseSprite();
}
