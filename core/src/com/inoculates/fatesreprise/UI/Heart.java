package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Heart UI that represents the health of Daur.
public class Heart extends UI {
    private GameScreen screen;
    private TextureAtlas atlas;
    private int state = 0, heart = 0;
    private BlueBarUI bar;

    // Note the heart integer, which is used to displace the heart UI by a certain amount. This allows for the creation
    // of multiple hearts
    public Heart(GameScreen screen, TextureAtlas atlas, BlueBarUI bar, int heart) {
        super(atlas.findRegion("heart1"));
        this.atlas = atlas;
        this.bar = bar;
        this.heart = heart;
        this.screen = screen;
    }

    // Draws depending on the state.
    public void draw(Batch batch) {
        super.draw(batch);
        switch (state) {
            case 0:
                // Full heart.
                setRegion(atlas.findRegion("heart1"));
                break;
            case 1:
                // Half heart.
                setRegion(atlas.findRegion("heart2"));
                break;
            case 2:
                // Empty heart.
                setRegion(atlas.findRegion("heart3"));
                break;
        }
    }

    public void setState(int s) {
        state = s;
    }

    public void renewPosition() {
        // Sets position according to which number heart it is on the bar.
        setPosition(bar.getX() + 115 + 8 * heart, bar.getY() + bar.getHeight() - getHeight() - 1);
    }

}
