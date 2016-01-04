package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Used to indicate where Daur is on the map.
public class DaurIcon extends UI {
    public DaurIcon(TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("DaurIcon")));
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void renewPosition() {

    }
}
