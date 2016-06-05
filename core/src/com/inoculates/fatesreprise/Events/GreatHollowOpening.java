package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

import java.util.Iterator;

// These are the events that are fired when Daur inserts the Great Hollow Key into the keyhole outside the Great Hollow.
public class GreatHollowOpening extends Event {
    Storage storage;
    String message = null;


    public GreatHollowOpening(TiledMap map, GameScreen screen, Storage storage) {
        super(screen, map);
        this.storage = storage;
        startEvent();
    }

    protected void startEvent() {
        message();
    }

    protected void message() {
        switch (stage) {
            case 0:
                screen.daur.stun();
                // Freezes Daur.
                screen.daur.freeze();
                screen.pauseScreen();
                // Plays key sound.
                screen.storage.sounds.get("unlock").play(2.0f);
                // Stops music for dramatic effect.
                storage.stopMusic();
                // After 0.75 seconds begins to shake screen.
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.shakeScreen(12, 0.1f, true);
                    }
                }, 0.75f);
                // After 1.95 seconds opens the door.
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        // Opens the Great Hollow.
                        openGreatHollow();
                        // Plays dungeon open sound.
                        screen.storage.sounds.get("dungeonopen").play(1.0f);
                    }
                }, 1.95f);
                // After two seconds, plays the success sound.
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.storage.sounds.get("powerup4").play(2.0f);
                    }
                }, 2);
                // After 2.1 seconds, plays the forest music again.
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.storage.music.get("forestmusic").play();
                        screen.storage.music.get("forestmusic").setVolume(0.75f);
                        screen.storage.music.get("forestmusic").setLooping(true);
                    }
                }, 2.1f);

                break;
            case 1:
                screen.daur.unStun();
                screen.unPauseScreen();
                break;
        }
    }

    // Opens the door to the Great Hollow by changing the entrance tiles.
    private void openGreatHollow() {
        // Recreates layer so it relates to the object layer.
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(2);

        // First acquires the opening tile.
        Iterator<TiledMapTile> tiles = screen.map.getTileSets().getTileSet("Tiles").iterator();
        TiledMapTile openingTile = null;
        while (tiles.hasNext()) {
            TiledMapTile tile = tiles.next();
            if (tile.getProperties().containsKey("greathollowopen")) {
                // Sets the left tile.
                openingTile = tile;
                break;
            }
        }

        // Finds the Great Hollow opening and then alters the tiles accordingly.
        for (MapObject object : screen.map.getLayers().get("Triggers").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;
                // Finds the rectangle that is necessary.
                if (object.getProperties().containsKey("GHO")) {
                    // Gets the cell and sets it to the opening tile. Returns afterwards
                    TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
                    cell.setTile(openingTile);
                    proceed();
                    return;
                }
            }
    }
}
