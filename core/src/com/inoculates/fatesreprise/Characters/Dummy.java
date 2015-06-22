
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Purely DEBUGGING class.
public class Dummy extends Enemy {
    TextureAtlas.AtlasRegion FU1 = atlas.findRegion("beetleU1"), FU2 = atlas.findRegion("beetleU2"),
            FD1 = atlas.findRegion("beetleD1"), FD2 = atlas.findRegion("beetleD2"), FR1 = atlas.findRegion("beetleR1"),
            FR2 = atlas.findRegion("beetleR2"), FL1 = atlas.findRegion("beetleL1"), FL2 = atlas.findRegion("beetleL2");

    public Dummy(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 1000);
    }

    protected void update(float deltaTime) {
    }

    protected void updateTime(float deltaTime) {
    }

    protected void tryMove() {

    }

    protected void createAnimations() {
        if (dir == UP) {
            run = new Animation(0.25f, FU1, FU2);
            idle = new Animation(0.5f, FU1);
        }
        else if (dir == DOWN) {
            run = new Animation(0.25f, FD1, FD2);
            idle = new Animation(0.5f, FD1);
        }
        else if (dir == RIGHT) {
            run = new Animation(0.25f, FR1, FR2);
            idle = new Animation(0.5f, FR1);
        }
        else if (dir == LEFT) {
            run = new Animation(0.25f, FL1, FL2);
            idle = new Animation(0.5f, FL1);
        }
    }

    protected boolean overrideCheck() {
        return state == DEAD;
    }

    protected boolean priorities(int cState) {
        return state == DEAD;
    }

    protected void chooseSprite()
    {
        Animation anim = idle;

        if (state == IDLE || state == DEAD)
            anim = idle;
        if (state == RUNNING)
            anim = run;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
