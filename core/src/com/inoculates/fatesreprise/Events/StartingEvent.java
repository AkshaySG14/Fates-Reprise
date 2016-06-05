package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Messenger;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

public class StartingEvent extends Event {
    Messenger messenger;

    // This is the event that immediately proceeds the the starting messenger event.
    public StartingEvent(TiledMap map, GameScreen screen) {
        super(screen, map);
        startEvent();
        // Stops the overworld theme, which will automatically play.
        screen.storage.music.get("overworldtheme").stop();
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
                    // Plays the fade sound.
                    screen.storage.sounds.get("mysterious").play(1.0f);
                    messenger.fade(true);
                    messenger.setPosition(x - messenger.getWidth() / 2, y - messenger.getHeight() / 2);
                    screen.characters1.add(messenger);
                }
            }

        // Sets the direction of Daur to up and freezes the screen.
        screen.daur.setDirection(2);
        screen.freeze();
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                message();
            }
        }, 2);
    }

    protected void message() {
        final Event event = this;
        Dialogue dialogue;
        ItemAcquisitionEvents aqItems;

        // Starts the series of events. Similar to the messenger events.
        switch (stage) {
            case 0:
                dialogue = new Dialogue(screen, "Oh dear. It appears I've forgotten something. To aid you in " +
                        "your quest. I have two items of some considerable worth to give to you. The first of which is a " +
                        "runed sword. Here.", this);
                screen.setText(dialogue, dialogue.getBackground());
                break;
            case 1:
                // Gives Daur the runed sword.
                aqItems = new ItemAcquisitionEvents(map, screen, BasicSwordItem.class, this);
                break;
            case 2:
                dialogue = new Dialogue(screen, "Next, I will teach you a very basic spell. This will come in handy for " +
                        "shocking enemies, so that you may destroy them more easily.",
                        event);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                break;
            case 3:
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
            case 5:
                // Ends the event by fading the messenger and unfreezing the screen, as well as Daur.
                messenger.fade(false);
                // Plays the fade sound.
                screen.storage.sounds.get("mysterious").play(1.0f);
                screen.setText(null, null);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.daur.unStun();
                        screen.daur.setRespawnPoint();
                        screen.unFreeze();
                        screen.unPauseGame();
                        screen.characters1.remove(messenger);
                        // Starts the overworld theme.
                        screen.storage.music.get("overworldtheme").play();
                        screen.storage.music.get("overworldtheme").setLooping(true);
                        screen.storage.music.get("overworldtheme").setVolume(0.75f);
                    }
                }, 1);
        }
    }
}
