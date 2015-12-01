package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;

// The sword slash
public class Slash extends Effect {
    int direction = 0;
    Sprite owner;

    public Slash(GameScreen screen, TiledMap map, TextureAtlas atlas, Sprite owner, int direction) {
        super(screen, map, atlas, true);
        this.owner = owner;
        this.direction = direction;
        chooseSprite();
    }

    // Sets the frame depending on the direction of the slash.
    protected void chooseSprite() {
        switch (direction) {
            case 0:
                setRegion(atlas.findRegion("slashright"));
                break;
            case 1:
                setRegion(atlas.findRegion("slashdown"));
                break;
            case 2:
                setRegion(atlas.findRegion("slashright"));
                break;
            case 3:
                setRegion(atlas.findRegion("slashleft"));
                break;
        }
        setSize(getRegionWidth() * 4/5, getRegionHeight() * 4/5);
    }

    // Repositions the sword effect based on the direction of the slash.
    public void reposition() {
        switch (direction) {
            case 0:
                setPosition(owner.getX() + owner.getWidth() / 2 - getWidth() / 2 + 4, owner.getY() + owner.getHeight() / 2 - getHeight() / 2 + 6);
                break;
            case 1:
                setPosition(owner.getX() + owner.getWidth() / 2 + 6, owner.getY() + owner.getHeight() / 2 - getHeight() / 2 - 3);
                break;
            case 2:
                setPosition(owner.getX() + owner.getWidth() / 2 - 4, owner.getY() + owner.getHeight() / 2 - getHeight() / 2 + 4);
                break;
            case 3:
                setPosition(owner.getX() - owner.getWidth() / 2, owner.getY() + owner.getHeight() / 2 - getHeight() / 2 - 4);
                break;
        }
    }

    protected void createAnimations() {
    }

    public void setAnimationTime(float time) {
        animationTime = time;
    }

    protected void updateTime(float deltaTime) {
    }

}
