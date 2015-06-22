package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.*;
import com.inoculates.fatesreprise.Text.TextBackground;

// Virtually same as green arrow.
public class RedSquare extends UI {
    private Animation design;
    private TextBackground owner;

    public RedSquare(TextureAtlas atlas, TextBackground owner) {
        super(new TextureRegion(atlas.findRegion("redsquare1")));
        design = new Animation(0.5f, atlas.findRegion("redsquare1"), atlas.findRegion("redsquare2"));
        this.owner = owner;
    }

    public void draw(Batch batch) {
        super.draw(batch);
        setRegion(design.getKeyFrame(owner.animationTime, true));
    }

    public void renewPosition() {
        setX(owner.getX() + owner.getWidth());
        setY(owner.getY());
    }
}
