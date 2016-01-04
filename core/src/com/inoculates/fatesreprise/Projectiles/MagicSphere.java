package com.inoculates.fatesreprise.Projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the slow-moving, large magic sphere launched by the Master Wizard.
public class MagicSphere extends Projectile {
    Character target;
    private Animation animate;
    private TextureAtlas.AtlasRegion proj1 = atlas.findRegion("magicsphere1"), proj2 = atlas.findRegion("magicsphere2");

    boolean exploding = false, terrain = false;
    final float RADIUS = 7.5f;
    private float angle;

    // Sets size and the angle of the Magic Sphere.
    public MagicSphere(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, float angle) {
        super(screen, map, atlas, character);
        setSize(15f, 15f);
        setStart(angle);
        this.angle = angle;
        createAnimations();
        chooseSprite();
    }

    // Sets the position of the Magic Sphere, depending on the angle of the Magic Sphere.
    private void setStart(float angle) {
        // Note the use of the angle to offset the position.
        setPosition((float) (owner.getX() + owner.getWidth() / 2 - getWidth() / 2 + Math.cos(angle)),
                (float) (owner.getY() + owner.getHeight() / 2 - getHeight() / 2 + Math.sin(angle)));
        // Sets the velocity also according to the angle.
        vel.x = (float) (Math.cos(angle));
        vel.y = (float) (Math.sin(angle));
    }

    // These two centers are used for collision, and rely on the circular nature of the ball.
    private float getCenterX() {
        return getX() + RADIUS;
    }

    private float getCenterY() {
        return getY() + RADIUS;
    }

    protected void update(float deltaTime) {
        createAnimations();
        chooseSprite();
        updateTime(deltaTime);
        if (vel.x != 0 || vel.y != 0)
            tryMove();
        // Rotates the sphere.
        rotate();
    }

    // Slowly spins the magic sphere.
    private void rotate() {
        setOriginCenter();
        setRotation(getRotation() + 2);
    }

    protected void chooseSprite() {
        setRegion(animate.getKeyFrame(animationTime, true));
    }

    protected void createAnimations() {
        animate = new Animation(0.1f, proj1, proj2);
    }

    // When a Magic Sphere hits either a terrain object or Daur.
    public void explode() {
        // If the Magic Sphere is destroyed by terrain, splinters into shards.
        if (terrain)
            spawnShards();
        // Rest of the explosion method.
        final Projectile projectile = this;
        vel.x = 0;
        vel.y = 0;
        // Fades the Magic Sphere out before removing it.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.8f);
            }
        }, 0.125f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.5f);
            }
        }, 0.25f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setAlpha(0.2f);
            }
        }, 0.375f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (screen.projectiles.contains(projectile))
                    screen.projectiles.remove(projectile);
            }
        }, 0.5f);
        exploding = true;
        animationTime = 0;
    }

    // Spawns three smaller shards that splinter into different directions.
    private void spawnShards() {
        // Creates a shard behind the sphere.
        final Splinter shard1 = new Splinter(screen, map, atlas, this, angle + (float) Math.PI);
        // Creates a shard slightly to the right of the back of the sphere.
        final Splinter shard2 = new Splinter(screen, map, atlas, this, angle + (float) Math.PI * 7 /8);
        // Creates a shard slightly to the left of the back of the sphere.
        final Splinter shard3 = new Splinter(screen, map, atlas, this, angle + (float) Math.PI * 9 / 8);
        // Adds all the shards to the renderer after 0.05 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.projectiles.add(shard1);
                screen.projectiles.add(shard2);
                screen.projectiles.add(shard3);
            }
        }, 0.05f);
    }

    protected void effects() {
        if (target == null || !target.equals(screen.daur))
            return;
        screen.daur.damageCollision(this);
    }

    private void tryMove() {
        checkCollisions();
    }

    protected boolean collidesCharacter() {
        for (Character character : screen.charIterator) {
            if (character != owner && screen.daur.isGrounded())
                for (float step = 0; step < character.getWidth(); step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < character.getHeight(); step2 += layer.getTileHeight() / 16)
                        if (Math.abs(character.getX() + step - getCenterX()) < RADIUS &&
                                Math.abs(character.getY() + step2 - getCenterY()) < RADIUS && isValidTarget(character)) {                            target = character;
                            effects();
                            return true;
                        }
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
            // Sets terrain to true to inform the game to splinter off the Magic Sphere.
            terrain = true;
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
            // Sets terrain to true to inform the game to splinter off the Magic Sphere.
            terrain = true;
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
}
