package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Concussive shot item with the relevant frame.
public class ConcussiveShotItem extends Item {

    public ConcussiveShotItem(TextureAtlas atlas) {
        super(atlas.findRegion("ConcussiveShot"), 0);
    }
}
