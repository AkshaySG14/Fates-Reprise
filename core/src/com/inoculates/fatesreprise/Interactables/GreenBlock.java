package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is basically the same class as the super class, but with the actual frames of the block included.
public class GreenBlock extends Block {
    protected int dir;

    public GreenBlock(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, String direction,
                      boolean limited) {
        super(screen, map, atlas, storage, direction, limited);
    }

    public GreenBlock(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, String direction,
                      boolean limited, int trigger) {
        super(screen, map, atlas, storage, direction, limited, trigger);
    }

    protected void update(float time) {

    }

    protected void createAnimations() {

    }

    protected void chooseSprite() {
        setRegion(atlas.findRegion("greenblock"));
        setSize(getRegionWidth(), getRegionHeight());
    }
}
