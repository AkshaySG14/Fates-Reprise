package com.inoculates.fatesreprise.Consumables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;

/*
Created by akshaysubramaniam on 13/9/15.
*/
public class Copper extends Consumable {
    public Copper(GameScreen screen, TiledMap map, TextureAtlas atlas, float spawnX, float spawnY) {
        super(screen, map, atlas.findRegion("copper"), spawnX, spawnY);
    }
}
