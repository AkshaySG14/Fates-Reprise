package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;

// The grass effect that appears when a character walks over grass.
public class Grass extends Effect {
    Animation anim;
    int type = 0;
    Character owner;

    // Gets the character owner so that the grass can periodically set its position.
    public Grass(GameScreen screen, TiledMap map, TextureAtlas atlas, Character owner) {
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

    // Repositions the grass to the center of the character.
    private void reposition() {
        setPosition(owner.getX() + owner.getWidth() / 2 - getWidth() / 2, owner.getY());
    }

    // Creates animation based on the type of grass the character has stepped on.
    protected void createAnimations() {
        switch (type) {
            case 0:
                anim = new Animation(0.5f, atlas.findRegion("springgrass1"), atlas.findRegion("springgrass2"));
                break;
            case 1:
                anim = new Animation(0.5f, atlas.findRegion("summergrass1"), atlas.findRegion("summergrass2"));
                break;
            case 2:
                anim = new Animation(0.5f, atlas.findRegion("wintergrass1"), atlas.findRegion("wintergrass2"));
                break;
            case 3:
                anim = new Animation(0.5f, atlas.findRegion("fallgrass1"), atlas.findRegion("fallgrass2"));
                break;
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAnimationTime(float time) {
        animationTime = time;
    }

    protected void updateTime(float deltaTime) {
        if (owner.isMoving())
            animationTime += deltaTime;
    }

}
