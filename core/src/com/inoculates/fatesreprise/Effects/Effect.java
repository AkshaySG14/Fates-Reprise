package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.AdvSprite;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Effect superclass that allows the game screen to iterate over all effects.
public abstract class Effect extends AdvSprite {
    GameScreen screen;
    TiledMap map;
    TextureAtlas atlas;

    float animationTime = 0;
    // Whether or not the effect will continue even after Daur leaves the cell it was created on.
    boolean persistent;

    public Effect(GameScreen screen, TiledMap map, TextureAtlas atlas, boolean persistent) {
        super(atlas.getRegions().get(0));
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.persistent = persistent;
        createAnimations();
        chooseSprite();
    }

    public void draw(Batch batch) {
        super.draw(batch);
        update(Gdx.graphics.getDeltaTime());
    }

    protected void update(float deltaTime) {
        createAnimations();
        chooseSprite();
        updateTime(deltaTime);
    }

    public boolean isPersistent() {
        return persistent;
    }

    protected void updateTime(float deltaTime) {
        animationTime += deltaTime;
    }

    abstract void chooseSprite();

    abstract void createAnimations();
}
