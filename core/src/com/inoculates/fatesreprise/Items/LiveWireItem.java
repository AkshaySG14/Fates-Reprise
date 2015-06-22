package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Live wire item with the relevant frame.
public class LiveWireItem extends Item {

    public LiveWireItem(TextureAtlas atlas) {
        super(atlas.findRegion("Live Wire"), 410);
    }
}
