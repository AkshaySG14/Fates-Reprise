package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Diamond coin.
public class DiamondItem extends CoinItem {

    public DiamondItem(TextureAtlas atlas) {
        super(atlas.findRegion("diamond"), 0);
    }
}
