package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.inoculates.fatesreprise.AdvSprite;
import com.inoculates.fatesreprise.Screens.GameScreen;

// UI superclass.
public abstract class UI extends AdvSprite {
    GameScreen screen;

    public UI(TextureRegion image) {
        super(image);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public abstract void renewPosition();

}
