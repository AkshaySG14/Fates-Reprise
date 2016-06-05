package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Screens.OverworldMapScreen;
import com.inoculates.fatesreprise.Screens.UnderworldMapScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is the input processor that is responsible for keyboard input during the dungeon map screen.
public class UnderworldMapInput implements InputProcessor {
    GameScreen screen;
    UnderworldMapScreen uScreen;
    Storage storage;

    public UnderworldMapInput(GameScreen screen, Storage storage, UnderworldMapScreen uScreen) {
        this.screen = screen;
        this.uScreen = uScreen;
        this.storage = storage;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        return true;
    }

    public boolean touchDragged(int x, int y, int z) {
        return true;
    }

    public boolean keyTyped(char x) {
        return true;
    }

    public boolean scrolled(int x) {
        return true;
    }

    // Interprets input for the underworld map screen.
    public boolean keyDown(int x) {
        // Once again, if the screen is frozen, does not allow input.
        if (screen.isFrozen())
            return true;

        // Checks the movement in accordance with the movement key pressed (moves the cursor in accordance with
        // direction).
        if (x == storage.moveDown || x == storage.moveUp)
            checkMapMovement(x);

        // Goes back to the normal screen.
        if (x == storage.secondary) {
            // Virtually the same as the Daur input processor, but changes game back to normal instead of paused.
            screen.freeze();
            screen.mask.setColor(Color.WHITE);
            screen.mask.setAlpha(1);
            screen.mask.fadeIn(0.5f);
            // Unpauses the game itself.
            screen.unPauseGame();
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    screen.unFreeze();
                }
            }, 0.5f);
            // Plays a sound accordingly.
            storage.sounds.get("click7").play(1.0f);
            // Sets the volume of the music to be normal.
            storage.setVolume(0.75f);
            return true;
        }
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        return true;
    }

    public boolean keyUp(int x) {
        return true;
    }

    public boolean touchUp(int x, int y, int z, int a) {
        return true;
    }

    // Changes the floor in accordance with button pressed.
    public void checkMapMovement(int input) {
        // Moves the floor up or down.
        if (input == storage.moveUp)
            uScreen.changeFloor(1);
        if (input == storage.moveDown)
            uScreen.changeFloor(-1);
    }

}
