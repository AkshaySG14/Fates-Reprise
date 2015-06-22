package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Runed sword item with the relevant frame.
public class BasicSwordItem extends Item {

    public BasicSwordItem(TextureAtlas atlas) {
        super(atlas.findRegion("Sword"), 0);
    }
}
