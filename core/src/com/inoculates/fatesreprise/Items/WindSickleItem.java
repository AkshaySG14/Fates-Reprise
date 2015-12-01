package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Windsickle item with the relevant frame.
public class WindSickleItem extends Item {

    public WindSickleItem(TextureAtlas atlas) {
        super(atlas.findRegion("WindSickle"), 0);
    }
}
