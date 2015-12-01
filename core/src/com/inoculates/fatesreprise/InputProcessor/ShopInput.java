package com.inoculates.fatesreprise.InputProcessor;

import com.badlogic.gdx.InputProcessor;
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
        if (x == storage.moveDown || x == storage.moveUp)
            checkShopMovement(x);

        // If the user hits the talk button, purchases the item.
        if (x == storage.talk)
            buyItem();

        // If the user hits the pause button, goes back to the normal screen.
        if (x == storage.pause)
            shop.destroy();

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

    // If the user can afford the selected item, buys it.
    private void buyItem() {
        if (storage.coins >= shop.getCost()) {
            storage.coins -= shop.getCost();
            // Simutaneously adds the item to Daur's inventory, and removes it from the shop.
            storage.items.add(shop.getItem());
            shop.removeItem();
        }
    }

}
