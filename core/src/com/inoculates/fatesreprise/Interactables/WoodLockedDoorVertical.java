package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is basically the same class as the super class, but with the actual frames of the locked door included. This is
// the vertical version of the locked door.
public class WoodLockedDoorVertical extends LockedDoor {

    public WoodLockedDoorVertical(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, float x, float y,
                                  int doorNumber) {
        super(screen, map, atlas, storage, doorNumber);
    }

    protected void update(float time) {

    }

    protected void createAnimations() {

    }

    protected void chooseSprite() {
        setRegion(atlas.findRegion("ghlockeddoorvert"));
        setSize(getRegionWidth(), getRegionHeight());
    }
}
