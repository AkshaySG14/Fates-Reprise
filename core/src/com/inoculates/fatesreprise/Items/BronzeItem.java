package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Bronze coin.
public class BronzeItem extends CoinItem {

    public BronzeItem(TextureAtlas atlas) {
        super(atlas.findRegion("bronze"), 0);
    }
}
