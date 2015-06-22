package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Items.BasicSwordItem;
import com.inoculates.fatesreprise.Items.ConcussiveShotItem;
import com.inoculates.fatesreprise.Items.Item;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

// This is the event that launches when the player acquires a new item.
public class ItemAcquisitionEvents extends Event {
    Class item;
    Item aqItem;
    String message = null;
    Event starter;

    public ItemAcquisitionEvents(TiledMap map, GameScreen screen, Class item, Event event) {
        super(screen, map);
        this.item = item;
        starter = event;
        startEvent();
    }

    protected void startEvent() {
        setMessage();
    }

    // Creates and adds the item acquired to the rendering list.
    private void setMessage() {
        // Daur has received the runed sword.
        if (item.equals(BasicSwordItem.class)) {
            message = "You have received a Runed Sword! Use it to smite your enemies.";
            aqItem = new BasicSwordItem(screen.daurAtlases.get(2));
        }
        // Daur has received the concussive shot spell.
        else if (item.equals(ConcussiveShotItem.class)) {
            message = "You have learned the spell Concussive Shot! Cast it to stun enemies for a short time.";
            aqItem = new ConcussiveShotItem(screen.daurAtlases.get(2));
        }

        // Sets the item's position above Daur, and at the center of Daur.
        aqItem.setPosition(screen.daur.getX() + screen.daur.getWidth() / 2 - aqItem.getWidth() / 2,
                screen.daur.getY() + screen.daur.getHeight() + 2);
        // Adds the item to the rendering list.
        screen.items.add(aqItem);
        // Adds the item to the GAME storage list.
        screen.storage.items.set(getEnd(), aqItem);
        // Forces Daur to do the item acquisition frame.
        screen.daur.forceState(8);
        message();
    }

    protected void message() {
        switch (stage) {
            case 0:
                Dialogue dialogue = new Dialogue(screen, message, this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                break;
            case 1:
                screen.setText(null, null);
                screen.items.remove(aqItem);
                screen.daur.unStun();
                starter.proceed();
                screen.daur.forceState(0);
                break;
        }
    }

    // Gets the end of the game item list.
    private int getEnd() {
        for (Item item : screen.storage.items)
            if (item == null)
                return screen.storage.items.indexOf(item);
        return -1;
    }
}
