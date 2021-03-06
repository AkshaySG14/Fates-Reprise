package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.Worlds.Houses;
import com.inoculates.fatesreprise.Worlds.UnderWorld;

// This is the input processor that allows Daur to act when the user presses a keyboard button.
public class DaurInput implements InputProcessor {
    GameScreen screen;
    Storage storage;

    public DaurInput(GameScreen screen, Storage storage) {
        this.screen = screen;
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

    // Interprets the users input in the form of keys pressed and translates it to actions.
    public boolean keyDown(int x) {
        // If the screen is froze, returns immediately, as there can be no input allowed.
        if (screen.isFrozen())
            return true;

        // Pauses the screen, if Daur is not currently stunned.
        if (x == storage.pause && !screen.daur.isStunned()) {
            // Freezes the screen to disallow further input for a short while. This is to avoid the user hitting the
            // pause button too many times.
            screen.freeze();
            // Fades in with white.
            screen.mask.setColor(Color.WHITE);
            screen.mask.setAlpha(1);
            screen.mask.fadeIn(0.5f);
            // Pauses the game, to ensure that no enemy moves during the pause.
            screen.pauseGame();
            // Unfreezes the game after 0.5 seconds. Creates a new timer to accomplish this.
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    screen.unFreeze();
                }
            }, 0.5f);
            // Plays the pause sound.
            storage.sounds.get("click10").play(1.0f);
            // Sets the volume of the music to be lower.
            storage.setVolume(0.1f);
        }

        // Pauses the screen and goes the map screen, if Daur is not currently stunned. This will only work if Daur is
        // in the upperworld, or is in the underworld and has a map.
        if (x == storage.secondary && !screen.daur.isStunned() && (!(screen.getWorld(storage.map) instanceof UnderWorld) ||
                (screen.getWorld(storage.map) instanceof UnderWorld && storage.hasMap())) &&
                !(screen.getWorld(storage.map) instanceof Houses)) {
            // Freezes the screen to disallow further input for a short while. This is to avoid the user hitting the
            // pause button too many times.
            screen.freeze();
            // Fades in with white.
            screen.mask.setColor(Color.WHITE);
            screen.mask.setAlpha(1);
            screen.mask.fadeIn(0.5f);
            // Pauses the game, to ensure that no enemy moves during the pause.
            screen.goToMap();
            // Unfreezes the game after 0.5 seconds. Creates a new timer to accomplish this.
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    screen.unFreeze();
                }
            }, 0.5f);
            // Plays the map sound.
            storage.sounds.get("click8").play(1.0f);
            // Sets the volume of the music to be lower.
            storage.setVolume(0.1f);
        }

        // If any of the storage slots are pressed, checks how Daur should react.
        if (x == storage.slotOne || x == storage.slotTwo || x == storage.slotThree)
            screen.daur.checkItem(x);

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
