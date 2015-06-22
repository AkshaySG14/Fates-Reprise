package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Daur;
import com.inoculates.fatesreprise.Characters.Messenger;
import com.inoculates.fatesreprise.Items.BasicSwordItem;
import com.inoculates.fatesreprise.Items.ConcussiveShotItem;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

public class StartingEvent extends Event {
    Messenger messenger;

    // This is the event that immediately proceeds the the starting messenger event.
    public StartingEvent(TiledMap map, GameScreen screen) {
        super(screen, map);
        startEvent();
    }

    protected void startEvent() {
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();

                // Creates and spawn the messenger in the proper place.
                if (object.getProperties().containsKey("msgspawn")) {
                    messenger = new Messenger(screen, map, screen.characterAtlases.get(0));
                    messenger.fade(true);
                    messenger.setPosition(x - messenger.getWidth() / 2, y - messenger.getHeight() / 2);
                    screen.characters1.add(messenger);
                }
            }

        // Sets the direciton of Daur to up and freezes the screen.
        screen.daur.setDirection(2);
        screen.freeze();

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                message();
            }
        }, 1);
        timer.start();
    }

    protected void message() {
        final Timer timer = new Timer();
        final Event event = this;
        Dialogue dialogue;
        ItemAcquisitionEvents aqItems;

        // Starts the series of events. Similar to the messenger events.
        switch (stage) {
            case 5:
                dialogue = new Dialogue(screen, "Oh dear. It appears I've forgotten something. To aid you in " +
                        "your quest. I have two items of some considerable worth to give to you. The first of which is a " +
                        "runed sword. Here.", this);
                screen.setText(dialogue, dialogue.getBackground());
                break;
            case 0:
                // Gives Daur the runed sword.
                aqItems = new ItemAcquisitionEvents(map, screen, BasicSwordItem.class, this);
                break;
            case 3:
                dialogue = new Dialogue(screen, "Next, I will teach you a very basic spell. This will come in handy for " +
                        "shocking enemies, so that you may destroy them more easily.",
                        event);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                break;
            case 1:
                // Gives Daur the concussive shot item.
                aqItems = new ItemAcquisitionEvents(map, screen, ConcussiveShotItem.class, this);
                break;
            case 4:
                dialogue = new Dialogue(screen, "With the two gifts that I have bequeathed, you should be able to start " +
                        "your journey. Once again I bid you good luck, and farewell.",
                        event);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                break;
            case 2:
                // Ends the event by fading the messenger and unfreezing the screen, as well as Daur.
                messenger.fade(false);
                screen.setText(null, null);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.daur.unStun();
                        screen.unFreeze();
                        screen.characters1.remove(messenger);
                    }
                }, 1);
                timer.start();
        }
    }
}
