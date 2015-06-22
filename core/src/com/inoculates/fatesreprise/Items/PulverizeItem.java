package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Pulverize item with the relevant frame.
public class PulverizeItem extends Item {

    public PulverizeItem(TextureAtlas atlas) {
        super(atlas.findRegion("Pulverize"), 510);
    }
}
