package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Minor health pot item with the relevant frame.
public class OakStaff extends Item {

    public OakStaff(TextureAtlas atlas) {
        super(atlas.findRegion("staff"), 0);
    }
}
