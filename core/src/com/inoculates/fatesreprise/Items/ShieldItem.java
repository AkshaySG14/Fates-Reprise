package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Shield item with the relevant frame.
public class ShieldItem extends Item {

    public ShieldItem(TextureAtlas atlas) {
        super(atlas.findRegion("Shield"), 20);
    }
}
