package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Minor health pot item with the relevant frame.
public class GreatHollowKey extends Item {

    public GreatHollowKey(TextureAtlas atlas) {
        super(atlas.findRegion("hollowkey"), 0);
    }
}
