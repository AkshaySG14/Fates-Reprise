package com.inoculates.fatesreprise.Items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.inoculates.fatesreprise.Characters.AdvSprite;

// This is the item super class that is useful for iteration purposes.
public class Item extends AdvSprite {
    // The gold cost of the item.
    int cost;

    // Renders the frame, sets the size to a constant 12x12, and sets the cost.
    public Item(TextureRegion icon, int cost) {
        super(icon);
        this.cost = cost;
        setSize(12, 12);
    }

    public int getCost() {
        return cost;
    }
}
