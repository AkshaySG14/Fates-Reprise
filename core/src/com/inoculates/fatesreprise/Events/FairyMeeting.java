package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.FairyQueen;
import com.inoculates.fatesreprise.Screens.GameScreen;

/*
 * Created by akshaysubramaniam on 11/9/15.
 */
public class FairyMeeting extends Event {
    private FairyQueen fairy;

    public FairyMeeting (TiledMap map, GameScreen screen) {
        super(screen, map);
        startEvent();
    }

    // Creates and sets the position for the fairy queen.
    protected void startEvent() {
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();

                // The area for the fairy queen spawn has been found. Creates and moves the fairy queen to this area.
                if (object.getProperties().containsKey("fairyspawn")) {
                    fairy = new FairyQueen(screen, map, screen.characterAtlases.get(0));
                    fairy.setPosition(x - fairy.getWidth() / 2, y - fairy.getHeight() / 2);
                    screen.characters1.add(fairy);
                    fairy.fade(true);
                    // Plays fade in sound.
                    screen.storage.sounds.get("fairyappear").stop();
                    screen.storage.sounds.get("fairyappear").play(1.0f);
                    // Starts playing the fairy fountain music.
                    screen.storage.stopMusic();
                    screen.storage.music.get("fairyfountainmusic").play();
                    screen.storage.music.get("fairyfountainmusic").setVolume(0.75f);
                    screen.storage.music.get("fairyfountainmusic").setLooping(true);
                }
            }
    }

    // Makes the fairy queen fade out and then removes her.
    protected void message() {
        fairy.fade(false);
        // Plays fade out sound.
        screen.storage.sounds.get("fairyappear").stop();
        screen.storage.sounds.get("fairyappear").play(1.0f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.characters1.remove(fairy);
            }
        }, 1);
        // Starts playing the Faron Woods music again.
        screen.storage.stopMusic();
        screen.storage.music.get("forestmusic").play();
        screen.storage.music.get("forestmusic").setVolume(0.75f);
        screen.storage.music.get("forestmusic").setLooping(true);
    }
}
