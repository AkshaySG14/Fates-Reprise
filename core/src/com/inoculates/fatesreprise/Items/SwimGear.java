package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SwimGear extends Item {

    public SwimGear(TextureAtlas atlas) {
        super(atlas.findRegion("compass"), 0);
    }
}
