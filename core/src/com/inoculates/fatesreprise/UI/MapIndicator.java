package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Indicator for the map UI.
public class MapIndicator extends UI {
    Animation design;
    float animationTime = 0;

    public MapIndicator(TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("Indicator1")));
        design = new Animation(0.5f, atlas.findRegion("Indicator1"), atlas.findRegion("Indicator2"));
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
