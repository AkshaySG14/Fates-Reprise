package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Reflect item with the relevant frame.
public class ReflectItem extends Item {

    public ReflectItem(TextureAtlas atlas) {
        super(atlas.findRegion("Reflect"), 605);
    }
}
