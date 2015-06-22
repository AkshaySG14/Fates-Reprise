package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

// Minor teleport item with the relevant frame.
public class MinorTeleportItem extends Item {

    public MinorTeleportItem(TextureAtlas atlas) {
        super(atlas.findRegion("Minor Teleport"), 220);
    }
}
