package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
import com.inoculates.fatesreprise.Screens.BeginningScreen;
import com.inoculates.fatesreprise.Screens.DefeatScreen;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is the input processor that is responsible for keyboard input during the beginning screen.
public class BeginningInput implements InputProcessor {
    BeginningScreen bScreen;
    Storage storage;

    public BeginningInput(Storage storage, BeginningScreen bScreen) {
        this.bScreen = bScreen;
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

    // Interprets input for the beginning screen.
    public boolean keyDown(int x) {
        // If the screen is transitioning, returns.
        if (bScreen.getTransitioning())
            return false;
        // Checks the movement in accordance with the movement key pressed (moves the cursor in accordance with
        // direction).
        if (x == storage.moveDown || x == storage.moveUp)
            checkPausedMovement(x);
        // Executes a decision based on the current decider position.
        if (x == storage.talk)
            bScreen.executeDecision();
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

    // Highlights the text above or below the current highlighted one.
    public void checkPausedMovement(int input) {
        if (input == storage.moveUp)
            bScreen.moveUp();
        if (input == storage.moveDown)
            bScreen.moveDown();
    }
}
