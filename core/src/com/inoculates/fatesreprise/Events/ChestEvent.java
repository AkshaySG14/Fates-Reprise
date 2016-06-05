package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Interactables.Chest;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

// This is the event responsible for the dialogue created when Daur wants to read a book.
public class ChestEvent extends Event {
    Chest chest;
    String message = null;
    Item aqItem;
    boolean questItem;

    public ChestEvent(TiledMap map, GameScreen screen, Chest chest) {
        super(screen, map);
        this.chest = chest;
        startEvent();
    }

    protected void startEvent() {
        // Note that the chest event first checks if the item obtained is a quest item. This is because quest items are
        // added to the quest item list rather than the normal one.
        questItem = (chest.getContents() instanceof OakStaff || chest.getContents() instanceof GreatHollowSmallKey ||
                chest.getContents() instanceof Compass || chest.getContents() instanceof Map ||
        chest.getContents() instanceof GreatHollowBossKey);
        // Sends the necessary dialogue and the item.
        setMessage();
        // Plays the music for the chest event.
        screen.storage.sounds.get("powerup1").play(1.0f);
    }

    // Informs the player that Daur has acquired the chest's item.
    private void setMessage() {
        message = "Daur has acquired " + chest.getItemName();
        aqItem = chest.getContents();
        // Sets the item's position above Daur, and at the center of Daur.
        aqItem.setPosition(screen.daur.getX() + screen.daur.getWidth() / 2 - aqItem.getWidth() / 2,
                screen.daur.getY() + screen.daur.getHeight() + 2);
        // Adds the item to the rendering list.
        screen.items.add(aqItem);
        // If the item is a coin, adds it to the storage coins. Also prevents overflow.
        if (aqItem instanceof CoinItem) {
            if (screen.storage.coins + chest.acquireContents() < 999)
                screen.storage.setCoins(screen.storage.coins + chest.acquireContents());
            else
                screen.storage.setCoins(999);
        }
        // If the item is a heart piece, adds it to the storage heart pieces.
        else if (aqItem instanceof HeartPiece)
            screen.daur.addHeartPiece();
        // Else adds the item to the GAME storage list if the item is not a coin or a heart piece.
        else {
            if (questItem) {
                // Note that small keys, maps, or compasses are not added to the inventory of Daur.
                if (!(aqItem instanceof GreatHollowSmallKey || aqItem instanceof Compass || aqItem instanceof Map)) {
                    screen.storage.questItems.set(getEnd(), aqItem);
                    // Also advance events.
                    advanceEvents();
                }
                // Adds the key depending on the type of item it is.
                else if (aqItem instanceof GreatHollowSmallKey)
                    screen.storage.addKey(aqItem);
                    // Adds the compass based on the dungeon.
                else if (aqItem instanceof Compass)
                    screen.storage.addCompass();
                    // Adds the Map based on the dungeon.
                else if (aqItem instanceof Map)
                    screen.storage.addMap();
            } else
                screen.storage.items.set(getEnd(), aqItem);
        }
        // Forces Daur to do the item acquisition frame.
        screen.daur.forceState(9);
        message();
    }

    // Creates the dialogue itself.
    protected void message() {
        switch (stage) {
            case 0:
                Dialogue dialogue = new Dialogue(screen, message, this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                screen.daur.freeze();
                screen.freeze();
                break;
            case 1:
                screen.setText(null, null);
                screen.items.remove(aqItem);
                screen.daur.unStun();
                screen.daur.forceState(0);
                screen.unFreeze();
                break;
        }
    }

    // Gets the end of the game item list. This differs depending on whether it is a quest item or not.
    private int getEnd() {
        if (questItem) {
            for (Item item : screen.storage.questItems)
                if (item == null)
                    return screen.storage.questItems.indexOf(item);
        } else {
            for (Item item : screen.storage.items)
                if (item == null)
                    return screen.storage.items.indexOf(item);
        }

        return -1;
    }

    // If the item acquired was a quest item, certain elements in the story may advance.
    private void advanceEvents() {
        // If Daur has acquired an oak staff, advances the fairy queen stage.
        if (aqItem instanceof OakStaff)
            screen.storage.setFairyQueenStage();
    }
}
