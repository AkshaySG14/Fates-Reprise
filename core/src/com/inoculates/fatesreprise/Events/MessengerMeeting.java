package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
    Timer timer = new Timer();

    public MessengerMeeting(TiledMap map, GameScreen screen) {
        super(screen, map);
        startEvent();
        // Plays the messenger meeting music.
        screen.storage.music.get("mysteriousmusic").play();
        screen.storage.music.get("mysteriousmusic").setVolume(0.75f);
        // Sets final version of screen.
        final GameScreen lScreen = screen;
        // Sets an oncompletionlistener, so that the mysterious music plays the looping version once played.
        screen.storage.music.get("mysteriousmusic").setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                lScreen.storage.music.get("mysteriousmusicloop").play();
                lScreen.storage.music.get("mysteriousmusicloop").setVolume(0.75f);
                lScreen.storage.music.get("mysteriousmusicloop").setLooping(true);
            }
        });
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
        screen.world2.setCellX(0);
        screen.world2.setCellY(15);
        screen.world2.setCameraInstantly();

        // Sets the mask position to the top left cell to enable the game to fade in and out.
        screen.mask.setPosition(screen.camera.position.x - screen.camera.viewportWidth / 2, screen.camera.position.y -
                screen.camera.viewportHeight / 2);
        screen.mask.setColor(Color.BLACK);
        screen.mask.fadeIn(3);
        // Stuns and freezes Daur and the screen.
        screen.daur.stun();
        screen.daur.setDirection(2);
        screen.freeze();
        screen.daur.forceState(0);
        // Starts the dialogue after 3 seconds to avoid input spillage.
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                message();
            }
        }, 3);
    }

    protected void message() {
        final Event event = this;
        final Sound waveSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/bigeffect.wav"));
        long waveID;
        switch (stage) {
            case 0:
                // Creates the dialogue text.
                Dialogue dialogue = new Dialogue(screen, "Come wanderer...", this);
                screen.setText(dialogue, dialogue.getBackground());
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
                        Dialogue dialogue = new Dialogue(screen, "Yes... Come closer and I will tell you of your fate...",
                                event);
                        screen.setText(dialogue, dialogue.getBackground());
                        screen.daur.freeze();
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
                        screen.daur.freeze();
                        screen.daur.stun();
                        screen.daur.forceState(0);
                    }
                }, 1.8f);
                break;
            case 3:
                // Fades out and proceeds to the game itself.
                screen.setText(null, null);
                // Washes the screen out.
                screen.mask.fadeOut(3);
                // Plays the wash out music. Also captures the ID of the soundbyte in a long format.
                waveID = waveSound.play(1.0f);
                // The timer ID for the sound.
                final long waveIDT = waveID;
                // Stops the messenger meeting sound.
                screen.storage.music.get("mysteriousmusic").stop();
                screen.storage.music.get("mysteriousmusicloop").stop();
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
                        screen.mask.fadeIn(3);
                        // Gradually lowers the sound of the wavesound.
                        Timer timer2 = new Timer();
                        for (float i = 0; i <= 3; i += 0.1) {
                            // This is the volume that the waveSound plays at. Note that as time increases, the volume
                            // will decrease accordingly.
                            final float volume = 1 - i / 3;
                            timer2.scheduleTask(new Timer.Task() {
                                @Override
                                public void run() {
                                    waveSound.setVolume(waveIDT, volume);
                                }
                            }, i);
                        }
                    }
                }, 3);
                // Creates and launches the next event that immediately proceeds the messenger meeting.
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        StartingEvent event = new StartingEvent(screen.world1.getMap(), screen);
                        waveSound.stop();
                    }
                }, 6);
        }
    }
}
