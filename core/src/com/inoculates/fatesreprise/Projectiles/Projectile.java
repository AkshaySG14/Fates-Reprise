package com.inoculates.fatesreprise.Projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.inoculates.fatesreprise.Characters.AdvSprite;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Consumables.Bronze;
import com.inoculates.fatesreprise.Consumables.Copper;
import com.inoculates.fatesreprise.Consumables.Heart;
import com.inoculates.fatesreprise.Effects.BushDestroy;
import com.inoculates.fatesreprise.Interactables.Interactable;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Worlds.World;

// This is the projectile super class.
public abstract class Projectile extends AdvSprite {
    GameScreen screen;
    TiledMap map;
    Cell collisionTile;
    TiledMapTileLayer layer;
    TextureAtlas atlas;
    Character owner;
    protected Vector2 vel = new Vector2();

    float animationTime = 0;
    float collideX = 0, collideY = 0;

    public Projectile(GameScreen screen, TiledMap map, TextureAtlas atlas, Character owner) {
        super(atlas.getRegions().get(0));
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.layer = (TiledMapTileLayer) map.getLayers().get(2);
        this.owner = owner;
    }

    public void draw(Batch batch) {
        super.draw(batch);
        if (!screen.isPaused())
            update(Gdx.graphics.getDeltaTime());
    }

    protected void update(float deltaTime) {
        createAnimations();
        chooseSprite();
        updateTime(deltaTime);
    }

    protected void updateTime(float deltaTime) {
        animationTime += deltaTime;
    }

    abstract void chooseSprite();

    abstract void createAnimations();

    abstract void effects();

    protected boolean isCellBlocked(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("blocked"))) {
            collideX = (int) (x / layer.getTileWidth()) * layer.getTileWidth();
            collideY = (int) (y / layer.getTileHeight()) * layer.getTileHeight();
            collisionTile = cell;
            return true;
        }
        else
            return false;
    }

    protected boolean isCellPost(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("post"));
    }

    protected boolean isCellShrub(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("shrub"));
    }

    protected boolean isCellSpike(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    protected boolean isCellClimbable(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("climbable");
    }

    protected boolean isCellSlow(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("slow");
    }

    protected int isCellGrass(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null) {
            if (cell.getTile().getProperties().containsKey("springgrass"))
                return 0;
            if (cell.getTile().getProperties().containsKey("summergrass"))
                return 1;
            if (cell.getTile().getProperties().containsKey("fallgrass"))
                return 2;
            if (cell.getTile().getProperties().containsKey("wintergrass"))
                return 3;
        }
        return 4;
    }

    protected boolean isCellSwimmable(float x, float y) {
        TiledMapTileLayer background = (TiledMapTileLayer) map.getLayers().get(1);
        Cell cell = background.getCell(((int) (x / layer.getTileWidth())), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("water");
    }

    protected boolean collidesRight() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
            if (isCellBlocked(getX() + getWidth(), getY() + step) ||
                    isCellPost(getX() + getWidth() - 5, getY() + step) ||
                    isCellShrub(getX() + getWidth() - 3, getY() + step))
                return true;
        return false;
    }

    protected boolean collidesLeft() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
            if (isCellBlocked(getX(), getY() + step) ||
                    isCellPost(getX() + 5, getY()+ step) ||
                    isCellShrub(getX() + 3, getY() + step))
                return true;
        return false;
    }

    protected boolean collidesTop() {
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellBlocked(getX()+ step, getY() + getHeight()))
                return true;
        for (float step = 0; step < getWidth() - 6; step += layer.getTileWidth() / 16)
            if (isCellShrub(getX() + 3 + step, getY() + getHeight()))
                return true;
        for (float step = 0; step < getWidth() - 10; step += layer.getTileWidth() / 16)
            if (isCellPost(getX() + 5 + step, getY() + getHeight()))
                return true;
        return false;
    }

    protected boolean collidesBottom() {
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellBlocked(getX() + step, getY()))
                return true;
        for (float step = 0; step < getWidth() - 6; step += layer.getTileWidth() / 16)
            if (isCellShrub(getX() + 3 + step, getY()))
                return true;
        for (float step = 0; step < getWidth() - 10; step += layer.getTileWidth() / 16)
            if (isCellPost(getX() + 5 + step, getY()))
                return true;
        return false;
    }

    protected boolean collidesCharacter() {
        for (Character character : screen.charIterator) {
            if (character != owner)
                for (float step = 0; step < getWidth() - 1; step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < getHeight() - 1; step2 += layer.getTileHeight() / 16)
                        if (character.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2))
                            return true;
        }
        return false;
    }

    protected boolean collidesInteractable() {
        for (Interactable interactable : screen.interactables) {
            for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                    if (interactable.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2))
                        return true;
        }
        return false;
    }

    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft() || collidesInteractable();
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesInteractable();

        if (collisionX) {
            explode();
            setX(oldX);
            vel.x = 0;
        }

        if (collidesCharacter()) {
            explode();
            setX(oldX);
            vel.x = 0;
        }

        setY(getY() + vel.y);

        if (vel.y < 0) {
            collisionY = collidesBottom() || collidesInteractable();
        } else if (vel.y > 0)
            collisionY = collidesTop() || collidesInteractable();

        if (collisionY) {
            explode();
            setY(oldY);
            vel.y = 0;
        }

        if (collidesCharacter()) {
            explode();
            setY(oldY);
            vel.y = 0;
        }
    }

    protected void detectConditions() {

    }

    // Check the melee weapon class for the exposition on this method.
    protected void explodeTile() {
        int id = 0;
        World world;

        if (collisionTile != null && collisionTile.getTile().getProperties().containsKey("bush")) {
            BushDestroy bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 0);
            if (collisionTile.getTile().getProperties().containsKey("spring")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 0);
                id = 0;
            }
            if (collisionTile.getTile().getProperties().containsKey("summer")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 1);
                id = 1;
            }
            if (collisionTile.getTile().getProperties().containsKey("winter")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 2);
                id = 2;
            }
            if (collisionTile.getTile().getProperties().containsKey("fall")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 3);
                id = 3;
            }
            bush.setPosition(collideX, collideY + 1);
            screen.effects.add(bush);

            collisionTile.setTile(screen.blankTile);

            world = screen.getWorld(map);

            switch (id) {
                case 0:
                    world.addCell("Spring Bush", collisionTile);
                    break;
                case 1:
                    world.addCell("Summer Bush", collisionTile);
                    break;
                case 2:
                    world.addCell("Fall Bush", collisionTile);
                    break;
                case 3:
                    world.addCell("Winter Bush", collisionTile);
                    break;
            }
            // Creates a consumable.
            createConsumable(collideX + layer.getTileWidth() / 2, collideY + layer.getTileHeight() / 2);
        }
    }

    // Note that this method applies to bushes and small objects. The X and Y are the center of the tile upon which
    // the bush sits.
    private void createConsumable(float x, float y) {
        // Creates a random number between 0 and 20, inclusive.
        int random = (int) (Math.random() * 20);
        // Bronze has a 25% chance of spawning from a regular enemy. Copper has a 5% chance. A heart has a 10% chance.
        // Spawning for bronze.
        if (random >= 0 && random <= 4) {
            Bronze bronze = new Bronze(screen, map, screen.miscAtlases.get(1), x, y);
            screen.consumables.add(bronze);
        }
        // For copper.
        if (random >= 8 && random <= 8) {
            Copper copper = new Copper(screen, map, screen.miscAtlases.get(1), x, y);
            screen.consumables.add(copper);
        }
        // For a heart.
        if (random >= 14 && random <= 15) {
            Heart heart = new Heart(screen, map, screen.miscAtlases.get(1), x, y);
            screen.consumables.add(heart);
        }
    }

    protected boolean checkOutOfBounds() {
        if ((getX() + getWidth() > screen.camera.position.x + screen.camera.viewportWidth / 2 && vel.x > 0)
                || (getX() < screen.camera.position.x - screen.camera.viewportWidth / 2 && vel.x < 0))
            return true;
        if ((getY() + getHeight() > screen.camera.position.y + screen.camera.viewportHeight / 2 - 16 && vel.y > 0)
                || (getY() < screen.camera.position.y - screen.camera.viewportHeight / 2) && vel.y < 0) {
            return true;
        }
        else
            return false;
    }

    public abstract void explode();

}
