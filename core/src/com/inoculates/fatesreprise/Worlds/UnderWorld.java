package com.inoculates.fatesreprise.Worlds;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

import java.util.ArrayList;

// Largely the same as the Houses class.
public class UnderWorld extends World {

    // Same as portals in and portals out.
    private ArrayList<Rectangle> exits = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> entrances = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> downstairs = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> upstairs = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> teleporters1 = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> teleporters2 = new ArrayList<Rectangle>();

    public UnderWorld(Storage storage, OrthographicCamera camera, TiledMap map, GameScreen screen) {
        super(storage, camera, map, screen);
        setAccess();
        setStairs();
        setPortals();
        createCharacters();
    }

    protected void createCharacters() {
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;
            }
    }

    // Adds all the exits and entrances.
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

    // Adds all the stairs in the game.
    private void setStairs() {
        for (MapObject object : map.getLayers().get("Level").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                if (object.getProperties().containsKey("up"))
                    upstairs.add(rect);
                if (object.getProperties().containsKey("down"))
                    downstairs.add(rect);
            }
    }

    // Adds all the teleports in the game.
    private void setPortals() {
        for (MapObject object : map.getLayers().get("Teleporters").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                if (object.getProperties().containsKey("TP1"))
                    teleporters1.add(rect);
                if (object.getProperties().containsKey("TP2"))
                    teleporters2.add(rect);
            }
    }

    protected void setShaderTransitions() {
        Vector2 vec;
        vec = new Vector2(2, 16);
        shaderCells.put("fwout1", vec);
    }

    // Overrides the shader transitions so that a null pointer exception does not occur.
    protected void checkShaderTransition() {
        Vector2 cells = new Vector2(cellX, cellY);
        if (shaderCells.get("fwout1").equals(cells))
            screen.setCurrentMapShader(null);
    }

    public Rectangle getAccess(int p, boolean in) {
        if (in)
            return entrances.get(p);
        else
            return exits.get(p);
    }

    public Rectangle getStairs(int s, boolean up) {
        if (up)
            return upstairs.get(s);
        else
            return downstairs.get(s);
    }

    public Rectangle getTeleporter(int t, boolean one) {
        if (one)
            return teleporters1.get(t);
        else
            return teleporters2.get(t);
    }

    public int getAccessSize() {
        return entrances.size();
    }

    public int getStairsSize() {
        return upstairs.size();
    }

    public int getTeleporterSize() {
        return teleporters1.size();
    }

    protected void setQuestEvents() {

    }
}
