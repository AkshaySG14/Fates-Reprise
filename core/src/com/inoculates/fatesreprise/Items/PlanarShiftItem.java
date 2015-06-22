package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Planar shift item with the relevant frame.
public class PlanarShiftItem extends Item {

    public PlanarShiftItem(TextureAtlas atlas) {
        super(atlas.findRegion("Planar Shift"), 340);
    }
}
