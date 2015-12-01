package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Advsprite is just a superclass that adds a couple of constructors and has the inherited method getInverted.
public class AdvSprite extends Sprite {
    protected boolean inverted = false;
    protected boolean destructing = false;

    public AdvSprite(TextureAtlas.AtlasRegion region) {
        super(region);
    }

    public AdvSprite(TextureRegion region) {
        super(region);
    }

    public boolean getInverted() {
        return inverted;
    }

    public boolean getDestructing() {
        return destructing;
    }

    protected float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public float getCX() {
        return getX() + getWidth() / 2;
    }

    public float getCY() {
        return getY() + getHeight() / 2;
    }

    public float getRX() {
        return getX() + getWidth();
    }

    public float getRY() {
        return getY() + getHeight();
    }
}
