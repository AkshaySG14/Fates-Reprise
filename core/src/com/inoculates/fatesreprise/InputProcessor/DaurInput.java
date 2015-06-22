package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Daur;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage;

// This is the input processor that allows Daur to act when the user presses a keyboard button.
public class DaurInput implements InputProcessor {
    GameScreen screen;
    Storage storage;
    Daur daur;

    public DaurInput(GameScreen screen, Storage storage) {
        this.screen = screen;
        this.storage = storage;
        daur = screen.daur;
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

    // Interprets the users input in the form of keys pressed and translates it to actions.
    public boolean keyDown(int x) {
        // If the screen is froze, returns immediately, as there can be no input allowed.
        if (screen.isFrozen())
            return true;

        // Pauses the screen, if Daur is not currently stunned.
        if (x == storage.pause && !daur.isStunned()) {
            // Freezes the screen to disallow further input for a short while. This is to avoid the user hitting the
            // pause button too many times.
            screen.freeze();
            // Fades in with white.
            screen.mask.setColor(Color.WHITE);
            screen.mask.setAlpha(1);
            screen.mask.fadeIn(0.5f);
            // Pauses the game, to ensure that no enemy moves during the pause.
            screen.pauseGame();
            // Unfreezes the game after 0.5 seconds.
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    screen.unFreeze();
                }
            }, 0.5f);
            timer.start();
        }

        // If any of the storage slots are pressed, checks how Daur should react.
        if (x == storage.slotOne || x == storage.slotTwo || x == storage.slotThree)
            daur.checkItem(x);

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
}
