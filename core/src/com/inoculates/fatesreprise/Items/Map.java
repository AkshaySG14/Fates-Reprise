package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Map extends Item {

    public Map(TextureAtlas atlas) {
        super(atlas.findRegion("map"), 0);
    }
}
