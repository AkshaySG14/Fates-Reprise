package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

// This is the event that launches when the player acquires a new item.
public class ItemAcquisitionEvents extends Event {
    Class item;
    Item aqItem;
    String message = null;
    Event starter;
    boolean questItem = false;

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
        // Daur has received the shield spell.
        else if (item.equals(ShieldItem.class)) {
            message = "You have learned the spell Shield! Cast it to repulse enemies and projectiles for a few seconds.";
            aqItem = new ShieldItem(screen.daurAtlases.get(2));
        }
        // Daur has received the wind sickle spell.
        else if (item.equals(WindSickleItem.class)) {
            message = "You have learned the spell Wind Sickle! Use it to cut through both enemies and bushes with great " +
                    "swiftness.";
            aqItem = new WindSickleItem(screen.daurAtlases.get(2));
        }
        // Daur has received the wind sickle spell.
        else if (item.equals(ZephyrsWispItem.class)) {
            message = "You have learned the spell Wind Sickle! Use it to cut through both enemies and bushes with great " +
                    "swiftness.";
            aqItem = new ZephyrsWispItem(screen.daurAtlases.get(2));
        }
        // Daur has received a minor health potion.
        else if (item.equals(MinorHealthPotionItem.class)) {
            message = "You obtained a minor health potion. Use this to recover three hearts.";
            aqItem = new MinorHealthPotionItem(screen.daurAtlases.get(2));
        }
        // Daur has received the Great Hollow Key.
        else if (item.equals(GreatHollowKey.class)) {
            message = "You have received the Great Hollow Key! Use this to open the door to the Great Hollow, where the " +
                    "first sage awaits.";
            aqItem = new GreatHollowKey(screen.daurAtlases.get(2));
            questItem = true;
        }

        // Sets the item's position above Daur, and at the center of Daur.
        aqItem.setPosition(screen.daur.getX() + screen.daur.getWidth() / 2 - aqItem.getWidth() / 2,
                screen.daur.getY() + screen.daur.getHeight() + 2);
        // Adds the item to the rendering list.
        screen.items.add(aqItem);
        // If quest item, adds to the game quest item storage list.
        if (questItem)
            screen.storage.questItems.set(getEnd(), aqItem);
        // Else adds the item to the game storage list.
        else
            screen.storage.items.set(getEnd(), aqItem);
        // Forces Daur to do the item acquisition frame.
        screen.daur.forceState(9);
        message();
    }

    protected void message() {
        switch (stage) {
            case 0:
                Dialogue dialogue = new Dialogue(screen, message, this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                screen.freeze();
                break;
            case 1:
                screen.setText(null, null);
                screen.items.remove(aqItem);
                screen.daur.unStun();
                starter.proceed();
                screen.daur.forceState(0);
                screen.unFreeze();
                break;
        }
    }

    // Gets the end of the game item list.
    private int getEnd() {
        if (questItem) {
            for (Item item : screen.storage.questItems)
                if (item == null)
                    return screen.storage.questItems.indexOf(item);
        }
        else {
            for (Item item : screen.storage.items)
                if (item == null)
                    return screen.storage.items.indexOf(item);
        }
        return -1;
    }
}
