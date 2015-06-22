package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Minor health pot item with the relevant frame.
public class MinorHealthPotionItem extends Item {

    public MinorHealthPotionItem(TextureAtlas atlas) {
        super(atlas.findRegion("Minor Health Potion"), 180);
    }
}
