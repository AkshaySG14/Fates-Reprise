package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Cursor icon for the pause screen.
public class Cursor extends UI {

    public Cursor(TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("cursor")));
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void renewPosition() {
        
    }
}
