package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Mirror Image item with the relevant frame.
public class MirrorImageItem extends Item {

    public MirrorImageItem(TextureAtlas atlas) {
        super(atlas.findRegion("Mirror Image"), 255);
    }
}
