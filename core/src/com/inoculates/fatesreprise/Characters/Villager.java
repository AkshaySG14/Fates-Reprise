
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Super class for villager, that helps methods that wish to interact with villagers.
public abstract class Villager extends Character {
    Animation idle;
    int villager;

    public Villager(GameScreen screen, TiledMap map, TextureAtlas atlas, int villager) {
        super(screen, map, atlas, screen.storage);
        this.villager = villager;
        createRegions();
    }

    public int getVillager() {
        return villager;
    }

    abstract void createRegions();
}
