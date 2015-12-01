package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is basically the same class as the super class, but with the actual frames of the chest included.
public class WoodChest extends Chest {

    public WoodChest(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, String contents, float sX,
                     float sY, int chestNumber) {
        super(screen, map, atlas, storage, contents, sX, sY, chestNumber);
    }

    protected void update(float time) {

    }

    protected void createAnimations() {

    }

    protected void chooseSprite() {
        if (open)
            setRegion(atlas.findRegion("woodopen"));
        else
            setRegion(atlas.findRegion("woodclosed"));
        setSize(getRegionWidth(), getRegionHeight());
    }
}
