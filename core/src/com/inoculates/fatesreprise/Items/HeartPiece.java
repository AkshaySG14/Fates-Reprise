package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Minor health pot item with the relevant frame.
public class HeartPiece extends Item {

    public HeartPiece(TextureAtlas atlas) {
        super(atlas.findRegion("heart"), 0);
    }
}
