package com.inoculates.fatesreprise.MeleeWeapons;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Consumables.Consumable;
import com.inoculates.fatesreprise.Effects.Slash;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the runed sword class responsible for the sword swing itself.
public class BasicSword extends MeleeWeapon {
    // The target that is hurt by the sword swing.
    Character target;
    // The visual frame of the sword.
    private TextureAtlas.AtlasRegion weapon = atlas.findRegion("sword");
    // Direction of the swing.
    private int direction;

    // Gets the direction, the position, and starts the attack method.
    public BasicSword(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, int direction) {
        super(screen, map, atlas, character);
        this.direction = direction;
        setPosition(owner.getX(), owner.getY());
        attack();
        chooseSprite();
    }

    // Note that this method is not responsible for the damage itself, but the visual effects of the sword slash.
    private void attack() {
        // Creates the slash effect on the screen.
        final Slash slash = new Slash(screen, map, screen.daurAtlases.get(3), this, direction);
        switch (direction) {
            // Right to up arc (Daur facing up).
            case 0:
                // Sets the sword facing to the right.
                setRotation(270);
                // Sets the position of the sword to look like Daur is holding it.
                setX(owner.getX() + owner.getWidth());
                setY(owner.getY() - 6);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        // After 0.075 seconds the sword will rotate to a top-right position and move accordingly. This
                        // is to emulate Daur swinging the sword.
                        setRotation(315);
                        setX(owner.getX() + owner.getWidth());
                        setY(owner.getY() + owner.getHeight() / 2);
                        // Respositions the slash on top of the sword.
                        slash.reposition();
                        // Renders the slash effect.
                        screen.effects.add(slash);
                        // Destroys the terrain object (like a bush) to the top right, if there is any.
                        sliceTile(UPRIGHT);
                    }
                }, 0.05625f);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        // After 0.15 seconds the sword will rotate to a vertical position and move accordingly.
                        setRotation(0);
                        setX(owner.getX());
                        setY(owner.getY() + owner.getHeight());
                        // Removes the slash effect.
                        screen.effects.remove(slash);
                        // Destroys the terrain object to the top, if there is any.
                        sliceTile(UP);
                    }
                }, 0.1125f);
                break;
            // Left to down arc (Daur facing down).
            case 1:
                setRotation(90);
                setX(owner.getX() - getWidth());
                setY(owner.getY() + owner.getHeight() / 2 - 5);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setRotation(135);
                        setX(owner.getX() - owner.getWidth() - 6);
                        setY(owner.getY() - 12);
                        slash.reposition();
                        screen.effects.add(slash);
                        sliceTile(DOWNLEFT);
                    }
                }, 0.05625f);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setRotation(180);
                        setX(owner.getX() - 4);
                        setY(owner.getY() - 18);
                        screen.effects.remove(slash);
                        sliceTile(DOWN);
                    }
                }, 0.1125f);
                break;
            // Up to right arc (Daur facing right).
            case 2:
                setRotation(0);
                setX(owner.getX() + getWidth() / 2 + 2);
                setY(owner.getY() + owner.getHeight());
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setRotation(315);
                        setX(owner.getX() + owner.getWidth() + 4);
                        setY(owner.getY() + owner.getHeight() / 2);
                        slash.reposition();
                        screen.effects.add(slash);
                        sliceTile(UPRIGHT);
                    }
                }, 0.05625f);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setRotation(270);
                        setX(owner.getX() + owner.getWidth());
                        setY(owner.getY() - owner.getHeight() / 2 - 3);
                        screen.effects.remove(slash);
                        sliceTile(RIGHT);
                    }
                }, 0.1125f);
                break;
            // Up to left arc (Daur facing left).
            case 3:
                setRotation(0);
                setX(owner.getX());
                setY(owner.getY() + owner.getHeight());
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setRotation(45);
                        setX(owner.getX() - owner.getWidth() + 2);
                        setY(owner.getY() + owner.getHeight() / 2 + 8);
                        slash.reposition();
                        screen.effects.add(slash);
                        sliceTile(UPLEFT);
                    }
                }, 0.05625f);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        setRotation(90);
                        setX(owner.getX() - owner.getWidth() - 3);
                        setY(owner.getY() - owner.getHeight() / 2 + 10);
                        screen.effects.remove(slash);
                        sliceTile(LEFT);
                    }
                }, 0.1125f);
                break;
        }
        // Does the actual damage itself.
        hitArc(direction);
    }

    // Destroys the tile given by the direction.
    private void sliceTile(int direction) {
        switch (direction) {
            case UP:
                // Destroys the terrain object to the top of Daur.
                explodeTile(owner.getX() + owner.getWidth() / 2, owner.getY() + owner.getHeight() / 2 + layer.getTileHeight());
                break;
            case UPRIGHT:
                // Destroys the terrain object to the top right of Daur.
                explodeTile(owner.getX() + owner.getWidth() / 2 + layer.getTileWidth(), owner.getY() + owner.getHeight() / 2 + layer.getTileHeight());
                break;
            case RIGHT:
                // Etc.
                explodeTile(owner.getX() + owner.getWidth() / 2 + layer.getTileWidth(), owner.getY() + owner.getHeight() / 2);
                break;
            case DOWN:
                explodeTile(owner.getX() + owner.getWidth() / 2, owner.getY() + owner.getHeight() / 2 - layer.getTileHeight());
                break;
            case DOWNLEFT:
                explodeTile(owner.getX() + owner.getWidth() / 2 - layer.getTileWidth(), owner.getY() + owner.getHeight() / 2 - layer.getTileHeight());
                break;
            case LEFT:
                explodeTile(owner.getX() + owner.getWidth() / 2 - layer.getTileWidth(), owner.getY() + owner.getHeight() / 2);
                break;
            case UPLEFT:
                explodeTile(owner.getX() + owner.getWidth() / 2 - layer.getTileWidth(), owner.getY() + owner.getHeight() / 2 + layer.getTileHeight());
                break;
        }
    }

    private void hitArc(int direction) {
        float dist = 0;
        // Damages all enemies in a parabolic arc.
        switch (direction) {
            // Up
            case 0:
                // From left of Daur to halfway through the sword at the right.
                for (float x = owner.getX(); x < owner.getX() + owner.getWidth() + 9; x++) {
                    // Creates an arc that starts as 18 at x = 0, and ends as 9.4922 at x = 22, which is the max dist
                    // before the loop exits. The constant value multiplied by the x value achieves this.
                    float y = (float) -Math.pow(dist * 0.13258252147, 2) + 18;
                    // Checks if a character or consumable is within the arc.
                    collidesCharacter(x, owner.getY() + owner.getHeight() + y, direction);
                    collidesConsumable(x, owner.getY() + owner.getHeight() + y, direction);
                    // Increments the x value of the parabolic graph.
                    dist ++;
                }
                break;
            // Down
            case 1:
                // From the right of Daur to halfway through the sword at the left.
                for (float x = owner.getX() + owner.getWidth(); x > owner.getX() - 9; x--) {
                    // Creates an arc that starts as -18 at x = 0, and ends as -9.4922 at x = 22.
                    float y = (float) Math.pow(dist * 0.13258252147, 2) - 18;
                    collidesCharacter(x, owner.getY() + y, direction);
                    collidesConsumable(x, owner.getY() + owner.getHeight() + y, direction);
                    dist++;
                }
                    break;
            // Right
            case 2 :
                // From the bottom of Daur to halfway through the sword at the top.
                for (float y = owner.getY(); y < owner.getY() + owner.getHeight() + 9; y++) {
                    // Creates an arc that starts as -18 at y = 0, and ends as 9.4922 at y = 23, which is the max dist
                    // before the loop exits. The constant value multiplied by the y value achieves this..
                    float x = (float) -Math.pow(dist * 0.133953975, 2) + 18;
                    collidesCharacter(owner.getX() + owner.getWidth() + x, y, direction);
                    collidesConsumable(x, owner.getY() + owner.getHeight() + y, direction);
                    // Increments the y value of the parabolic graph.
                    dist++;
                }
                break;
            // Left
            case 3:
                // From the bottom of Daur to halfway through the sword at the top.
                for (float y = owner.getY(); y < owner.getY() + owner.getHeight() + 9; y++) {
                    // Creates an arc that starts as -18 at y = 0, and ends as -9.4922 at y = 23.
                    float x = (float) Math.pow(dist * 0.133953975, 2) - 18;
                    collidesCharacter(owner.getX() + x, y, direction);
                    collidesConsumable(x, owner.getY() + owner.getHeight() + y, direction);
                    dist++;
                }
                break;
        }
    }

    // Sets the frame of the weapon and the size.
    protected void chooseSprite() {
        setRegion(weapon);
        setSize(getRegionWidth() * 4 / 5, getRegionHeight() * 4 / 5);
    }

    protected void createAnimations() {

    }

    // If an enemy is caught in the arc, damages the enemy.
    protected void effects() {
        if (target == null || !target.isEnemy())
            return;
        Enemy enemy = (Enemy) target;
        enemy.damageCollision(this, 1);
    }

    // This checks if the sword has collided with an enemy in its arc. The method works under the presumption that
    // the sword travels in a perfect quarter-circle arc.
    protected void collidesCharacter(float x, float y, int direction) {
        for (Character character : screen.charIterator) {
            if (character != owner)
                // Gets the x and y position of the character.
                for (float cX = character.getX(); cX < character.getX() + character.getWidth(); cX++)
                    for (float cY = character.getY(); cY < character.getY() + character.getHeight(); cY++)
                        switch (direction) {
                            // Up
                            // If the enemy is to the top-right of Daur and within the distance of the sword length.
                            case 0:
                                if (cX > owner.getX() && cY > owner.getY() && getDistance(owner.getCX(), owner.getCY(),
                                        cX, cY) < getWidth() * 1.5f) {
                                    target = character;
                                    effects();
                                }
                                break;
                            // Down
                            // If the enemy is to the bottom-left of Daur and within the distance of the sword length.
                            case 1:
                                if (cX < owner.getRX() && cY < owner.getCY() && getDistance(owner.getCX(), owner.getCY(),
                                        cX, cY) < getWidth() * 1.5f) {
                                    target = character;
                                    effects();
                                }
                                break;
                            // Right
                            // If the enemy is to the top-right of Daur and within the distance of the sword length.
                            case 2:
                                if (cX > owner.getX() && cY > owner.getY() && getDistance(owner.getCX(), owner.getCY(),
                                        cX, cY) < getWidth() * 1.5f) {
                                    target = character;
                                    effects();
                                }
                                break;
                            // Left
                            // If the enemy is to the top-left of Daur and within the distance of the sword length.
                            case 3:
                                if (cX < owner.getRX() && cY > owner.getY() && getDistance(owner.getCX(), owner.getCY(),
                                        cX, cY) < getWidth() * 1.5f) {
                                    target = character;
                                    effects();
                                }
                                break;
                        }
        }
    }

    // This checks if the sword has collided with a consumable in its arc. The method works under the presumption that
    // the sword travels in a perfect quarter-circle arc.
    protected void collidesConsumable(float x, float y, int direction) {
        for (Consumable consumable : screen.consumables) {
            // Gets the x and y position of the character.
            for (float cX = consumable.getX(); cX < consumable.getX() - 1 + consumable.getWidth() + 2; cX++)
                for (float cY = consumable.getY(); cY < consumable.getY() - 1 + consumable.getHeight() + 2; cY++)
                    switch (direction) {
                        // Up
                        // If the consumable is to the top-right of Daur and within the distance of the sword length.
                        case 0:
                            if (cX > owner.getX() && cY > owner.getY() && getDistance(owner.getCX(), owner.getCY(),
                                    cX, cY) < getWidth() * 1.5f) {
                                screen.daur.eatConsumable(consumable);
                                return;
                            }
                            break;
                        // Down
                        // If the consumable is to the bottom-left of Daur and within the distance of the sword length.
                        case 1:
                            if (cX < owner.getRX() && cY < owner.getCY() && getDistance(owner.getCX(), owner.getCY(),
                                    cX, cY) < getWidth() * 1.5f) {
                                screen.daur.eatConsumable(consumable);
                                return;
                            }
                            break;
                        // Right
                        // If the consumable is to the top-right of Daur and within the distance of the sword length.
                        case 2:
                            if (cX > owner.getX() && cY > owner.getY() && getDistance(owner.getCX(), owner.getCY(),
                                    cX, cY) < getWidth() * 1.5f) {
                                screen.daur.eatConsumable(consumable);
                                return;
                            }
                            break;
                        // Left
                        // If the consumable is to the top-left of Daur and within the distance of the sword length.
                        case 3:
                            if (cX < owner.getRX() && cY > owner.getY() && getDistance(owner.getCX(), owner.getCY(),
                                    cX, cY) < getWidth() * 1.5f) {
                                screen.daur.eatConsumable(consumable);
                                return;
                            }
                            break;
                    }
        }
    }

}
