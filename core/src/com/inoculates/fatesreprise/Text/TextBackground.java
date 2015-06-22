package com.inoculates.fatesreprise.Text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.UI.GreenArrow;
import com.inoculates.fatesreprise.UI.RedSquare;

// This is the image of the dialogue box.
public class TextBackground extends Sprite {
    //
    public Sprite addition;
    private GreenArrow arrow;
    private RedSquare square;

    public float animationTime = 0;

    public TextBackground(GameScreen screen, TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("background")));
        // Creates the arrow and the square.
        arrow = new GreenArrow(atlas, this);
        square = new RedSquare(atlas, this);
    }

    public void draw(Batch batch) {
        super.draw(batch);
        // Draws arrow or square.
        arrow.renewPosition();
        square.renewPosition();
        animationTime += Gdx.graphics.getDeltaTime();
    }

    public void setState(boolean green) {
        // Sets the additional sprite to be one of the two.
        if (green)
            addition = arrow;
        else
            addition = square;
    }
}
