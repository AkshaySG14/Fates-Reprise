package com.inoculates.fatesreprise.MeleeWeapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.inoculates.fatesreprise.Characters.AdvSprite;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Consumables.Bronze;
import com.inoculates.fatesreprise.Consumables.Copper;
import com.inoculates.fatesreprise.Consumables.Heart;
import com.inoculates.fatesreprise.Effects.BushDestroy;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Worlds.World;

// Super class for all melee weapons.
public abstract class MeleeWeapon extends AdvSprite {
    GameScreen screen;
    TiledMap map;
    Cell collisionTile;
    TiledMapTileLayer layer;
    TextureAtlas atlas;
    Character owner;

    float animationTime = 0;
    float collideX = 0, collideY = 0;
    // The various directions a melee weapon can rotate.
    protected static final int LEFT = -1, RIGHT = 1, DOWN = -2, UP = 2, UPRIGHT = 3, UPLEFT = -3,
            DOWNRIGHT = 4, DOWNLEFT = -4;

    public MeleeWeapon(GameScreen screen, TiledMap map, TextureAtlas atlas, Character owner) {
        super(atlas.getRegions().get(0));
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.layer = (TiledMapTileLayer) map.getLayers().get(2);
        this.owner = owner;
        checkOutOfBounds();
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
        if (cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("blocked")))
            return true;
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
    //Checks whether the cell at the x and y point is a spike or not.
    protected boolean isCellSpike(float x, float y) {
        Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    //Checks whether the cell at the x and y point is able to be climbed by the knight or not.
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

    // This is the method that destroys the terrain object.
    protected void explodeTile(float x, float y) {
        // Sets a null ID.
        int id = 0;
        // The world the weapon exists in (under, over, or houses).
        World world;

        // Gets the tile of the x and y position.
        collisionTile = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        // If the tile is not equal to null and is a bush.
        if (collisionTile != null && collisionTile.getTile().getProperties().containsKey("bush")) {
            // Creates a bush destroy effect at the tile position.
            BushDestroy bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 0);
            // Gets the tile position in an integer.
            collideX = (int) (x / layer.getTileWidth()) * layer.getTileWidth();
            collideY = (int) (y / layer.getTileHeight()) * layer.getTileHeight();

            // If the bush is a spring bush.
            if (collisionTile.getTile().getProperties().containsKey("spring")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 0);
                // Sets the ID to zero. NOTE: this is PURELY for the purpose of recreating the bush when Daur enters
                // and exits a different world.
                id = 0;
            }
            // Same but for the summer bush.
            if (collisionTile.getTile().getProperties().containsKey("summer")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 1);
                id = 1;
            }
            // Etc.
            if (collisionTile.getTile().getProperties().containsKey("winter")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 2);
                id = 2;
            }
            if (collisionTile.getTile().getProperties().containsKey("fall")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 3);
                id = 3;
            }

            // Sets the position to the previously obtained collideX and collideY.
            bush.setPosition(collideX, collideY);
            screen.effects.add(bush);

            // Sets the tile to blank.
            collisionTile.setTile(screen.blankTile);
            // Gets the world that the sword exists in.
            world = screen.getWorld(map);

            // Creates the string value that tells the game to create the specific bush at the tile point when all
            // objects are respawned.
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

    // Checks if the weapon is outside the current cell.
    protected void checkOutOfBounds() {
        if (getX() > screen.camera.position.x + screen.camera.viewportWidth / 2 || getX() + getWidth() < screen.camera.position.x - screen.camera.viewportWidth / 2)
            screen.meleeWeapons.remove(this);
        if (getY() > screen.camera.position.y + screen.camera.viewportHeight / 2 || getY() + getHeight() < screen.camera.position.y - screen.camera.viewportHeight / 2)
            screen.meleeWeapons.remove(this);
    }

}
