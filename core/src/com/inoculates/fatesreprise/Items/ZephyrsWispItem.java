package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Concussive shot item with the relevant frame.
public class ZephyrsWispItem extends Item {

    public ZephyrsWispItem(TextureAtlas atlas) {
        super(atlas.findRegion("ZephyrsWisp"), 0);
    }
}
