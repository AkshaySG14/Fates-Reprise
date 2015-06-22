package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Giants might item with the relevant frame.
public class GiantsMightItem extends Item {

    public GiantsMightItem(TextureAtlas atlas) {
        super(atlas.findRegion("Giant's Might"), 180);
    }
}
