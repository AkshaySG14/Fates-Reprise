package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Flame gout item with the relevant frame.
public class FlameGoutItem extends Item {

    public FlameGoutItem(TextureAtlas atlas) {
        super(atlas.findRegion("Flame Gout"), 120);
    }
}
