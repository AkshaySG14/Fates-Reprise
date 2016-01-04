package com.inoculates.fatesreprise.Projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the whirling sphere launched by the Master Wizard that can randomly explode.
public class WhirlingSpheres extends Projectile {
    Character target;
    private Animation animate;
    private TextureAtlas.AtlasRegion proj = atlas.findRegion("triplesphere");

    boolean exploding = false, terrain = false;
    final float RADIUS = 4.5f;
    private float angle;

    // Sets size and the angle of the Whirling Spheres.
    public WhirlingSpheres(GameScreen screen, TiledMap map, TextureAtlas atlas, Character character, float angle) {
        super(screen, map, atlas, character);
        setSize(9, 9);
        setStart(angle);
        this.angle = angle;
        createAnimations();
        chooseSprite();
        // After a random number of seconds, explodes the spheres prematurely.
        randomExplode();
    }

    // Sets the position of the Whirling Spheres, depending on the angle of the Whirling Spheres.
    private void setStart(float angle) {
        // Note the use of the angle to offset the position.
        setPosition((float) (owner.getX() + owner.getWidth() / 2 - getWidth() / 2 + Math.cos(angle)),
                (float) (owner.getY() + owner.getHeight() / 2 - getHeight() / 2 + Math.sin(angle)));
        // Sets the velocity also according to the angle.
        vel.x = (float) (Math.cos(angle)) * 2;
        vel.y = (float) (Math.sin(angle)) * 2;
    }

    private void randomExplode() {
        // Explodes somewhere between 0.2 and 0.7 seconds.
        float random = (float) Math.random() / 2 + 0.2f;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // If the whirling spheres has not already exploded, explodes, creating smaller shards.
                if (!exploding) {
                    terrain = true;
                    explode();
                }
            }
        }, random);
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

    // Quickly spins the whirling spheres.
    private void rotate() {
        setOriginCenter();
        setRotation(getRotation() + 5);
    }

    protected void chooseSprite() {
        setRegion(animate.getKeyFrame(animationTime, true));
    }

    protected void createAnimations() {
        animate = new Animation(0.1f, proj);
    }

    // When a Whirling Sphere hits either a terrain object or Daur.
    public void explode() {
        // If the Whirling Spheres is destroyed by terrain, splinters into shards.
        if (terrain)
            spawnShards();
        // Rest of the explosion method.
        final Projectile projectile = this;
        vel.x = 0;
        vel.y = 0;
        // Fades the Whirling Spheres out before removing it.
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

    // Spawns five smaller shards that splinter into different directions. Splinters into roughly a star-shaped pattern.
    private void spawnShards() {
        // Creates a shard at the same angle as the whirling spheres.
        final Splinter shard1 = new Splinter(screen, map, atlas, this, angle);
        final Splinter shard2 = new Splinter(screen, map, atlas, this, angle + (float) Math.PI * 0.4f);
        final Splinter shard3 = new Splinter(screen, map, atlas, this, angle + (float) Math.PI * 0.8f);
        final Splinter shard4 = new Splinter(screen, map, atlas, this, angle + (float) Math.PI * 1.2f);
        final Splinter shard5 = new Splinter(screen, map, atlas, this, angle + (float) Math.PI * 1.6f);
        // Adds all the shards to the renderer after 0.05 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.projectiles.add(shard1);
                screen.projectiles.add(shard2);
                screen.projectiles.add(shard3);
                screen.projectiles.add(shard4);
                screen.projectiles.add(shard5);
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
            // Sets terrain to true to inform the game to splinter off the Whirling Spheres.
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
            // Sets terrain to true to inform the game to splinter off the Whirling Spheres.
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
