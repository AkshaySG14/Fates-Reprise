package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.inoculates.fatesreprise.Characters.Bat;
import com.inoculates.fatesreprise.Characters.Lurker;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// These are the events that are fired when Daur enters a bush to obtain a heart piece.
public class HeartPieceBushEvent extends Event {
    Storage storage;
    String message = null;


    public HeartPieceBushEvent(TiledMap map, GameScreen screen, Storage storage) {
        super(screen, map);
        this.storage = storage;
        // Notifies the game that the heart piece event is occurring.
        screen.storage.FDstorage.beginHeartPieceEvent();

        startEvent();
    }

    protected void startEvent() {
        message();
    }

    // Since there is no dialogue goes straight to the creation of enemies.
    protected void message() {
        createEvent();
    }

    // This creates the wizards and bushes for the event.
    private void createEvent() {
        for (MapObject object : map.getLayers().get("Triggers").getObjects())
            if (object instanceof RectangleMapObject) {
                // Casts the rectangular object into a normal rectangle.
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                // Gets the middle of the spawn rectangle.
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;
                // Creates the wizards that will fight with Daur.
                if (object.getProperties().containsKey("bhb")) {
                    Bat bat = new Bat(screen, map, screen.characterAtlases.get(2));
                    bat.setSpawn(x - bat.getWidth() / 2, y - bat.getHeight() / 2);
                    // Adds to both the rendering list and the short-term memory.
                    screen.characters3.add(bat);
                    screen.storage.FDstorage.addHeartPieceEnemy(bat);
                }

                if (object.getProperties().containsKey("bhl")) {
                    Lurker lurker = new Lurker(screen, map, screen.characterAtlases.get(2));
                    lurker.setSpawn(x - lurker.getWidth() / 2, y - lurker.getHeight() / 2);
                    // Adds to both the rendering list and the short-term memory.
                    screen.characters3.add(lurker);
                    screen.storage.FDstorage.addHeartPieceEnemy(lurker);
                }

            }
    }
}
