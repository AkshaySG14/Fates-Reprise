package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.inoculates.fatesreprise.Text.TextBackground;

// Used in maps to block out unknown areas.
public class GraySquare extends UI {
    public GraySquare(TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("GraySquare")));
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void renewPosition() {

    }
}
