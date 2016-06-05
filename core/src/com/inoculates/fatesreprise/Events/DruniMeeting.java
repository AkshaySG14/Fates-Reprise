package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Druni;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;
import com.inoculates.fatesreprise.UI.UI;

// This is the class responsible for the cutscene where Daur meets the first sage, Druni.
public class DruniMeeting extends Event {
    Druni druni;

    public DruniMeeting(TiledMap map, GameScreen screen, Druni druni) {
        super(screen, map);
        startEvent();
        this.druni = druni;
    }

    protected void startEvent() {
        // Starts the dialogue.
        message();
        // Plays the sound when Daur meets the sage. Stops the victory music.
        screen.storage.stopMusic();
        screen.storage.music.get("sagemeetingmusic").play();
        screen.storage.music.get("sagemeetingmusic").setVolume(1.0f);
    }

    protected void message() {
        Timer timer = new Timer();
        switch (stage) {
            case 0:
                // Creates the dialogue text, stuns Daur to prevent movement, freezes the screen to prevent the user
                // from bringing up options, sets the direction of Daur to look up, and makes Daur idle.
                Dialogue dialogue = new Dialogue(screen, "Wanderer. I must thank you for journeying all the way here to " +
                        "rescue me. As you must know, I have been kidnapped and sealed away along with the seven other " +
                        "gods. Though I do not know who is responsible for this sinister and reprehensible act, your " +
                        "actions have surely impeded his plans. Now that I am free, I will assist you in any way I can to " +
                        "find him. " + "Our next course of action should be to find the next sage, Khalin. Hopefully " +
                        "along the way we may find clues as to the identity of this villain. Here, before we depart, let " +
                        "me bestow upon you the gift of additional life.", this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                screen.daur.setDirection(2);
                screen.freeze();
                screen.daur.forceState(0);
                break;
            case 1:
                // Increases the max health of Daur.
                screen.daur.incrementMaxHealth();
                // Sets sages one to true, as Daur has acquired the first sage.
                screen.storage.setSage(0);
                // Increments the main quest to 4, for trigger-related purposes.
                screen.storage.setMainQuestStage();
                // Removes Druni from rendering list.
                druni.fade(false);
                // After one second, moves Daur back to the overworld.
                screen.setText(null, null);
                screen.unPauseGame();
                screen.mask.fadeOut(3);
                // Plays the wash out music.
                final long waveID = screen.storage.sounds.get("bigeffect").play(1.0f);
                // Stops the current music.
                screen.storage.stopMusic();
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        // Sets the tile map to the overworld.
                        screen.setTileMap(0);
                        map = screen.map;
                        // Gets the new spawn point for Daur. This is for the OVERWORLD.
                        for (MapObject object : map.getLayers().get("Access").getObjects())
                            if (object instanceof RectangleMapObject) {
                                RectangleMapObject rectObject = (RectangleMapObject) object;
                                Rectangle rect = rectObject.getRectangle();
                                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                                if (object.getProperties().containsKey("greathollowexit")) {
                                    // Sets the new map for Daur.
                                    screen.daur.setMap(map);
                                    // Sets the new position for Daur.
                                    screen.daur.setPosition(x - screen.daur.getWidth() / 2, y - screen.daur.getHeight() / 2);
                                    screen.daur.setRespawnPoint();
                                    // Makes Daur face down.
                                    screen.daur.setDirection(-2);
                                    // Sets the Daur state to idle.
                                    screen.daur.forceState(0);
                                    // Sets the cell of the overworld to the beginning cell.
                                    screen.world1.setCellX((int) (screen.daur.getX() / layer.getTileWidth() / 10));
                                    screen.world1.setCellY((int) (screen.daur.getY() / layer.getTileHeight() / 10));
                                    // Instantly pans the camera to the new position.
                                    screen.world1.setCameraInstantly();
                                }
                            }
                        // Fades the mask in, causing the screen to wash in.
                        screen.mask.setColor(Color.WHITE);
                        screen.mask.fadeIn(3);
                        for (UI ui : screen.UIS)
                            ui.renewPosition();
                        // Removes Druni from the game.
                        screen.characters2.remove(druni);
                        // Gradually lowers the sound of the wavesound.
                        Timer timer2 = new Timer();
                        for (float i = 0; i <= 3; i += 0.1) {
                            // This is the volume that the waveSound plays at. Note that as time increases, the volume
                            // will decrease accordingly.
                            final float volume = 1 - i / 3;
                            timer2.scheduleTask(new Timer.Task() {
                                @Override
                                public void run() {
                                    screen.storage.sounds.get("bigeffect").setVolume(waveID, volume);
                                }
                            }, i);
                        }
                    }
                }, 3);
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.daur.unStun();
                        screen.daur.setRespawnPoint();
                        screen.unFreeze();
                        screen.unPauseGame();
                        screen.storage.sounds.get("bigeffect").stop();
                        // Starts the woods music.
                        screen.storage.music.get("forestmusic").play();
                        screen.storage.music.get("forestmusic").setVolume(0.75f);
                        screen.storage.music.get("forestmusic").setLooping(true);
                    }
                }, 6);
        }
    }
}
