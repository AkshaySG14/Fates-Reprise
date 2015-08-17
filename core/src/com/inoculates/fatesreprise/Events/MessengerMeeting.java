package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Daur;
import com.inoculates.fatesreprise.Characters.Messenger;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

// This is the class responsible for the beginning cutscene.
public class MessengerMeeting extends Event {
    Messenger messenger;

    public MessengerMeeting(TiledMap map, GameScreen screen) {
        super(screen, map);
        startEvent();
    }

    protected void startEvent() {
        // Creates and sets the position for both the messenger and Daur in the beginning area.
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();

                // The area for the messenger spawn has been found. Creates and moves the messenger to this area.
                if (object.getProperties().containsKey("startmsgspawn")) {
                    messenger = new Messenger(screen, map, screen.characterAtlases.get(0));
                    messenger.setPosition(x - messenger.getWidth() / 2, y - messenger.getHeight() / 2);
                    screen.characters2.add(messenger);
                }
                // Same but for Daur.
                if (object.getProperties().containsKey("startmainspawn")) {
                    Daur daur = new Daur(screen, map, screen.daurAtlases.get(0));
                    daur.setPosition(x - daur.getWidth() / 2, y - daur.getHeight() / 2);
                    screen.setDaur(daur);
                }
            }
        // Sets the cell to the beginning cell (top left cell).
        screen.world2.setCellX(1);
        screen.world2.setCellY(16);

        // Sets the mask position to the top left cell to enable the game to fade in and out.
        screen.mask.setPosition(screen.camera.position.x - screen.camera.viewportWidth / 2, screen.camera.position.y - screen.camera.viewportHeight / 2);
        screen.mask.setColor(Color.BLACK);
        screen.mask.fadeIn(1);

        // Starts the dialogue.
        message();
    }

    protected void message() {
        final Timer timer = new Timer();
        final Event event = this;

        switch (stage) {
            case 0:
                // Creates the dialogue text, stuns Daur to prevent movement, freezes the screen to prevent the user
                // from bringing up options, sets the direction of Daur to look up, and makes Daur idle.
                Dialogue dialogue = new Dialogue(screen, "Closer wanderer...", this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                screen.daur.setDirection(2);
                screen.freeze();
                screen.daur.forceState(0);
                break;
            case 1:
                // Makes Daur move upwards by setting his velocity, clears the text, and makes Daur go into his running
                // state.
                screen.setText(null, null);
                screen.daur.modifyVelocity(0, 0.2f, 1);
                screen.daur.forceState(3);

                // More dialogue, stuns Daur, and makes him idle after one second.
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        Dialogue dialogue = new Dialogue(screen, "Yes... Come and I will tell you of your fate...",
                                event);
                        screen.setText(dialogue, dialogue.getBackground());
                        screen.daur.stun();
                        screen.daur.forceState(0);
                    }
                }, 1);
                break;
            case 2:
                screen.setText(null, null);
                screen.daur.modifyVelocity(0, 0.2f, 1.8f);
                screen.daur.forceState(3);

                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        Dialogue dialogue = new Dialogue(screen, "Wanderer, you know not why I brought you here. " +
                                "In truth, I did so at the behest of the gods. Beings who, despite all their power " +
                                "now currently require aid. Eight gods have been locked " +
                                "away. By whom, I do not know. To break their seals requires a mortal hand capable of " +
                                "overcoming great odds. The gods have you deemed you worthy of such a task. " +
                                "You will free the gods from their earthly prisons, and I shall give " +
                                "any help I can as a messenger. To begin the task before us, I will send you to the town " +
                                "of Carthell. Find out where the gods are being held, and free them. Good luck.",
                                event);
                        screen.setText(dialogue, dialogue.getBackground());
                        screen.daur.stun();
                        screen.daur.forceState(0);
                    }
                }, 1.8f);
                break;
            case 3:
                // Fades out and proceeds to the game itself.
                screen.setText(null, null);
                // Washes the screen out.
                screen.mask.fadeOut(1);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        // Removes messenger from the rendering list.
                        screen.characters2.remove(messenger);
                        // Sets the tile map to the overworld.
                        screen.setTileMap(0);
                        map = screen.map;
                        // Gets the new spawn point for Daur. This is for the OVERWORLD.
                        for (MapObject object : map.getLayers().get("Spawns").getObjects())
                            if (object instanceof RectangleMapObject) {
                                RectangleMapObject rectObject = (RectangleMapObject) object;
                                Rectangle rect = rectObject.getRectangle();
                                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();

                                if (object.getProperties().containsKey("startmainspawn")) {
                                    // Sets the new map for Daur.
                                    screen.daur.setMap(map);
                                    // Sets the new position for Daur.
                                    screen.daur.setPosition(x - screen.daur.getWidth() / 2, y - screen.daur.getHeight() / 2);
                                    // Makes Daur face down.
                                    screen.daur.setDirection(-2);
                                    // Stuns Daur.
                                    screen.daur.stun();
                                    // Sets the Daur state to idle.
                                    screen.daur.forceState(0);
                                    // Sets the cell of the overworld to the beginning cell.
                                    screen.world1.setCellX(6);
                                    screen.world1.setCellY(10);
                                    // Instantly pans the camera to the new position.
                                    screen.world1.setCameraInstantly();
                                }
                            }
                        // Fades the mask in, causing the screen to wash in.
                        screen.mask.setColor(Color.WHITE);
                        screen.mask.fadeIn(1);
                    }
                }, 1);
                // Creates and launches the next event that immediately proceeds the mesenger meeting.
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        StartingEvent event = new StartingEvent(screen.world1.getMap(), screen);
                    }
                }, 2);
                timer.start();
        }
    }
}
