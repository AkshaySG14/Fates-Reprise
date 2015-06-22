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
import com.inoculates.fatesreprise.Storage;

import java.util.ArrayList;
import java.util.Collections;

// Refer to the Houses class.
public class UpperWorld extends World {

    private Pathfinder pFinder;

    private ArrayList<Rectangle> portalsIn = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> portalsOut = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> exits = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> entrances = new ArrayList<Rectangle>();

    public UpperWorld(Storage storage, OrthographicCamera camera, TiledMap map, GameScreen screen) {
        super(storage, camera, map, screen);

        // Creates the pathfinder class by giving the storage and map class.
        pFinder = new Pathfinder(storage, map);

        setAccess();
        setPortals();
        createCharacters();
    }

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

                if (object.getProperties().containsKey("PO"))
                    portalsOut.add(rectangle);
                if (object.getProperties().containsKey("PI"))
                    portalsIn.add(rectangle);
            }
    }

    private void setAccess() {
        for (MapObject object : map.getLayers().get("Access").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                if (object.getProperties().containsKey("entrance"))
                    entrances.add(rect);
                if (object.getProperties().containsKey("exit"))
                    exits.add(rect);
            }
    }

    protected void setShaderTransitions() {
        Vector2 vec;
        vec = new Vector2(8, 5);
        shaderCells.put("fwin1", vec);
        vec = new Vector2(8, 6);
        shaderCells.put("fwout1", vec);
        vec = new Vector2(10, 5);
        shaderCells.put("fwin2", vec);
        vec = new Vector2(10, 6);
        shaderCells.put("fwout2", vec);
    }

    protected void createCharacters() {
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;

                if (object.getProperties().containsKey("villagerspawn1")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 0);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn2")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 1);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn3")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 2);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn4")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 3);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn5")) {
                    ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
                    Collections.addAll(rectangles, pFinder.v5Path1, pFinder.v5Path2, pFinder.v5Path3, pFinder.v5Path4);
                    DynamicVillager villager = new DynamicVillager(screen, map, screen.characterAtlases.get(1), rectangles, 4);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn6")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 5);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn7")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 6);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn8")) {
                    ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
                    Collections.addAll(rectangles, pFinder.v8Path1, pFinder.v8Path2, pFinder.v8Path3, pFinder.v8Path4, pFinder.v8Path5, pFinder.v8Path6);
                    DynamicVillager villager = new DynamicVillager(screen, map, screen.characterAtlases.get(1), rectangles, 7);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn9")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 8);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }
                if (object.getProperties().containsKey("villagerspawn10")) {
                    StaticVillager villager = new StaticVillager(screen, map, screen.characterAtlases.get(1), 9);
                    villager.setPosition(x - villager.getWidth() / 2, y - villager.getHeight() / 2);
                    screen.characters1.add(villager);
                }

            }
    }

    public Rectangle getPortal(int p, boolean in) {
        if (in)
            return portalsIn.get(p);
        else
            return portalsOut.get(p);
    }

    public Rectangle getAccess(int p, boolean in) {
        if (in)
            return exits.get(p);
        else
            return entrances.get(p);
    }

    protected void checkShaderTransition() {
        Vector2 cells = new Vector2(cellX, cellY);
        if (shaderCells.get("fwin1").equals(cells) || shaderCells.get("fwin2").equals(cells))
            screen.setCurrentMapShader(new ShaderProgram(Gdx.files.internal("Shaders/faron.vert"), Gdx.files.internal("Shaders/faron.frag")));
        else if (shaderCells.get("fwout1").equals(cells) || shaderCells.get("fwout2").equals(cells))
            screen.setCurrentMapShader(null);
    }

    public int getAccessSize() {
        return entrances.size();
    }

    public int getPortalSize() {
        return portalsIn.size();
    }
}
