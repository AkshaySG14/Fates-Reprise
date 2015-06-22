package com.inoculates.fatesreprise.Worlds;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage;

import java.util.ArrayList;

// Largely the same as the Houses class.
public class UnderWorld extends World {

    // Same as portals in and portals out.
    private ArrayList<Rectangle> exits = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> entrances = new ArrayList<Rectangle>();

    public UnderWorld(Storage storage, OrthographicCamera camera, TiledMap map, GameScreen screen) {
        super(storage, camera, map, screen);

        setAccess();
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
        vec = new Vector2(1, 14);
        shaderCells.put("fwin1", vec);
        vec = new Vector2(1, 15);
        shaderCells.put("fwout1", vec);
        vec = new Vector2(3, 14);
        shaderCells.put("fwin2", vec);
        vec = new Vector2(3, 15);
        shaderCells.put("fwout2", vec);
    }

    public Rectangle getAccess(int p, boolean in) {
        if (in)
            return exits.get(p);
        else
            return entrances.get(p);
    }

    public int getAccessSize() {
        return entrances.size();
    }
}
