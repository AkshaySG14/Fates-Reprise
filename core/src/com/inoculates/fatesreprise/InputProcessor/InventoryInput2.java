package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Screens.PausedScreenGame2;
import com.inoculates.fatesreprise.Storage.Storage;

// This is the input processor that is responsible for keyboard input during a pause screen.
public class InventoryInput2 implements InputProcessor {
    GameScreen screen;
    PausedScreenGame2 pScreen;
    Storage storage;

    public InventoryInput2(GameScreen screen, Storage storage, PausedScreenGame2 pScreen) {
        this.screen = screen;
        this.pScreen = pScreen;
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

    // Interprets input for the pause screen.
    public boolean keyDown(int x) {
        // Once again, if the screen is frozen, does not allow input.
        if (screen.isFrozen())
            return true;

        // Checks the movement in accordance with the movement key pressed (moves the cursor in accordance with
        // direction).
        if (x == storage.moveDown || x == storage.moveUp || x == storage.moveLeft || x == storage.moveRight)
            checkPausedMovement(x);

        // Checks if the user is attempting to save or quit the game.
        if (x == storage.talk)
            pScreen.checkButtonPressed();

        // The user is attempting to unpause the game.
        if (x == storage.pause) {
            // Virtually the same as the Daur input processor, but changes game back to normal instead of paused.
            screen.freeze();
            screen.mask.setColor(Color.WHITE);
            screen.mask.setAlpha(1);
            screen.mask.fadeIn(0.5f);
            // Unpauses the game itself.
            screen.unPauseGame();
            screen.unFreeze();
            // Plays a sound indicating the user has returned to the game.
            storage.sounds.get("click7").play(1.0f);
            // Sets the volume of the music to be normal.
            storage.setVolume(0.75f);
        }

        // Changes screen back to first screen.
        if (x == storage.secondary) {
            pScreen.changeScreen();
            // Plays a sound accordingly.
            storage.sounds.get("click9").play(1.0f);
        }

        return true;
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

    // Changes the cursor according to the movement. Note that the items are arranged in a grid, and therefore the
    // cursor will move in a grid-like fashion.
    public void checkPausedMovement(int input) {
        // Moves the cursor to the gridspace on the right.
        if (input == storage.moveRight)
            pScreen.changeGrid(1, false);
        // Etc.
        if (input == storage.moveLeft)
            pScreen.changeGrid(-1, false);
        if (input == storage.moveUp)
            pScreen.changeGrid(1, true);
        if (input == storage.moveDown)
            pScreen.changeGrid(-1, true);
    }
}
