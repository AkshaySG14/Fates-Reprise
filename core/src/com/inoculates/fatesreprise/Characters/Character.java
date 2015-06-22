package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.AdvSprite;
import com.inoculates.fatesreprise.Interactables.Interactable;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage;

import java.awt.*;

// This character class is the super class for ALL characters in the game, including enemies. It is the subclass of the
// modified sprite class.
public abstract class Character extends AdvSprite {
    // This float indicates the time the current animation has run for. Eg. how long the idle animation has been running.
    // This is used to determine the frame of the animation.
    protected float animationTime;
    // These modifiers pertain to the velocity, and modify the velocity by being summed with it when the speed of the
    // character is calculated.
    protected float modifierX = 0, modifierY = 0;

    // The state of the character, which determines its animation, the health, which is self explanatory, the armor, which
    // reduces a loss of health by its value, and the character's direction, which determines what frame is used during all
    // animations.
    protected int state = IDLE, health = 10, armor = 0, dir = DOWN;

    // The different directions represented as integers.
    protected static final int LEFT = -1, RIGHT = 1, DOWN = -2, UP = 2;
    // Two of the most common states represented as integers.
    protected static final int IDLE = 0, DEAD = 1;

    // Whether the character is on a platform or not. This is to ensure that the character's velocity when moving is
    // properly calculated when on a platform, as like in real life.
    protected boolean pX = false, pY = false;
    // Whether the character is stunned (cannot move but still properly displays animations) or completely frozen
    // (animation included).
    protected boolean stun = false, frozen = false;
    // Whether the character is transparent (does NOT collide with other characters).
    protected boolean transparent = false;

    // The velocity and acceleration of the character expressed as vectors. The layer to which the character belongs
    // (foreground, background, etc).
    protected Vector2 vel = new Vector2();
    protected Vector2 ace = new Vector2();
    protected TiledMapTileLayer layer;
    protected GameScreen screen;
    protected TextureAtlas atlas;
    protected TiledMap map;
    protected Storage storage;

    // The class constructor that initializes the stream, tile map the character is on, the texture atlas that holds its
    // frames, and the storage class responsible for holding global states.
    public Character(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage) {
        super(atlas.getRegions().get(0));
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.storage = storage;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
    }

    // This is the method responsible for drawing the character's current frame onto the map. Also calls the method
    // that updates the character's actions, like animation frames or movement.
    public void draw(Batch batch) {
        super.draw(batch);
        periodicCheck(Gdx.graphics.getDeltaTime());
    }

    // Updates the time, updates the character sprite itself, sets the current animation of the character, and sets the
    // frame of the character.
    protected void periodicCheck(float deltaTime) {
        updateTime(deltaTime);
        update(deltaTime);
        createAnimations();
        chooseSprite();
    }

    //Sets the velocity components of the character depending on the modifiers.
    protected void SVX(float x) {
        vel.x = x - modifierX * Math.signum(x);
    }

    protected void SVY(float y) {
        vel.y = y - modifierY * Math.signum(y);
    }

    // Updates the animation time of the character. This is to ensure that the proper frame is being drawn.
    protected void updateTime(float deltaTime) {
        if (!frozen)
            animationTime += deltaTime;
    }

    // Sets the state of the character to the given one if and only if it passes either the override check (if not given
    // the override flag) or the priorities check (if given the override flag).
    protected void setState(int cState, boolean override) {
        if (cState == state || (!override && overrideCheck()) || (override && priorities(cState)))
            return;

        state = cState;
        animationTime = 0;
    }

    // Forces the state (does NOT check).
    public void forceState(int cState) {
        state = cState;
    }

    // Checks whether the cell at the x and y point is blocked or not.
    protected boolean isCellBlocked(float x, float y) {
        // Gets the cell by dividing the x position of the character by the tile width (16) and casting to an int. The
        // same is done for the y position. This will essentially acquire the cell position, which will then be checked
        // for its properties.
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        // Checks if one of the cell properties is blocked.
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("blocked"));
    }

    // Checks whether the cell at the x and y point is a hole.
    protected boolean isCellHole(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("hole"));
    }

    // Checks whether the cell at the x and y point is a post (wooden stake). This is because a post has an irregular
    // shape.
    protected boolean isCellPost(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("post"));
    }

    // Checks whether the cell at the x and y point is a shrub (the small tree). This is because a bush has an irregular
    // shape.
    protected boolean isCellShrub(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("shrub"));
    }

    // Checks whether the cell at the x and y point is a spike or not.
    protected boolean isCellSpike(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    // Checks whether the cell at the x and y point is a an area that slows the character (like a ladder or a bush).
    protected boolean isCellSlow(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("slow");
    }

    //Checks whether the cell at the x and y point is shallow water (causes ripple but no swimming).
    protected boolean isCellShallowWater(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("shallowwater");
    }

    //Checks whether the cell at the x and y point is water that is deep enough to swim.
    protected boolean isCellWater(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("water");
    }

    //Checks whether the cell at the x and y point is water that is too deep to swim normally.
    protected boolean isCellDeepWater(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("deepwater");
    }

    //Checks whether the cell at the x and y point is grass, and creates the grass effect accordingly.
    protected int isCellGrass(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null) {
            // Checks what type of grass the character has trod on (spring, summer, fall, or winter).
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

    // Checks whether the cell at the x and y point is shallow water. This is will be checked every time the character's
    // frame is updated.
    protected boolean detectShallowWater() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellShallowWater(getX() + step + 1, getY() + 1))
                return true;
        return false;
    }

    // Same but for normal swimmable water.
    protected boolean detectWater() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellWater(getX() + step + 1, getY() + 1))
                return true;
        return false;
    }

    // Same but for deep water.
    protected boolean detectDeepWater() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            if (isCellDeepWater(getX() + step + 1, getY() + 1))
                return true;
        return false;
    }

    // Same but for slow cells. Note that the whole character's bounding rectangle is scanned for any point that exists
    // in the cell.
    protected boolean detectSlow() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
            for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                if (isCellSlow(getX() + step + 1, getY() + 1 + step2))
                    return true;
        return false;
    }

    // Same but for grass.
    protected int detectGrass() {
        for (float step = 0; step < getWidth() / 3; step += layer.getTileWidth() / 16)
            for (float step2 = 0; step2 < (getHeight() - 5) / 3; step2 += layer.getTileHeight() / 16) {
                // Gets the grass type and sets the grass effect accordingly.
                int grassType = isCellGrass(getX() + getWidth() / 3 + step, getY() + (getHeight() - 5) / 3 + step2);
                if (grassType != 4)
                    return grassType;
            }
        return 4;
    }

    // A complex method that detects whether the character is near enough a hole to fall down it.
    protected Point detectHole() {
        // The point of the hole (has both x and y components).
        Point holePoint;
        // Iterates through the character's width (from the x origin to the right end of the character).
        for (float step = 0; step < getWidth() - 4; step += layer.getTileWidth() / 16)
            // Checks if the x point and the y origin point of the character are inside a cell that i a hole.
            if (isCellHole(getX() + 2 + step, getY())) {
                // These complex-looking lines of code simply get the middle of the cell with the hole in it.
                float holeX = (int) ((getX() + 2 + step) / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() * 0.5f;
                float holeY = (int) ((getY()) / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() * 0.5f;
                // Sets the middle of the cell as the hole point and returns it.
                holePoint = new Point((int) holeX, (int) holeY);
                return holePoint;
            }
        return null;
    }

    //This periodically checks whether any part of the character is in a blocked cell if his x velocity is positive.
    protected boolean collidesRight() {
        for (float step = 0; step < getHeight() - 5; step += layer.getTileHeight() / 16)
            // Note the extra steps account for the irregular shapes of the post and shrub.
            if (isCellBlocked(getX() + getWidth() - 1, getY() + 1 + step) ||
                    isCellPost(getX() + getWidth() - 6, getY() + 1 + step) ||
                    isCellShrub(getX() + getWidth() - 4, getY() + 1 + step))
                return true;
        return false;
    }

    //This periodically checks whether any part of the character is in a blocked cell if his x velocity is negative.
    protected boolean collidesLeft() {
        for (float step = 0; step < getHeight() - 5; step += layer.getTileHeight() / 16)
            if (isCellBlocked(getX() + 1, getY() + 1 + step) ||
                    isCellPost(getX() + 6, getY() + 1 + step) ||
                    isCellShrub(getX() + 4, getY() + 1 + step))
                return true;
        return false;
    }

    //This periodically checks whether any part of the character is in a blocked cell if his y velocity is positive.
    protected boolean collidesTop() {
        for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16) {
            if (isCellBlocked(getX() + 1 + step, getY() + getHeight() - 5))
                return true;
        }
        for (float step = 0; step < getWidth() - 8; step += layer.getTileWidth() / 16)
            if (isCellShrub(getX() + 4 + step, getY() + getHeight() - 5))
                return true;
        for (float step = 0; step < getWidth() - 12; step += layer.getTileWidth() / 16)
            if (isCellPost(getX() + 6 + step, getY() + getHeight() - 5))
                return true;
        return false;
    }

    //This periodically checks whether any part of the knight is in a blocked cell if his y velocity is negative.
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

    // Checks if this character's bounding rectangle intersects another's.
    protected boolean collidesCharacter() {
        for (Character character : screen.charIterator)
            for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() - 1; step2 += layer.getTileHeight() / 16)
                    if (character.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2) && !character.equals(this))
                        return true;
        return false;
    }

    // Same but for interactables (essentially objects that can move or be destroyed).
    protected boolean collidesInteractable() {
        for (Interactable interactable : screen.interactables) {
            for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                    if (interactable.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2))
                        return true;
        }
        return false;
    }

    // Modifies the velocity of the character for a short amount of time. NOTE: this overrides the character's current
    // velocity and stuns it.
    public void modifyVelocity(float x, float y, float time) {
        // Sets new velocity.
        vel.x = x;
        vel.y = y;
        // Stuns character.
        stun = true;
        // Unstuns after the specified time.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
            }
        }, time);
        timer.start();
    }

    // This modifier is used to add or subtract from the character's velocity. Useful for slowing or speeding up the
    // character.
    public void setModifier(float modX, float modY) {
        modifierX = modX;
        modifierY = modY;
    }

    // Sets the direction of the character, and consequently which way the character is facing in animations.
    public void setDirection(int direction) {
        dir = direction;
    }

    // Modifies the modifiers for the velocity. If the modifiers are sufficiently marginal (below zero and therefore
    // no longer applicable), sets them to zero.
    protected void resetModifier(float modX, float modY) {
        modifierX = modifierX - modX;
        modifierY = modifierY - modY;
        if (modifierX < 0)
            modifierX = 0;
        if (modifierY < 0)
            modifierY = 0;
    }

    // Simply sets the new map for the character, to ensure that the character knows what layer it's on.
    public void setMap(TiledMap map) {
        this.map = map;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
    }

    // Getter methods more or less.
    public boolean isTransparent() {
        return transparent;
    }

    public boolean isEnemy() {
        return this instanceof Enemy;
    }

    public boolean isMoving() {
        return vel.x != 0 || vel.y != 0;
    }

    public boolean isStunned() { return stun; }

    public void setFrozen(boolean isFrozen) {
        frozen = isFrozen;
    }

    public int getDirection() {
        return dir;
    }

    public int getState() {
        return state;
    }

    // Sets whether the character is stunned or not. Useful for concurrent threads that need methods, and cannot
    // interact with non-constant variables.
    public void stun() {
        stun = true;
    }

    public void unStun() {
        stun = false;
    }

    // These abstract methods are dependant upon the nature of the character, and thus are left for the sub classes to
    // define.
    abstract void update(float deltaTime);

    abstract void createAnimations();

    abstract void chooseSprite();

    abstract boolean priorities(int cState);

    abstract boolean overrideCheck();
}
