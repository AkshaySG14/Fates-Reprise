package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the red UI bar at the top of the screen.
public class BlueBar extends UI {
    BlueBarUI owner;

    public BlueBar(GameScreen screen, TextureAtlas atlas, BlueBarUI owner) {
        super(atlas.findRegion("bluebar"));
        this.screen = screen;
        this.owner = owner;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void renewPosition() {
        setPosition(screen.camera.position.x - screen.camera.viewportWidth / 2, screen.camera.position.y +
                screen.camera.viewportHeight / 2 - getHeight());
    }
}
