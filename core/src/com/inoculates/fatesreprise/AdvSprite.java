package com.inoculates.fatesreprise;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Advsprite is just a superclass that adds a couple of constructors and has the inherited method getInverted.
public class AdvSprite extends Sprite {
    protected boolean inverted = false;

    public AdvSprite(TextureAtlas.AtlasRegion region) {
        super(region);
    }

    public AdvSprite(TextureRegion region) {
        super(region);
    }

    public boolean getInverted() {
        return inverted;
    }

}
