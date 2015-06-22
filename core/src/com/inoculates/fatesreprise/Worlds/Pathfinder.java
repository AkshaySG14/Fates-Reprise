package com.inoculates.fatesreprise.Worlds;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.inoculates.fatesreprise.Storage;

// This is the class that finds the rectangles on the map that denote the dynamic villager pathing, and creates a
// dynamic pathing for it.
public class Pathfinder {
    Storage storage;
    TiledMap map;
    public Rectangle v5Path1, v5Path2, v5Path3, v5Path4, v8Path1, v8Path2, v8Path3, v8Path4, v8Path5, v8Path6;

    public Pathfinder(Storage storage, TiledMap map) {
        this.map = map;
        this.storage = storage;
        createVillagerPaths();
    }

    // Gets the various pathing rectangles for each dynamic villager, which is later used for each individual dynamic
    // villager to gain their path.
    private void createVillagerPaths() {
        for (MapObject object : map.getLayers().get("Paths").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                if (object.getProperties().containsKey("villager5pathing1"))
                    v5Path1 = rect;
                if (object.getProperties().containsKey("villager5pathing2"))
                    v5Path2 = rect;
                if (object.getProperties().containsKey("villager5pathing3"))
                    v5Path3 = rect;
                if (object.getProperties().containsKey("villager5pathing4"))
                    v5Path4 = rect;
                if (object.getProperties().containsKey("villager8pathing1"))
                    v8Path1 = rect;
                if (object.getProperties().containsKey("villager8pathing2"))
                    v8Path2 = rect;
                if (object.getProperties().containsKey("villager8pathing3"))
                    v8Path3 = rect;
                if (object.getProperties().containsKey("villager8pathing4"))
                    v8Path4 = rect;
                if (object.getProperties().containsKey("villager8pathing5"))
                    v8Path5 = rect;
                if (object.getProperties().containsKey("villager8pathing6"))
                    v8Path6 = rect;
            }
    }
}
