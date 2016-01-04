package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Minor health pot item with the relevant frame.
public class GreatHollowBossKey extends Item {

    public GreatHollowBossKey(TextureAtlas atlas) {
        super(atlas.findRegion("greathollowbosskey"), 0);
    }
}
