package com.inoculates.fatesreprise.Worlds;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                float width = layer.getTileWidth();
                float height = layer.getTileHeight();
                Rectangle rectangle = new Rectangle(x, y, width, height / 2);

                if (object.getProperties().containsKey("entrance"))
                    entrances.add(rectangle);
                if (object.getProperties().containsKey("exit"))
                    exits.add(rectangle);
            }
    }

    // Adds each cell to the fwin and fwout array lists.
    protected void setShaderTransitions() {
        Vector2 vec;
        // For Faron In (i.e. applies the Faron Woods shader).
        vec = new Vector2(7, 4);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(9, 4);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(8, 4);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(9, 3);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(8, 1);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(7, 0);
        shaderCells.get("fwin").add(vec);
        vec = new Vector2(7, 2);
        shaderCells.get("fwin").add(vec);
        // For Faron Out (i.e. removes the Faron Woods shader).
        vec = new Vector2(7, 5);
        shaderCells.get("fwout").add(vec);
        vec = new Vector2(9, 5);
        shaderCells.get("fwout").add(vec);
    }

    // Plays music if Daur enters a specific cell (and no music is playing previously). This method simply adds the cells
    // to the arraylist.
    protected void setMusicCells() {
        Vector2 vec;
        // For the overworld.
        vec = new Vector2(7, 8);
        musicCells.get("overworldtheme").add(vec);
        vec = new Vector2(7, 7);
        musicCells.get("overworldtheme").add(vec);
        // Uses a for loop for the bottom three cells.
        for (int x = 7; x <= 9; x ++) {
            vec = new Vector2(x, 5);
            musicCells.get("overworldtheme").add(vec);
        }
        // For Carthell Village. Uses a for loop to go in a 5x4 rectangle to add the cells.
        for (int y = 6; y <= 9; y ++)
            for (int x = 8; x <= 12; x ++) {
                vec = new Vector2(x, y);
                musicCells.get("carthellvillage").add(vec);
            }
        // Adds the last cell, at the bottom.
        vec = new Vector2(7, 6);
        musicCells.get("carthellvillage").add(vec);
        // For the Faron Woods. Uses a for loop similarly to Carthell Village.
        for (int y = 0; y <= 4; y ++)
            for (int x = 7; x <= 9; x ++) {
                // Ignore the bottom right cell.
                if (x == 9 && y == 0)
                    continue;
                vec = new Vector2(x, y);
                musicCells.get("faronwoods").add(vec);
            }
        // Add the fairy fountain area as well.
        vec = new Vector2(10, 3);
        musicCells.get("faronwoods").add(vec);
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
            return entrances.get(p);
        else
            return exits.get(p);
    }

    public int getAccessSize() {
        return entrances.size();
    }

    public int getPortalSize() {
        return portalsIn.size();
    }

    // This method sets all corresponding quest events. E.g. if a door was opened by a quest event, it will remain open.
    public void setQuestEvents() {
        // Great Hollow should be open.
        if (storage.mainQuestStage > 1)
            openGreatHollow();
    }

    private void openGreatHollow() {
        // Recreates layer so it relates to the object layer.
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(2);

        // First acquires the opening tile.
        Iterator<TiledMapTile> tiles = map.getTileSets().getTileSet("Tiles").iterator();
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
        for (MapObject object : map.getLayers().get("Triggers").getObjects())
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
                    return;
                }
            }
    }

}
