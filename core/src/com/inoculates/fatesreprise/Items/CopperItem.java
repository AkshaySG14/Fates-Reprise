package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Copper coin.
public class CopperItem extends CoinItem {

    public CopperItem(TextureAtlas atlas) {
        super(atlas.findRegion("copper"), 0);
    }
}
