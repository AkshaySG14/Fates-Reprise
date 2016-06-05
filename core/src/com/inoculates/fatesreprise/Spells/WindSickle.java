package com.inoculates.fatesreprise.Spells;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Effects.BushDestroy;
import com.inoculates.fatesreprise.Effects.WindSickleDeath;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Worlds.World;

// Concussive shot spell.
public class WindSickle extends Spell {
    Animation anim;
    Character target;

    boolean exploding = false;
    int direction;
    long sickleSFX;
    boolean first;

    public WindSickle(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, int direction, boolean first) {
        super(screen, map, atlas, character);
        createAnimations();
        chooseSprite();
        this.direction = direction;
        this.first = first;
        // Sets position/direction based on the direction launched. Also two sickles spawn, each with a different position/
        // direction.
        if (first)
            setStartOne();
        else
            setStartTwo();
        sickleSFX = screen.storage.sounds.get("sickle").loop(0.5f);
        // Removes the sound after two seconds to prevent infinite looping.
        final GameScreen tScreen = screen;
    }

    // Creates the primary/first wind sickle.
    private void setStartOne() {
        switch (direction) {
            // Facing right, therefore spawn the sickle below him and make it go to the top-right of Daur.
            case 1:
                setPosition(owner.getX() + owner.getWidth() - getWidth(), owner.getY() - getHeight());
                vel.x = 2;
                vel.y = 1.5f;
                break;
            // Facing left, spawn the same but make it go to the top-left of Daur.
            case -1:
                setPosition(owner.getX(), owner.getY() - getHeight());
                vel.x = -2;
                vel.y = 1.5f;
                break;
            // Facing up, spawn to the right of Daur and make it go to the top-left of Daur.
            case 2:
                setPosition(owner.getX() + owner.getWidth() - getWidth() / 2, owner.getY() + owner.getHeight() - getHeight());
                vel.x = -1.5f;
                vel.y = 2;
                break;
            // Facing down, spawn the same but make it go to the bottom-left of Daur.
            case -2:
                setPosition(owner.getX() + owner.getWidth() - getWidth() / 2, owner.getY());
                vel.x = -1.5f;
                vel.y = -2;
                break;
        }
    }

    // Creates the secondary wind sickle.
    private void setStartTwo() {
        switch (direction) {
            // Facing right, therefore spawn the sickle above him and make it go to the bottom-right of Daur.
            case 1:
                setPosition(owner.getX() + owner.getWidth() - getWidth(), owner.getY() + owner.getHeight() - getHeight() / 2);
                vel.x = 2;
                vel.y = -1.5f;
                break;
            // Facing left, spawn the same but make it go to the bottom-left of Daur.
            case -1:
                setPosition(owner.getX(), owner.getY() + owner.getHeight() - getHeight() / 2);
                vel.x = -2;
                vel.y = -1.5f;
                break;
            // Facing up, spawn to the left of Daur and make it go to the top-right of Daur.
            case 2:
                setPosition(owner.getX() - getWidth(), owner.getY() + owner.getHeight() - getHeight());
                vel.x = 1.5f;
                vel.y = 2;
                break;
            // Facing down, spawn the same but make it go to the bottom-right of Daur.
            case -2:
                setPosition(owner.getX() - getWidth(), owner.getY());
                vel.x = 1.5f;
                vel.y = -2;
                break;
        }
    }

    protected void update(float deltaTime) {
        chooseSprite();
        rotate();
        updateTime(deltaTime);
        if (vel.x != 0 || vel.y != 0)
            tryMove();
    }

    // Constantly rotates the wind sickle.
    private void rotate() {
        setOriginCenter();
        setRotation(getRotation() + 10);
    }

    protected void chooseSprite() {
        createAnimations();
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 3 / 5, anim.getKeyFrame(animationTime, true).getRegionHeight() * 3 / 5);
    }

    protected void createAnimations() {
        anim = new Animation(0.2f, atlas.findRegion("windsickle1"), atlas.findRegion("windsickle2"));
    }

    protected void explode() {
        explodeTile(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }

    protected void explodeTile(float x, float y) {
        int id = -1;
        World world;
        int cX = (int) (x / layer.getTileWidth());
        int cY = (int) (y / layer.getTileHeight());
        collisionTile = layer.getCell(cX, cY);

        if (collisionTile != null) {
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
            // Note that the this applies to the bramble, which only the wind sickle can destroy.
            if (collisionTile.getTile().getProperties().containsKey("bramble")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 0);
                id = 4;
            }
            // This if statement applies to the four grasses.
            if (collisionTile.getTile().getProperties().containsKey("springgrass")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 0);
                id = 5;
            }
            if (collisionTile.getTile().getProperties().containsKey("summergrass")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 1);
                id = 6;
            }
            if (collisionTile.getTile().getProperties().containsKey("fallgrass")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 3);
                id = 7;
            }
            if (collisionTile.getTile().getProperties().containsKey("wintergrass")) {
                bush = new BushDestroy(screen, map, screen.daurAtlases.get(3), 2);
                id = 8;
            }

            // Nothing was found.
            if (id == -1)
                return;

            bush.setPosition(cX * 16, cY * 16);
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
                case 4:
                    world.addCell("Bramble", collisionTile);
                    break;
                case 5:
                    world.addCell("Spring Grass", collisionTile);
                    break;
                case 6:
                    world.addCell("Summer Grass", collisionTile);
                    break;
                case 7:
                    world.addCell("Fall Grass", collisionTile);
                    break;
                case 8:
                    world.addCell("Winter Grass", collisionTile);
                    break;
            }
            // Creates a consumable.
            createConsumable(cX * 16 + layer.getTileWidth() / 2, cY * 16 + layer.getTileHeight() / 2);
            // Plays the bush cut sound.
            screen.storage.sounds.get("bushcut2").play(1.0f);
        }
    }

    // Removes the sickle immediately.
    private void die() {
        vel.x = 0;
        vel.y = 0;
        // Creates the death effect, which dissipates quickly.
        WindSickleDeath death = new WindSickleDeath(screen, map, screen.daurAtlases.get(3));
        death.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
        // Removes self from rendering list and adds the death effect.
        screen.effects.add(death);
        // Ends the sickle sound effect.
        screen.storage.sounds.get("sickle").stop(sickleSFX);
        screen.storage.sounds.get("zap2").play(1.0f);
        final Spell spell = this;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.spells.remove(spell);
            }
        }, 0.01f);
    }

    // Hurts the target if it is an enemy and is not invulnerable (due to injury, death, or natural invulnerability).
    protected void effects() {
        if (target == null || !target.isEnemy())
            return;
        final Enemy enemy = (Enemy) target;
        if (!enemy.isTransparent() && !enemy.isDead())
            enemy.damageCollision(this, 1);
    }

    private void tryMove() {
        boolean collisionX = false;
        boolean collisionY = false;
        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft() || collidesInteractable();
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesInteractable();

        if (collisionX)
            die();

        setY(getY() + vel.y);

        if (vel.y < 0) {
            collisionY = collidesBottom() || collidesInteractable();
        } else if (vel.y > 0)
            collisionY = collidesTop() || collidesInteractable();

        if (collisionY)
            die();

        if (checkOutOfBounds())
            die();

        collidesCharacter();
        explode();
    }

    protected boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("blocked")) &&
                !(cell.getTile().getProperties().containsKey("bush")) && !(cell.getTile().getProperties().containsKey("bramble"))) {
            collideX = (int) (x / layer.getTileWidth()) * layer.getTileWidth();
            collideY = (int) (y / layer.getTileHeight()) * layer.getTileHeight();
            collisionTile = cell;
            return true;
        }
        else
            return false;
    }

    protected boolean collidesCharacter() {
        for (Character character : screen.charIterator) {
            if (character != owner)
                for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < getHeight(); step2 += layer.getTileHeight() / 16)
                        if (character.getBoundingRectangle().contains(getX()  + step, getY()+ step2) && !character.isTransparent()) {
                            target = character;
                            effects();
                            return true;
                        }
        }
        return false;
    }
}
