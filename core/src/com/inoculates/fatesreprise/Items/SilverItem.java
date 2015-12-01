package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Silver coin.
public class SilverItem extends CoinItem {

    public SilverItem(TextureAtlas atlas) {
        super(atlas.findRegion("silver"), 0);
    }
}
