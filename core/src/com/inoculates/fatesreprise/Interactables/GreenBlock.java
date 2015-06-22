package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage;

// This is basically the same class as the super class, but with the actual frames of the block included.
public class GreenBlock extends Block {
    protected int dir;

    public GreenBlock(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, String direction, boolean limited) {
        super(screen, map, atlas, storage, direction, limited);
    }

    protected void update(float time) {

    }

    protected void createAnimations() {

    }

    protected void chooseSprite() {
        setRegion(atlas.findRegion("greenblock"));
    }
}
