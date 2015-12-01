package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.AdvSprite;
import com.inoculates.fatesreprise.Effects.Grass;
import com.inoculates.fatesreprise.Effects.Ripple;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

import java.awt.*;

// This is the interactable super class, which is responsible for all interactables, including blocks. Shares a lot of
// similarities to the character class.
public abstract class Interactable extends AdvSprite {
    protected float animationTime;

    protected int state = IDLE;
    private int cellX, cellY;

    protected static final int LEFT = -1, RIGHT = 1, DOWN = -2, UP = 2;
    protected static final int IDLE = 0, DEAD = 1, MOVING = 2;

    protected boolean frozen = false;

    protected Vector2 vel = new Vector2();
    protected Vector2 ace = new Vector2();
    protected TiledMapTileLayer layer;
    protected GameScreen screen;
    protected TextureAtlas atlas;
    protected TiledMap map;
    protected Storage storage;

    protected Grass grass;
    protected Ripple ripple;


    public Interactable(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage) {
        super(atlas.getRegions().get(0));
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.storage = storage;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        createAnimations();
        chooseSprite();
    }

    public void draw(Batch batch) {
        super.draw(batch);
        if (!screen.isPaused())
            periodicCheck(Gdx.graphics.getDeltaTime());
        if ((vel.x != 0 || vel.y != 0) && canMove())
            tryMove();
    }

    protected void periodicCheck(float deltaTime) {
        updateTime(deltaTime);
        update(deltaTime);
        createAnimations();
        chooseSprite();
    }

    protected void updateTime(float deltaTime) {
        if (!frozen)
            animationTime += deltaTime;
    }

    public void setState(int cState) {
        state = cState;
    }

    protected boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("blocked"));
    }

    protected boolean isCellHole(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("hole"));
    }

    protected boolean isCellPost(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("post"));
    }

    protected boolean isCellShrub(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("shrub"));
    }

    protected boolean isCellSpike(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    protected boolean isCellSlow(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("slow");
    }

    protected boolean isCellShallowWater(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("shallowwater");
    }

    protected boolean isCellWater(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("water");
    }

    protected boolean isCellDeepWater(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("deepwater");
    }

    protected int isCellGrass(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
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

    protected boolean detectShallowWater() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellShallowWater(getX() + step + 1, getY() + 1))
                return true;
        return false;
    }

    protected boolean detectWater() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellWater(getX() + step + 1, getY() + 1))
                return true;
        return false;
    }

    protected boolean detectDeepWater() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellDeepWater(getX() + step + 1, getY() + 1))
                return true;
        return false;
    }

    protected boolean detectSlow() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                if (isCellSlow(getX() + step + 1, getY() + 1 + step2))
                    return true;
        return false;
    }

    protected int detectGrass() {
        for (float step = 0; step < getWidth() / 3; step += layer.getTileWidth() / 16)
            for (float step2 = 0; step2 < (getHeight() - 5) / 3; step2 += layer.getTileHeight() / 16) {
                int grassType = isCellGrass(getX() + getWidth() / 3 + step, getY() + (getHeight() - 5) / 3 + step2);
                if (grassType != 4)
                    return grassType;
            }
        return 4;
    }

    protected Point detectHole() {
        Point holePoint;
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellHole(getX() + step, getY())) {
                float holeX = (int) ((getX() + step) / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() * 0.5f;
                float holeY = (int) ((getY()) / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() * 0.5f;
                holePoint = new Point((int) holeX, (int) holeY);
                return holePoint;
            }
        return null;
    }

    protected boolean collidesRight() {
        for (float step = 0; step < getHeight() - 5; step += layer.getTileHeight() / 16)
            if (isCellBlocked(getX() + getWidth() - 1, getY() + 1 + step) ||
                    isCellPost(getX() + getWidth() - 6, getY() + 1 + step) ||
                    isCellShrub(getX() + getWidth() - 4, getY() + 1 + step))
                return true;
        return false;
    }

    protected boolean collidesLeft() {
        for (float step = 0; step < getHeight() - 5; step += layer.getTileHeight() / 16)
            if (isCellBlocked(getX() + 1, getY() + 1 + step) ||
                    isCellPost(getX() + 6, getY() + 1 + step) ||
                    isCellShrub(getX() + 4, getY() + 1 + step))
                return true;
        return false;
    }

    protected boolean collidesTop() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellBlocked(getX() + 1 + step, getY() + getHeight() - 5))
                return true;
        for (float step = 0; step < getWidth() - 8; step += layer.getTileWidth() / 16)
            if (isCellShrub(getX() + 4 + step, getY() + getHeight() - 5))
                return true;
        for (float step = 0; step < getWidth() - 12; step += layer.getTileWidth() / 16)
            if (isCellPost(getX() + 6 + step, getY() + getHeight() - 5))
                return true;
        return false;
    }

    protected boolean collidesBottom() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellBlocked(getX() + 1 + step, getY() + 1))
                return true;
        for (float step = 0; step < getWidth() - 8; step += layer.getTileWidth() / 16)
            if (isCellShrub(getX() + 4 + step, getY() + 1))
                return true;
        for (float step = 0; step < getWidth() - 12; step += layer.getTileWidth() / 16)
            if (isCellPost(getX() + 6 + step, getY() + 1))
                return true;
        return false;
    }

    protected boolean collidesCharacter() {
        for (com.inoculates.fatesreprise.Characters.Character character : screen.charIterator)
            for (float step = 0; step < getWidth() - 1; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() - 1; step2 += layer.getTileHeight() / 16)
                    if (character.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2) && !character.equals(this))
                        return true;
        return false;
    }

    protected void checkCollisions() {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft() || getX() < (cellX - 1) * layer.getTileWidth() * 10;
        else if (vel.x > 0)
            collisionX = collidesRight() || getX() + getWidth() > cellX * layer.getTileWidth() * 10;

        if (collisionX) {
            setX(oldX);
            vel.x = vel.x * -1;
        }

        setY(getY() + vel.y);

        if (vel.y < 0)
            collisionY = collidesBottom() || getY() < (cellY - 1) * layer.getTileHeight() * 10;

        else if (vel.y > 0)
            collisionY = collidesTop() || getY() + getHeight() > cellY * layer.getTileHeight() * 10;

        if ((collisionY)) {
            setY(oldY);
            vel.y = vel.y * -1;
        }
    }

    protected void detectConditions() {
        if (detectGrass() != 4) {
            grass.setType(detectGrass());
            if (!screen.effects.contains(grass)) {
                screen.effects.add(grass);
                grass.setPosition(getX(), getY());
                grass.setAnimationTime(animationTime);
            }
        }
        else if (screen.effects.contains(grass))
            screen.effects.remove(grass);

        if (detectShallowWater()) {
            if (!screen.effects.contains(ripple)) {
                screen.effects.add(ripple);
                ripple.setPosition(getX(), getY());
                ripple.setAnimationTime(animationTime);
            }
        }

        else if (screen.effects.contains(ripple))
            screen.effects.remove(ripple);
    }

    public void setSpawn(float spawnX, float spawnY) {
        cellX = (int) (spawnX / (layer.getTileWidth() * 10) + 1);
        cellY = (int) (spawnY / (layer.getTileHeight() * 10) + 1);
        setPosition(spawnX, spawnY);
    }

    public void modifyVelocity(float x, float y, float time) {
        vel.x = x;
        vel.y = y;
        if (time == -1)
            return;

        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
            }
        }, time);
        timer.start();
    }

    public void setMap(TiledMap map) {
        this.map = map;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
    }

    protected boolean canMove() {
        return true;
    }

    public void setFrozen(boolean isFrozen) {
        frozen = isFrozen;
    }

    public int getState() {
        return state;
    }

    abstract void update(float deltaTime);

    abstract void createAnimations();

    abstract void chooseSprite();

    abstract boolean priorities(int cState);

    abstract boolean overrideCheck();

    abstract void tryMove();
}
