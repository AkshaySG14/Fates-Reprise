package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;

public class Ripple extends Effect {
    Animation anim;
    Character owner;

    // Ripple effect when a character steps on water.
    public Ripple(GameScreen screen, TiledMap map, TextureAtlas atlas, Character owner) {
        super(screen, map, atlas, false);
        // Is persistent if owner is player.
        if (owner instanceof Daur)
            persistent = true;
        this.owner = owner;
    }

    protected void update(float deltaTime) {
        chooseSprite();
        reposition();
        updateTime(deltaTime);
    }

    protected void chooseSprite() {
        createAnimations();
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 3 / 4, anim.getKeyFrame(animationTime, true).getRegionHeight());
    }

    private void reposition() {
        setPosition(owner.getX() + owner.getWidth() / 2 - getWidth() / 2, owner.getY() - getHeight());
    }

    // Ripple animation
    protected void createAnimations() {
        anim = new Animation(0.333333f, atlas.findRegion("ripple1"), atlas.findRegion("ripple2"), atlas.findRegion("ripple3"));
    }

    public void setAnimationTime(float time) {
        animationTime = time;
    }

}
