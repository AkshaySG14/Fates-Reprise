package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

// This is the coin super class that is useful for iteration purposes. It is used to differentiate coins from other items.
public class CoinItem extends Item {

    // Renders the frame, sets the size to a constant 12x12, and sets the cost.
    public CoinItem(TextureRegion icon, int cost) {
        super(icon, cost);
        setSize(12, 12);
    }
}
