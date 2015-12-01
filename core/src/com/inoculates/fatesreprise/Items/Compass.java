package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Compass extends Item {

    public Compass(TextureAtlas atlas) {
        super(atlas.findRegion("compass"), 0);
    }
}
