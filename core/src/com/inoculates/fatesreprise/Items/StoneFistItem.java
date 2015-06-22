package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Stone fist item with the relevant frame.
public class StoneFistItem extends Item {

    public StoneFistItem(TextureAtlas atlas) {
        super(atlas.findRegion("Stone Fist"), 485);
    }
}
