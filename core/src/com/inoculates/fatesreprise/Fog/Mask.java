package com.inoculates.fatesreprise.Fog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;

public class Mask extends Sprite {

    // This mask causes the fade in / fade out effects seen in cutscenes.
    public Mask() {
        super(new TextureRegion(new Texture(Gdx.files.internal("Fog/mask.png"))));
        setAlpha(0);
    }

    // Washes the screen out with the color the mask is set to.
    public void fadeOut(float fadeTime) {
        for (float i = 0; i <= fadeTime; i += 0.01) {
            // The use of a timer with an iterated delay is used to gradually wash out the screen.
            final float alpha = i * (1 / fadeTime);
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(alpha);
                }
            }, i);
            timer.start();
        }
    }

    public void fadeIn(float fadeTime) {
        // Same but for washing the screen in.
        for (float i = fadeTime; i >= 0; i -= 0.01) {
            final float alpha = (i * 1 / fadeTime);
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(alpha);
                }
            }, fadeTime - i);
            timer.start();
        }
    }
}
