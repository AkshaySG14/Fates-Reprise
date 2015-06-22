package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Indicator for the shop UI.
public class Indicator extends UI {
    Animation design;
    float animationTime = 0;

    public Indicator(TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("indicator")));
        design = new Animation(0.5f, atlas.findRegion("indicator"), atlas.findRegion("indicator2"));
    }

    public void draw(Batch batch) {
        super.draw(batch);
        updateTime(Gdx.graphics.getDeltaTime());
        setRegion(design.getKeyFrame(animationTime, true));
    }

    public void renewPosition() {
        
    }

    private void updateTime(float deltaTime) {
        animationTime += deltaTime;
    }
}
