package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.fatesreprise.Characters.AdvSprite;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Characters.Daur;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Consumables.Consumable;
import com.inoculates.fatesreprise.Interactables.Interactable;
import com.inoculates.fatesreprise.Interactables.Platform;
import com.inoculates.fatesreprise.Interactables.Teleporter;
import com.inoculates.fatesreprise.Screens.GameScreen;

/*
Created by akshaysubramaniam on 13/9/15.
*/
public class Shadow extends Effect {
    private AdvSprite owner;
    private TiledMapTileLayer layer;

    public Shadow(GameScreen screen, TiledMap map, TextureAtlas atlas, AdvSprite owner, float spawnX, float spawnY, float ratio) {
        super(screen, map, atlas, false);
        this.owner = owner;
        setRegion(atlas.findRegion("shadow"));
        layer = (TiledMapTileLayer) screen.map.getLayers().get(0);
        // Sets the width and height in accordance with the ratio given.
        setSize(getRegionWidth() * ratio, getRegionHeight() * ratio);
        setPosition(spawnX, spawnY);
    }

    public void draw(SpriteBatch batch) {
        // Continually sets the x to appear as though this is the owner's shadow.
        setX(owner.getCX() - getWidth() / 2);
        // If shadow's owner no longer exists in a rendering list, removes itself from the effect rendering list.
        if (owner instanceof Consumable && !screen.consumables.contains(owner))
            screen.effects.remove(this);

        super.draw(batch);
    }

    // Checks if the shadow has collided with any interactable.
    public boolean collidesInteractable() {
        for (Interactable interactable : screen.interactables) {
            for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() + 5; step2 += layer.getTileHeight() / 16)
                    if (interactable.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2) &&
                            !(interactable instanceof Teleporter || interactable instanceof Platform))
                        return true;
        }
        return false;
    }

    // Checks if the shadow has collided with any character.
    public boolean collidesCharacter() {
        for (Character character : screen.drawnSprites)
            for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() + 5; step2 += layer.getTileHeight() / 16)
                    if (character.getBoundingRectangle().contains(getX() + 2 + step, getY() + 1 + step2) &&
                            !character.equals(screen.daur) && !(character instanceof Enemy))
                        return true;
        return false;
    }

    protected void chooseSprite() {
        setRegion(atlas.findRegion("shadow"));
    }

    protected void createAnimations() {

    }

}