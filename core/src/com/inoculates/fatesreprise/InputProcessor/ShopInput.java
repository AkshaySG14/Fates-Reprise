package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Events.ItemAcquisitionEvents;
import com.inoculates.fatesreprise.Items.Item;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.UI.Shop;

// This is the input or the shop.
public class ShopInput implements InputProcessor {
    GameScreen screen;
    Storage storage;
    Shop shop;

    public ShopInput(GameScreen screen, Storage storage, Shop shop) {
        this.screen = screen;
        this.storage = storage;
        this.shop = shop;
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

    // Checks any input.
    public boolean keyDown(int x) {
        if (screen.isFrozen())
            return true;

        // Moves the cursor in the shop window.
        if (x == storage.moveDown || x == storage.moveUp) {
            checkShopMovement(x);
            // Plays the sound that indicates the player moved the cursor.
            storage.sounds.get("click1").play(1.0f);
        }

        // If the user hits the talk button, attempts to purchases the item.
        if (x == storage.talk)
            buyItem();

        // If the user hits the pause button, goes back to the normal screen.
        if (x == storage.pause) {
            shop.destroy();
            // Plays a sound indicating the user has returned to the game.
            storage.sounds.get("click7").play(1.0f);
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

    // Sets the cursor and by extension the current item being perused depending on whether the user has hit the up or
    // down button.
    private void checkShopMovement(int input) {
        if (input == storage.moveDown)
            shop.movePosition(-1);
        if (input == storage.moveUp)
            shop.movePosition(1);
    }

    // If the user can afford the selected item, and does not have it in the inventory already, buys it.
    private void buyItem() {
        if (storage.coins >= shop.getCost() && !hasItem()) {
            storage.coins -= shop.getCost();
            // Creates an acquisition event so that Daur receives the item. Uses a timer to prevent input spilling into
            // the acquisition event.
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    ItemAcquisitionEvents event = new ItemAcquisitionEvents(screen.map, screen, shop.getItem().getClass(),
                            shop.getShopEvent());
                }
            }, 0.01f);
            // Plays the sound that indicates the player pressed a button.
            storage.sounds.get("click3").play(1.0f);
            shop.end();
        }
        // Causes error sound.
        else
            storage.sounds.get("error").play(1.0f);
    }

    // If the user has an item of the same type, returns true. Else, returns false.
    private boolean hasItem() {
        // Goes through all the items in Daur's inventory, trying to find one that is the same class as the given one.
        for (Item item : storage.items)
            if (item != null && item.getClass() == shop.getItem().getClass())
                return true;
        // Checks all slot items as well.
        if (storage.item1 != null && storage.item1.getClass() == shop.getItem().getClass())
            return true;
        else if (storage.item2 != null && storage.item2.getClass() == shop.getItem().getClass())
            return true;
        else if (storage.item3 != null && storage.item3.getClass() == shop.getItem().getClass())
            return true;
        // Else Daur does NOT have the item; returns false.
        return false;
    }

}
