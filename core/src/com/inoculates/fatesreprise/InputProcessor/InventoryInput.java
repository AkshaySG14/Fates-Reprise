package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Items.Item;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Screens.PausedScreenGame;
import com.inoculates.fatesreprise.Storage.Storage;

// This is the input processor that is responsible for keyboard input during a pause screen.
public class InventoryInput implements InputProcessor {
    GameScreen screen;
    PausedScreenGame pScreen;
    Storage storage;

    public InventoryInput(GameScreen screen, Storage storage, PausedScreenGame pScreen) {
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

        // Switches the item out in accordance with the storage slot pressed.
        if (x == storage.slotOne || x == storage.slotTwo || x == storage.slotThree)
            checkSwitchItem(x);

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

        // Goes to the secondary pause screen.
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

    // Checks if the user is trying to bring a new item into one of three primary item slots.
    public void checkSwitchItem(int input) {
        // Ensures that the pause screen actually exists.
        if (pScreen == null)
            return;
        // If the user is trying to switch a new item into the first slot.
        if (input == storage.slotOne) {
            // If there exists no item currently in slot one, and the item the cursor is selecting exists, sets the
            // slot one item to the highlighted item. Also removes the new slot one item from the item list.
            if (storage.item1 == null && pScreen.getCurrentItem() != null) {
                storage.setItem1(pScreen.getCurrentItem());
                storage.items.set(pScreen.getCurrentPosition(), null);
            }
            // Else switches the places of the two items, as there exists an item in the first slot. If there is no item
            // currently highlighted by the cursor, will simply remove the slot one item.
            else {
                Item item = storage.item1;
                storage.setItem1(pScreen.getCurrentItem());
                storage.items.set(pScreen.getCurrentPosition(), item);
            }
            // Gets the new current item, whether it be a null item or the old slot one item.
            pScreen.setCurrentItem();
        }
        // Same for the other slots.
        if (input == storage.slotTwo) {
            if (storage.item2 == null && pScreen.getCurrentItem() != null) {
                storage.setItem2(pScreen.getCurrentItem());
                storage.items.set(pScreen.getCurrentPosition(), null);
            } else {
                Item item = storage.item2;
                storage.setItem2(pScreen.getCurrentItem());
                storage.items.set(pScreen.getCurrentPosition(), item);
            }
            pScreen.setCurrentItem();
        }
        if (input == storage.slotThree) {
            if (storage.item3 == null && pScreen.getCurrentItem() != null) {
                storage.setItem3(pScreen.getCurrentItem());
                storage.items.set(pScreen.getCurrentPosition(), null);
            } else {
                Item item = storage.item3;
                storage.setItem3(pScreen.getCurrentItem());
                storage.items.set(pScreen.getCurrentPosition(), item);
            }
            pScreen.setCurrentItem();
        }
        // Plays a sound to acknowledge the user has swapped an item out.
        storage.sounds.get("click2").play(1.0f);
    }

}
