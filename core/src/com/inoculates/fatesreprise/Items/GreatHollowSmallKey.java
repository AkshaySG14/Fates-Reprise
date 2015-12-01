package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Minor health pot item with the relevant frame.
public class GreatHollowSmallKey extends Item {

    public GreatHollowSmallKey(TextureAtlas atlas) {
        super(atlas.findRegion("greathollowkey"), 0);
    }
}
