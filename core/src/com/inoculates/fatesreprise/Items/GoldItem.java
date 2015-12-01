package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Gold coin.
public class GoldItem extends CoinItem {

    public GoldItem(TextureAtlas atlas) {
        super(atlas.findRegion("gold"), 0);
    }
}
