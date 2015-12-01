package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.inoculates.fatesreprise.Characters.Wizard;
import com.inoculates.fatesreprise.Interactables.BushBlock;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.Text.Dialogue;

// These are the events that are fired when Daur attempts to obtain the Oak Staff, but is waylaid by several enemies.
public class BushAmbush extends Event {
    Storage storage;
    String message = null;


    public BushAmbush(TiledMap map, GameScreen screen, Storage storage) {
        super(screen, map);
        this.storage = storage;
        // Notifies the game that the ambush is occurring.
        screen.storage.FDstorage.beginAmbush();
        startEvent();
    }

    protected void startEvent() {
        message();
    }

    protected void message() {
        Dialogue dialogue;
        // Begins dialogue.
        switch (stage) {
            case 0:
                dialogue = new Dialogue(screen, "Hmph. Did you really think we'd just let you take back the Fairy " +
                        "Queen's staff? Well then, you're a bigger idiot than I thought. I'm afraid you're journey ends " +
                        "here!",
                        this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                // Freezes Daur.
                screen.daur.freeze();
                screen.pauseScreen();
                break;
        // Breaks dialogue and initiates the fight.
            case 1:
                screen.setText(null, null);
                screen.daur.unStun();
                screen.unPauseScreen();
                createEvent();
                break;
        }
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
                if (object.getProperties().containsKey("mas")) {
                    Wizard wizard = new Wizard(screen, map, screen.characterAtlases.get(2));
                    wizard.setSpawn(x - wizard.getWidth() / 2, y - wizard.getHeight() / 2);
                    // Adds to both the rendering list and the short-term memory.
                    screen.characters3.add(wizard);
                    screen.storage.FDstorage.addAmbushEnemy(wizard);
                }

                if (object.getProperties().containsKey("mab")) {
                    BushBlock bushBlock = new BushBlock(screen, map, screen.miscAtlases.get(0), storage, x, y);
                    // Adds to both the rendering list and the short-term memory.
                    screen.interactables.add(bushBlock);
                    storage.FDstorage.addAmbushBushBlocks(bushBlock);
                }

            }
    }
}
