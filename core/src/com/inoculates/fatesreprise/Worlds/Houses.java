package com.inoculates.fatesreprise.Worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

import java.util.ArrayList;

// This is the tiled map where houses exclusively exist.
public class Houses extends World {
    // The portals and counters of the world.
    private ArrayList<Rectangle> portalsIn = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> portalsOut = new ArrayList<Rectangle>();
    public ArrayList<Rectangle> counters = new ArrayList<Rectangle>();

    public Houses(Storage storage, OrthographicCamera camera, TiledMap map, GameScreen screen) {
        super(storage, camera, map, screen);
        createCharacters();

        setPortals();
        setCounters();
    }

    // Creates every character by finding the spawn rectangle map object and then creating based on the key it has.
    protected void createCharacters() {
        // Gets every rectangle map object from the spawns layer.
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                // Gets the x and y of the rectangle map object.
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;
                // If the object denotes a villager spawn, spawns the respective villager.
                if (object.getProperties().containsKey("villagerspawn1")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 10);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn2")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 11);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn3")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 12);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn4")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 13);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn5")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 14);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn6")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 15);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn7")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 16);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn8")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 17);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn9")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 18);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn10")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 19);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn11")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 20);
                    villager.setPosition(x - villager.getWidth() / 2 + 0.5f, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn12")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 21);
                    villager.setPosition(x - villager.getWidth() / 2 + 1, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn13")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 22);
                    villager.setPosition(x - villager.getWidth() / 2 + 1, y - villager.getHeight() / 2 - 1);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn14")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 23);
                    villager.setPosition(x - villager.getWidth() / 2 + 1, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn15")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 24);
                    villager.setPosition(x - villager.getWidth() / 2 + 1, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn16")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 25);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn17")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 26);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters3.add(villager);
                }
            }
    }

    // Same as villager creation but for portals.
    private void setPortals() {
        for (MapObject object : map.getLayers().get("Portals").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                float width = layer.getTileWidth();
                float height = layer.getTileHeight();
                Rectangle rectangle = new Rectangle(x, y, width, height / 2);
                // Added property designates the type of portal.
                if (object.getProperties().containsKey("PO"))
                    portalsOut.add(rectangle);
                if (object.getProperties().containsKey("PI"))
                    portalsIn.add(rectangle);
            }
    }

    // Creates all the counters via the same method as villagers, which are used to talk to a shopkeeper over it.
    private void setCounters() {
        for (MapObject object : map.getLayers().get("Counters").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                float width = layer.getTileWidth();
                float height = layer.getTileHeight();
                Rectangle rectangle = new Rectangle(x, y, width, height);

                if (object.getProperties().containsKey("counter"))
                    counters.add(rectangle);
            }
    }

    // This method adds very vectors which store the CELL positions of the various shader transitions. For example, a
    // shader transition will occur at cell 0x, 13y.
    protected void setShaderTransitions() {
        Vector2 vec;
        // For Faron Woods in (i.e. Faron Woods shader is applied).
        vec = new Vector2(3, 0);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(2, 1);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(2, 1);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(0, 0);
        shaderCells.get("fwin").add(vec);
    }

    // Plays music if Daur enters a specific cell (and no music is playing previously). This method simply adds the cells
    // to the arraylist.
    protected void setMusicCells() {
        Vector2 vec;
        // For the house music. Note the exception of cell 9, 10, 11, and 12. These are the shops and the mayor's office.
        for (int i = 0; i < 16; i ++) {
            if (i == 9 || i == 10 || i == 11 || i == 12)
                continue;
            vec = new Vector2(i, 15);
            musicCells.get("house").add(vec);
        }
        for (int i = 0; i < 3; i ++) {
            vec = new Vector2(i, 14);
            musicCells.get("house").add(vec);
        }
        // For the shops.
        for (int i = 9; i <= 11; i ++) {
            vec = new Vector2(i, 15);
            musicCells.get("shop").add(vec);
        }
        // For the mayor's office.
        vec = new Vector2(12, 15);
        musicCells.get("mayor").add(vec);
        // For cave music. This includes the bush areas.
        for (int i = 0; i < 4; i ++) {
            vec = new Vector2(i, 0);
            musicCells.get("cave").add(vec);
        }
        for (int i = 0; i < 5; i ++) {
            vec = new Vector2(i, 1);
            musicCells.get("cave").add(vec);
        }
    }

    // Gets the portal based on the integer given.
    public Rectangle getPortal(int p, boolean in) {
        if (in)
            return portalsIn.get(p);
        else
            return portalsOut.get(p);
    }

    public int getPortalSize() {
        return portalsIn.size();
    }

    public void setQuestEvents() {

    }
}
