package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.*;
import com.inoculates.fatesreprise.Text.TextBackground;

// This is the green arrow that is located BELOW the dialogue box. Note that it is a rectangle with a green arrow, not
// just a green arrow. This is so that it will never conflict in spacing with the text.
public class GreenArrow extends UI {
    private Animation design;
    private TextBackground owner;

    public GreenArrow(TextureAtlas atlas, TextBackground owner) {
        super(new TextureRegion(atlas.findRegion("greenarrow1")));
        design = new Animation(0.5f, atlas.findRegion("greenarrow1"), atlas.findRegion("greenarrow2"));
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
