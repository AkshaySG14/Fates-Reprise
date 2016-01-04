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
        vec = new Vector2(3, 0);
        shaderCells.put("fwin1", vec);
        vec = new Vector2(2, 1);
        shaderCells.put("fwin2", vec);
        vec = new Vector2(2, 1);
        shaderCells.put("fwin3", vec);
        vec = new Vector2(0, 0);
        shaderCells.put("fwin4", vec);
    }

    // Gets the portal based on the integer given.
    public Rectangle getPortal(int p, boolean in) {
        if (in)
            return portalsIn.get(p);
        else
            return portalsOut.get(p);
    }

    // Checks if the cell has a certain shader transition inside of it, and applies it if it does.
    protected void checkShaderTransition() {
        Vector2 cells = new Vector2(cellX, cellY);
        if (shaderCells.get("fwin1").equals(cells) || shaderCells.get("fwin2").equals(cells))
            screen.setCurrentMapShader(new ShaderProgram(Gdx.files.internal("Shaders/faron.vert"), Gdx.files.internal("Shaders/faron.frag")));
    }

    public int getPortalSize() {
        return portalsIn.size();
    }

    public void setQuestEvents() {

    }
}
