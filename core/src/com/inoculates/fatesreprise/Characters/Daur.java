
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Consumables.*;
import com.inoculates.fatesreprise.Effects.*;
import com.inoculates.fatesreprise.Events.*;
import com.inoculates.fatesreprise.Events.Event;
import com.inoculates.fatesreprise.Interactables.*;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.MeleeWeapons.BasicSword;
import com.inoculates.fatesreprise.Projectiles.Projectile;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Spells.WindSickle;
import com.inoculates.fatesreprise.UI.*;
import com.inoculates.fatesreprise.UI.Heart;
import com.inoculates.fatesreprise.Worlds.UnderWorld;
import com.inoculates.fatesreprise.Worlds.UpperWorld;

import java.awt.*;
import java.util.ArrayList;

// Daur class responsible for every action of the main character, Daur. The most complicated character class.
public class Daur extends Character {
    // All of these static integers represent the various states Daur goes through.
    // Each state Daur is set to will cause Daur to have a different animation.
    private final int ATTACKING = 2, RUNNING = 3, PUSHING = 4, JUMPING = 5, SWIMMING = 6, FALLING = 7, DROWNING = 8,
            ITEMAQ = 9, CASTING = 10, KNOCKOUT = 11, JUMPATTACKING = 12;

    // Animations of Daur, which as mentioned are determined by the state
    Animation idle, run, push, cast, attack, jump, swim, falling, drowning, itemAQ, knockout;

    // The collision direction of Daur, which determines the orientation of his frames and animations when he is pushing
    // on something. This is to ensure that regardless of whatever direction the player is moving Daur in, he will remain
    // pushing in the same direction.
    int collisionDir;

    // Whether Daur is currently invulnerable (cannot be harmed). Also whether Daur is shielding.
    public boolean invulnerability = false, shielding = false;

    // Whether Daur is slowed, on grass, in dialogue, displaying his wounded animation (flashing colors), falling down
    // a hole, attacking, his spell's on cooldown, and swimming.
    private boolean slowed = false, onGrass = false;
    private boolean talkPressed = false;
    private boolean transitioning = false;
    private boolean fallingHole = false;
    private boolean attacking = false;
    private boolean spellCooldown = false;
    private boolean swimming = false;
    private boolean grounded = true;
    private boolean onPlatform = false;
    private boolean dying = false;

    // The max possible speed of Daur, the amount of time Daur has to push on a block for it to move, and the amount of
    // time Daur has been shielding.
    private float maxSpeed = 0, blockTime = 0, shieldTime;
    // The jumping velocity of Daur. This is added to the normal velocity so that it appears as though Daur is jumping.
    private float jumpVelocity = 0, platformVelX = 0, platformVelY = 0;

    // This timer creates a concurrent thread that is responsible for the slowing effect when Daur swims.
    private Timer dragTimer = new Timer();
    // This timer is used to fill up Daur's hearts.
    private Timer heartTimer = new Timer();
    // The grass effect that will stick to Daur.
    private Grass grass;
    // The ripple effect that will also stick to Daur.
    private Ripple ripple;
    // The shadow effect that will be beneath Daur when in the air.
    private Shadow shadow;
    // The shield effect that will guard Daur.
    private Shield shield;
    // The block that Daur is currently moving.
    private Block moveBlock;
    // Daur's sword, that appears when swung.
    private BasicSword sword;
    // The array list that represents the heart UI elemnt shown to the user.
    private ArrayList<Heart> hearts = new ArrayList<Heart>();
    // The respawn point of the Daur (if he dies), and the spawn point (if he falls down a hole and must be spawned).
    private Point respawnPoint, spawnPoint;
    // The bar for the UI on the game that hovers on the top edge of the screen.
    BlueBarUI blueBar;

    // The regions that serve as frames for the animations of Daur. Note that nearly every frame has FOUR directions to
    // it, including the idle and walking animations.
    TextureAtlas.AtlasRegion FD1 = atlas.findRegion("bottom1"), FD2 = atlas.findRegion("bottom2"),
            FU1 = atlas.findRegion("top1"), FU2 = atlas.findRegion("top2"), FL1 = atlas.findRegion("left1"),
            FL2 = atlas.findRegion("left2"), FR1 = atlas.findRegion("right1"), FR2 = atlas.findRegion("right2");

    TextureAtlas.AtlasRegion PD1 = atlas.findRegion("bottompush1"), PD2 = atlas.findRegion("bottompush2"),
            PU1 = atlas.findRegion("toppush1"), PU2 = atlas.findRegion("toppush2"), PR1 = atlas.findRegion("rightpush1"),
            PR2 = atlas.findRegion("rightpush2"), PL1 = atlas.findRegion("leftpush1"), PL2 = atlas.findRegion("leftpush2");

    TextureAtlas.AtlasRegion SU1 = atlas.findRegion("topswim1"), SU2 = atlas.findRegion("topswim2"),
            SD1 = atlas.findRegion("bottomswim1"), SD2 = atlas.findRegion("bottomswim2"), SL1 = atlas.findRegion("leftswim1"),
            SL2 = atlas.findRegion("leftswim2"), SR1 = atlas.findRegion("rightswim1"), SR2 = atlas.findRegion("rightswim2");

    TextureAtlas.AtlasRegion AD1 = atlas.findRegion("attackdown1"), AD2 = atlas.findRegion("attackdown2"),
            AU1 = atlas.findRegion("attackup1"), AU2 = atlas.findRegion("attackup2"), AL1 = atlas.findRegion("attackleft1"),
            AL2 = atlas.findRegion("attackleft2"), AR1 = atlas.findRegion("attackright1"), AR2 = atlas.findRegion("attackright2");

    TextureAtlas.AtlasRegion JD1 = atlas.findRegion("jumpD1"), JD2 = atlas.findRegion("jumpD2"), JD3 = atlas.findRegion("jumpD3"),
            JU1 = atlas.findRegion("jumpU1"), JU2 = atlas.findRegion("jumpU2"), JU3 = atlas.findRegion("jumpU3"),
            JR1 = atlas.findRegion("jumpR1"), JR2 = atlas.findRegion("jumpR2"), JR3 = atlas.findRegion("jumpR3"),
            JL1 = atlas.findRegion("jumpL1"), JL2 = atlas.findRegion("jumpL2"), JL3 = atlas.findRegion("jumpL3");

    TextureAtlas.AtlasRegion F1 = atlas.findRegion("falling1"), F2 = atlas.findRegion("falling2"), F3 = atlas.findRegion("falling3");

    TextureAtlas.AtlasRegion DR = atlas.findRegion("drown");

    TextureAtlas.AtlasRegion KO = atlas.findRegion("dead");

    TextureAtlas.AtlasRegion IA = atlas.findRegion("itemacquired");

    // Initializes the Daur class by setting Daur's atlas, layer, screen, map, frames, and animations. Additionally sets
    // the mana, coins, and health in case a game has been loaded. Also creates the effects and the UI for the game.
    public Daur(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, screen.storage);
        health = storage.health;

        // Creates effects.
        grass = new Grass(screen, map, screen.daurAtlases.get(3), this);
        ripple = new Ripple(screen, map, screen.daurAtlases.get(3), this);
        shadow = new Shadow(screen, map, screen.miscAtlases.get(1), this, getX(), getY(), 1);

        // Creates UI bar for the user to see the various UI elements.
        blueBar = new BlueBarUI(screen, screen.daurAtlases.get(1));
        screen.UIS.add(blueBar);

        // Creates each individual heart and adds to the UI bar.
        for (int i = 0; i < storage.maxHealth / 2; i++) {
            Heart heart = new Heart(screen, screen.daurAtlases.get(1), blueBar, i);
            hearts.add(heart);
            screen.UIS.add(heart);
        }
        updateHearts();
    }

    // This overrides the character draw method purely to draw the sword so that it displays at all times.
    public void draw(Batch batch) {
        super.draw(batch);
        if (attacking)
            sword.draw(batch);
    }

    // Checks for key inputs, collision of enemies, and checks whether Daur is currently in fog.
    protected void update(float deltaTime) {
        processKeys();
        collidesEnemy();
        checkFog();
    }

    // This method moves Daur depending on the x and y components of its velocity. Additionally, it checks for any
    // collisions, and for any change in condition (like slow).
    private void tryMove() {
        checkCollisions();
        detectPlatform();
        detectConditions();
    }

    // Sets the velocity components of the character depending on the modifiers. Overidden to prevent changes during
    // transitioning.
    protected void SVX(float x) {
        if (transitioning)
            return;

        vel.x = x - modifierX * Math.signum(x);
    }

    protected void SVY(float y) {
        if (transitioning)
            return;

        vel.y = y - modifierY * Math.signum(y);
    }

    // Checks whether Daur's condition has changed depending on its position.
    private void detectConditions() {
        // Checks if Daur is slowed.
        if (detectSlow() && grounded) {
            slowed = true;
            setModifier(0.35f, 0.35f);
            // If currently slowed, but not in a slow cell, removes the slow effect.
        } else if (slowed) {
            slowed = false;
            resetModifier(0.35f, 0.35f);
        }

        // Checks if any grass has been detected (if there is no grass type there is no grass).
        if (detectGrass() != 4 && grounded) {
            // Sets the grass type accordingly.
            grass.setType(detectGrass());
            // If previously not on grass, adds the grass screen effect and sets the position. Additionally, sets the
            // frame.
            if (!onGrass) {
                onGrass = true;
                screen.effects.add(grass);
                grass.setPosition(getX(), getY());
                grass.setAnimationTime(animationTime);
            }
            // Else if Daur was on grass previously, but no more, removes the grass and sets Daur's condition
            // accordingly.
        } else {
            onGrass = false;
            screen.effects.remove(grass);
        }

        // If Daur is on shallow water, a ripple effect will be added to Daur, similarly to the grass effect.
        if (detectShallowWater() && grounded) {
            if (!screen.effects.contains(ripple)) {
                screen.effects.add(ripple);
                ripple.setPosition(getX(), getY());
                ripple.setAnimationTime(animationTime);
            }
            // If the Daur has a ripple effect, but is not in shallow water, the game removes the effect.
        }
        else if (screen.effects.contains(ripple))
            screen.effects.remove(ripple);

        // If Daur is in deep enough water to swim, Daur will begin to swim, and his state will be set accordingly.
        if (grounded && detectWater() || (detectDeepWater() && canSwim() && grounded)) {
            swimming = true;
            setState(SWIMMING, true);
        }
        // Else if daur was previously swimming, ensures that Daur is no longer swimming, and allows him to move
        // freely. Also immediately sets his state to idle.
        else if (swimming) {
            unStun();
            swimming = false;
            setState(IDLE, true);
        }

        // If Daur is in water that is too deep for him to traverse (without proper swim gear) he drowns.
        if (detectDeepWater() && !canSwim() && grounded)
            drown();

        // If the Daur is currently in a hole, causes him to fall.
        if (detectHole() != null && grounded && !onPlatform) {
            // Creates the boolean variable isFalling, to check if Daur is close enough to fall, or only close enough
            // to gravitate.
            boolean isFalling = false;
            // Gets the middle of the hole cell.
            Point holePoint = detectHole();
            // Distance from Daur to the middle of the hole cell.
            float distanceX = Math.abs(holePoint.x - getCX());
            float distanceY = Math.abs(holePoint.y - getCY());
            // If Daur is sufficiently close in both the x and y respects, causes him to fall.
            if (distanceX < getWidth() / 2.25f && distanceY < getHeight() / 2.25f) {
                fallHole(holePoint);
                isFalling = true;
            }
            // Otherwise simply gravitates him.
            if (!isFalling) {
                fallingHole = true;
                gravitateHole(holePoint);
            }
        }
        // Else if Daur is not in a hole cell, but was falling previously, snaps him back to a normal state. Also reduces
        // his acceleration to zero, so he is no longer gravitating.
        else if (fallingHole) {
            fallingHole = false;
            ace.x = 0;
            ace.y = 0;
        }
    }

    // This detects if Daur is currently on a platform. Should he be, his velocity is modified by the platform's, and
    // he will not fall down.
    private void detectPlatform() {
        // Does not work if in the air.
        if (!grounded) {
            platformVelX = 0;
            platformVelY = 0;
            onPlatform = false;
            return;
        }
        for (Interactable interactable : screen.interactables)
            if (interactable instanceof Platform && screen.isInView(interactable)) {
                Platform platform = (Platform) interactable;
                // If the vertices of Daur are inside a platform, sets onPlatform to true.
                if (platform.getBoundingRectangle().contains(getX(), getCY()) ||
                        platform.getBoundingRectangle().contains(getRX(), getCY()) ||
                        platform.getBoundingRectangle().contains(getCX(), getY()) ||
                        platform.getBoundingRectangle().contains(getCX(), getRY() - 5) ||
                        platform.getBoundingRectangle().contains(getX(), getRY() - 5) ||
                        platform.getBoundingRectangle().contains(getX(), getY()) ||
                        platform.getBoundingRectangle().contains(getRX(), getRY() - 5) ||
                        platform.getBoundingRectangle().contains(getRX(), getY())) {
                    onPlatform = true;
                    platformVelX = platform.getVelX();
                    platformVelY = platform.getVelY();
                    return;
                }
            }
        platformVelX = 0;
        platformVelY = 0;
        onPlatform = false;
    }

    // This is what occurs if Daur were to fall down a hole.
    private void fallHole(Point hole) {
        // Resets channeling as Daur is falling.
        stopChanneling();
        // If Daur is already falling, no need to make him fall down twice.
        if (state == FALLING)
            return;
        // Sets the state to falling for the animation.
        setState(FALLING, true);
        // This method adjusts the position of Daur's sprite to emulate Daur falling down the center of the hole.
        resetPosition(hole);
        // Causes Daur to be motionless and receive no input.
        vel.x = 0;
        vel.y = 0;
        ace.x = 0;
        ace.y = 0;
        stun();

        // After one second of falling, Daur's health will be reduced, he will flicker to show he has been hurt,
        // his position will be reset to the spawn point, he will be unstunned, and his state will be set to idle.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                loseHealth(1);
                setPosition(spawnPoint.x, spawnPoint.y);
                unStun();
                setState(IDLE, true);
            }
        }, 1);
    }

    // This is what occurs if Daur were to drown in a deepwater area.
    private void drown() {
        // Resets channeling.
        stopChanneling();
        // If Daur is already drowning, returns.
        if (state == DROWNING)
            return;
        // Else sets the state to drowning.
        setState(DROWNING, true);
        // Immobilizes and stuns Daur.
        freeze();
        stun();
        // Adjusts Daur's new sprite position to the center of the tile to simulate drowning.
        setPosition((int) ((getCX()) / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2 - getWidth() / 2,
                (int) ((getY() + 2) / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2 - getHeight() / 2);
        chooseSprite();
        setPosition((int) ((getCX()) / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2 - getWidth() / 2,
                (int) ((getY() + 2) / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2 - getHeight() / 2);
        // Adds the small splash effect.
        Splash splash = new Splash(screen, map, screen.daurAtlases.get(3), this);
        screen.effects.add(splash);

        // After 0.75 seconds of falling, Daur's health will be reduced, he will flicker to show he has been hurt,
        // his position will be reset to the spawn point, he will be unstunned, and his state will be set to idle.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                loseHealth(1);
                setPosition(spawnPoint.x, spawnPoint.y);
                unStun();
                setState(IDLE, true);
                detectConditions();
            }
        }, 0.75f);
    }

    // With every new frame, Daur's position is reset due to the vastly different sizes of his sprites during his falling
    // animation. Note that the time is rounded up to ensure that Daur has changed his sprite (as the frame and timer
    // do not line up perfectly).
    private void resetPosition(final Point hole) {
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                chooseSprite();
                setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
            }
        }, 0.000001f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                chooseSprite();
                setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
            }
        }, 0.33333334f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                chooseSprite();
                setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
            }
        }, 0.666666667f);
    }

    // This method is responsible for constantly accelerating Daur towards the center of the hole.
    private void gravitateHole(Point holePoint) {
        // Gets angle between Daur and center of the hole.
        float angle = (float) Math.atan2(holePoint.y - getY() - getHeight() / 2, holePoint.x - getX() - getWidth() / 2);
        // Causes Daur to accelerate towards the hole, but not by too much.
        ace.x = (float) Math.cos(angle) / 10;
        ace.y = (float) Math.sin(angle);
        // Sets the max speed to ensure Daur does not fly into the hole at super speeds.
        maxSpeed = 0.5f;
    }

    // Large method that checks for any collisions with other characters, objects, or terrain.
    private void checkCollisions() {
        // Sets the current x and y values. This retains the soon-to-be "old" position components.
        float oldX = getX(), oldY = getY(), oldSY = shadow.getY();
        // The boolean for collisions.
        boolean collisionX = false, collisionY = false;

        // Accelerates Daur accordingly.
        vel.x += ace.x;

        // Ensures that Daur does not accelerate out of control by setting a maximum speed.
        if (ace.x != 0 && Math.abs(vel.x) > maxSpeed)
            vel.x = Math.signum(vel.x) * maxSpeed;

        // Adds velocity to x value.
        setX(getX() + vel.x + platformVelX);
        // Sets shadow's position accordingly.
        shadow.setPosition(getCX() - shadow.getWidth() / 2, shadow.getY());

        // Detects collision and if there is one, moves Daur back. The first if statement is if Daur is moving to the
        // left and the second if to the right.
        if (vel.x < 0 && grounded)
            // If Daur has collided with terrain or an interactable while on the ground.
            collisionX = collidesLeft() || collidesInteractable() || collidesHalfBlockLeft() ||
                    // If Daur is stunned, ensures that he does not go to another cell. This is to prevent the user from
                    // feeling too confused.
                    (getX() < storage.cellX * layer.getTileWidth() * 10 && stun && !swimming);
        else if (vel.x > 0 && grounded)
            collisionX = collidesRight() || collidesInteractable() || collidesHalfBlockRight() ||
                    (getX() + getWidth() > (storage.cellX + 1) * layer.getTileWidth() * 10 && stun && !swimming);
        else if (!grounded)
            collisionX = collidesShadow();

        // If Daur has collided in any way on the x-axis. NOTE: this is only for terrain.
        if (collisionX) {
            // Sets the collision direction of Daur depending on the x component of his velocity.
            if (vel.x > 0)
                collisionDir = 1;
            else collisionDir = -1;

            // If collision occurs, sets the x value of Daur back to the old one, and causes him to stop.
            setX(oldX);
            shadow.setPosition(getCX() - shadow.getWidth() / 2, shadow.getY());
            vel.x = 0;

            // If Daur has collided while swimming, also sets the y component of his velocity to zero, unstuns him to
            // allow movement, and clears all instances of drag.
            if (swimming) {
                dragTimer.clear();
                unStun();
                setY(oldY);
                vel.y = 0;
            }
            else
                // If not swimming, Daur's state is set to pushing. However, this does NOT override any other state.
                setState(PUSHING, false);
        }

        // If Daur collides with character, does largely the same as terrain. However, Daur will not be set to his
        // pushing state.
        if (collidesCharacter()) {
            setX(oldX);
            vel.x = 0;

            if (swimming) {
                dragTimer.clear();
                unStun();
                setY(oldY);
                vel.y = 0;
            }
        }

        // Similar to the x version, but for the y component of Daur's velocity.
        vel.y += ace.y;

        if (ace.y != 0 && Math.abs(vel.y) > maxSpeed)
            vel.y = Math.signum(vel.y) * maxSpeed;

        // Adds velocity and jump velocity to y value.
        setY(getY() + vel.y + jumpVelocity + platformVelY);
        // Sets shadow's position accordingly.
        shadow.setPosition(getCX() - shadow.getWidth() / 2, shadow.getY() + vel.y);

        // Detects collision on the y axis.
        if (vel.y < 0 && grounded)
            collisionY = collidesBottom() || collidesInteractable() || collidesHalfBlockBottom() ||
                    (getY() < storage.cellY * layer.getTileHeight() * 10 && stun && !swimming);
        else if (vel.y > 0 && grounded)
            collisionY = collidesTop() || collidesInteractable() || collidesHalfBlockTop() ||
                    (getY() + getHeight() > (storage.cellY + 1) * layer.getTileHeight() * 10 - 16 && stun && !swimming);
            // If not on the ground, collision is based on the shadow of Daur.
        else if (!grounded)
            collisionY = collidesShadow();

        if (collisionY) {
            if (vel.y > 0)
                collisionDir = 2;
            else collisionDir = -2;

            setY(oldY);
            shadow.setY(oldSY);
            vel.y = jumpVelocity;
            setY(getY() + jumpVelocity);

            if (swimming) {
                dragTimer.clear();
                unStun();
                setX(oldX);
                vel.x = 0;
            }
            else
                setState(PUSHING, false);
        }

        if (collidesCharacter()) {
            setY(oldY);
            if (swimming) {
                dragTimer.clear();
                unStun();
                setX(oldX);
                vel.x = 0;
            }
        }

        // If jumping, constantly reduces jump velocity so it appears as though Daur is falling.
        if (state == JUMPING || state == JUMPATTACKING) {
            // If Daur is below the shadow, ends the jump.
            if (getY() < shadow.getY())
                endJump();
                // Lowers jump velocity otherwise.
            else
                jumpVelocity -= 0.1345;
        }

        if (!collisionY && !collisionX && state == PUSHING)
            setState(IDLE, false);
    }

    // Checks if Daur has collided with one of the interactables on the screen by checking if their bounding rectangles
    // have intersected.
    protected boolean collidesInteractable() {
        for (Interactable interactable : screen.interactables) {
            for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                    if (interactable.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2)) {
                        // Checks if the interactable Daur has collided with is a block.
                        checkBlock(interactable);
                        // Check if the interactable Daur has collided with is a locked door.
                        checkLockedDoor(interactable);
                        // If interactable is not a teleporter, collides with the interactable. Also does not collide if
                        // Daur is inside the chest.
                        if (!(interactable instanceof Teleporter || interactable instanceof Platform))
                            return true;
                    }
        }
        return false;
    }

    // Checks if Daur has collided with one of the consumables on the screen by checking if their bounding rectangles
    // have intersected.
    protected boolean collidesConsumable() {
        for (Consumable consumable : screen.consumables) {
            for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                    if (consumable.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2)) {
                        // Does the corresponding action.
                        eatConsumable(consumable);
                        return true;
                    }
        }
        return false;
    }

    // Checks if the SHADOW of Daur has collides. This is used to determine if Daur has collided while jumping. As he
    // must return to his shadow, his shadow is used to determine collision.
    protected boolean collidesShadow() {
        for (float x = getX() + 1; x < getX() + getWidth(); x ++)
            for (float y = shadow.getY() + 1; y < shadow.getY() + shadow.getHeight() + 5; y ++)
                if (isCellBlocked(x, y) || isCellPost(x + 6, y) || isCellShrub(x + 4, y) || collidesHalfBlock(x, y))
                    return true;
        return (shadow.collidesCharacter() || shadow.collidesInteractable());
    }

    // Checks if the shadow has collided with any half blocks.
    private boolean collidesHalfBlock(float x, float y) {
        // Daur is moving right, checks for right half block collision.
        if (vel.x > 0 && vel.y == 0) {
            if (isCellHalfBlockedR(getRX() - 8, y) || isCellHalfBlockedL(shadow.getRX(), y) || isCellHalfBlockedT(x, shadow.getRY() - 4) ||
                    isCellHalfBlockedB(x, shadow.getY() + 8) || isCellHalfBlockedBR(getRX() - 8, shadow.getY() + 8) ||
                    isCellHalfBlockedTR(getRX() - 8, shadow.getRY() - 4) || isCellHalfBlockedBL(shadow.getRX(), shadow.getY() + 8) ||
                    isCellHalfBlockedTL(shadow.getRX(), shadow.getRY() - 4))
                return true;
        }
        // Daur is moving right and up, checks for right or top half block collision.
        else if (vel.x > 0 && vel.y > 0) {
            if (isCellHalfBlockedR(getRX() - 8, y) || isCellHalfBlockedL(shadow.getRX(), y) || isCellHalfBlockedT(x, shadow.getRY() - 4) ||
                    isCellHalfBlockedB(x, shadow.getRY() + 2) || isCellHalfBlockedBR(getRX() - 8, shadow.getRY() + 2) ||
                    isCellHalfBlockedTR(getRX() - 8, shadow.getRY() - 4) || isCellHalfBlockedBL(shadow.getRX(), shadow.getRY() + 2) ||
                    isCellHalfBlockedTL(shadow.getRX(), shadow.getRY() - 4))
                return true;
        }
        // Daur is moving right and bottom, checks for right or bottom half block collision.
        else if (vel.x > 0 && vel.y < 0) {
            if (isCellHalfBlockedR(getRX() - 8, y) || isCellHalfBlockedL(shadow.getRX(), y) || isCellHalfBlockedT(x, shadow.getY() + 1) ||
                    isCellHalfBlockedB(x, shadow.getY() + 8) || isCellHalfBlockedBR(getRX() - 8, shadow.getY() + 8) ||
                    isCellHalfBlockedTR(getRX() - 8, shadow.getY() + 1) || isCellHalfBlockedBL(shadow.getRX(), shadow.getY() + 8) ||
                    isCellHalfBlockedTL(shadow.getRX(), shadow.getY() + 1))
                return true;
        }
        // Daur is moving left, checks for left half block collision.
        else if (vel.x < 0 && vel.y == 0) {
            if (isCellHalfBlockedL(getX() + 8, y) || isCellHalfBlockedR(shadow.getX() - 1, y) || isCellHalfBlockedT(x, shadow.getRY() - 4) ||
                    isCellHalfBlockedB(x, shadow.getY() + 8) || isCellHalfBlockedBR(shadow.getX() - 1, shadow.getY() + 8) ||
                    isCellHalfBlockedTR(shadow.getX() - 1, shadow.getRY() - 4) || isCellHalfBlockedBL(getX() + 8, shadow.getY() + 8) ||
                    isCellHalfBlockedTL(getX() + 8, shadow.getRY() - 4))
                return true;
        }
        // Daur is moving left and up, checks for left and top half block collision.
        else if (vel.x < 0 && vel.y > 0) {
            if (isCellHalfBlockedR(shadow.getX() - 1, y) || isCellHalfBlockedL(getX() + 8, y) || isCellHalfBlockedT(x, shadow.getRY() - 4) ||
                    isCellHalfBlockedB(x, shadow.getRY() + 2)  || isCellHalfBlockedBR(shadow.getX() - 1, shadow.getRY() + 2) ||
                    isCellHalfBlockedTR(shadow.getX() - 1, shadow.getRY() - 4) || isCellHalfBlockedBL(getX() + 8, shadow.getRY() + 2) ||
                    isCellHalfBlockedTL(getX() + 8, shadow.getRY() - 4))
                return true;
        }
        // Daur is moving left and down, checks for left and bottom half block collision.
        else if (vel.x < 0 && vel.y < 0) {
            if (isCellHalfBlockedR(shadow.getX() - 1, y) || isCellHalfBlockedL(getX() + 8, y) || isCellHalfBlockedT(x, shadow.getY() + 1) ||
                    isCellHalfBlockedB(x, shadow.getY() + 8)  || isCellHalfBlockedBR(shadow.getX() - 1, shadow.getY() + 8) ||
                    isCellHalfBlockedTR(shadow.getX() - 1, shadow.getY() + 1) || isCellHalfBlockedBL(getX() + 8, shadow.getY() + 8) ||
                    isCellHalfBlockedTL(getX() + 8, shadow.getY() + 1))
                return true;
        }
        // Daur is moving up, checks for top half block collision.
        else if (vel.y > 0 && vel.x == 0) {
            if (isCellHalfBlockedT(x, shadow.getRY() - 4) || isCellHalfBlockedB(x, shadow.getRY() + 2) || isCellHalfBlockedR(shadow.getRX() - 8, y) ||
                    isCellHalfBlockedL(getX() + 8, y) || isCellHalfBlockedBR(getRX() - 8, shadow.getRY() + 2) ||
                    isCellHalfBlockedTR(getRX() - 8, shadow.getRY() - 4) || isCellHalfBlockedBL(getX() + 8, shadow.getRY() + 2) ||
                    isCellHalfBlockedTL(getX() + 8, shadow.getRY() - 4))
                return true;
        }
        // Daur is moving down, checks for bottom half block collision.
        else if (vel.y < 0 && vel.x == 0) {
            if (isCellHalfBlockedB(x, shadow.getY() + 8) || isCellHalfBlockedT(x, shadow.getY() + 1) || isCellHalfBlockedR(shadow.getRX() - 8, y) ||
                    isCellHalfBlockedL(getX() + 8, y) || isCellHalfBlockedBR(getRX() - 8, shadow.getY() + 8) ||
                    isCellHalfBlockedTR(getRX() - 8, shadow.getY() + 1) || isCellHalfBlockedBL(getX() + 8, shadow.getY() + 8) ||
                    isCellHalfBlockedTL(getX() + 8, shadow.getY() + 1))
                return true;
        }
        return false;
    }

    // Checks if Daur has collided with a block, and if so, pushes on the block.
    private void checkBlock(final Interactable interactable) {
        // Checks if the collided interactable is indeed a block.
        if (interactable instanceof Block) {
            // Casts interactable to a block to properly use methods.
            Block block = (Block) interactable;
            // If Daur is currently not pushing on a block, or if this block is a new block, sets the current block
            // to the the one Daur is pushing on.
            if (moveBlock == null || !moveBlock.equals(block)) {
                moveBlock = block;
                // Also resets the amount of time Daur has to push on the block to move it.
                blockTime = 0;
            }
            // Else if the block is the same one Daur is pushing on, and he has been pushing on it for more than 0.75
            // seconds, moves it.
            else if (blockTime > 0.75f) {
                // Moves block in accordance with Daur's direction.
                block.move(dir);
                // Also stuns Daur for 0.25 seconds to ensure that Daur does not buggily collide with the moving block.
                stun();
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        unStun();
                    }
                }, 0.25f);
            }
        }
    }

    // Checks if Daur has collided with a locked door, and if so, opens the door.
    private void checkLockedDoor(final Interactable interactable) {
        // Checks if the collided interactable is indeed a door.
        if (interactable instanceof LockedDoor) {
            // Casts interactable to a block to properly use methods.
            LockedDoor door = (LockedDoor) interactable;
            // If facing the door, the door is not yet unlocked, and Daur has a key, opens it and loses a key.
            if (facingObject(door) && !door.isUnlocked() && door.canOpen()) {
                // Opens the door.
                door.open(dir);
                // Removes the key based on the dungeon.
                storage.removeKey();
            }
        }
        // Checks if the collided interactable is alternatively a BOSS door.
        if (interactable instanceof BossLockedDoor) {
            // Same as above.
            BossLockedDoor door = (BossLockedDoor) interactable;
            if (facingObject(door) && !door.isUnlocked() && hasBossKey()) {
                // Opens the door.
                door.open(dir);
                // Removes the key based on the dungeon.
                removeBossKey();
            }
        }
    }

    // Depending on the dungeon, checks if Daur has the boss key.
    private boolean hasBossKey() {
        switch (storage.dungeon) {
            case 0:
                for (Item item : storage.questItems)
                    if (item instanceof GreatHollowBossKey)
                        return true;
                return false;
        }
        return false;
    }

    // Depending on dungeon removes boss
    private void removeBossKey() {
        Item questItem = storage.questItems.get(0);
        switch (storage.dungeon) {
            case 0:
                for (Item item : storage.questItems)
                    if (item instanceof GreatHollowBossKey)
                        questItem = item;
        }
        storage.questItems.remove(questItem);
    }

    // This method consumes the consumables and does the corresponding action.
    public void eatConsumable(Consumable consumable) {
        // Temporary integer to express the coins gained.
        int coins = 0;
        // Coins of different grades confer wealth to Daur.
        if (consumable instanceof Bronze)
            coins = 1;
        if (consumable instanceof Copper)
            coins = 5;
        if (consumable instanceof Silver)
            coins = 10;
        if (consumable instanceof Gold)
            coins = 20;
        if (consumable instanceof Diamond)
            coins = 50;
        // A heart simply gives one full heart (2 health).
        if (consumable instanceof com.inoculates.fatesreprise.Consumables.Heart) {
            health += 2;
            // If the health of Daur is greater than the total health, sets his health to the total health.
            if (health > hearts.size() * 2)
                health = hearts.size() * 2;
        }

        // Updates the storage, regardless if the coins or health of the Daur has been updated. This is to get rid of
        // unnecessary code.
        // Coins cannot go above 999.
        if (storage.coins + coins < 999)
            storage.setCoins(storage.coins + coins);
        else
            storage.setCoins(999);
        storage.health = health;
        // Also updates the heart UI if need be.
        updateHearts();
        // Removes consumable after consuming it.
        consumable.consume();
    }

    // This adds a heart piece to Daur's list. If Daur has all four pieces, adds to his max health.
    public void addHeartPiece() {
        screen.storage.incHeartPiece();
        // If Daur has all four pieces, adds to Daur's max health, and fills all his empty hearts up.
        if (screen.storage.heartPieces > 3) {
            screen.storage.fillHeart();
            Heart heart = new Heart(screen, screen.daurAtlases.get(1), blueBar, screen.storage.maxHealth / 2 - 1);
            hearts.add(heart);
            screen.UIS.add(heart);
            fillHearts(6);
            updateHearts();
        }
    }

    // This creates an entirely new heart, as well as healing Daur to full.
    public void incrementMaxHealth() {
        storage.incMaxHealth();
        Heart heart = new Heart(screen, screen.daurAtlases.get(1), blueBar, screen.storage.maxHealth / 2 - 1);
        hearts.add(heart);
        screen.UIS.add(heart);
        fillHearts(storage.maxHealth);
        updateHearts();
    }

    // Fills the hearts of Daur, with a time-based stagger in between to the amount specified.
    private void fillHearts(int amount) {
        // For every health lost, increments health in an interval of 0.1.
        for (int i = 1; i <= amount; i++)
            heartTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    // If already filled, no need to fill it any further.
                    if (health == storage.maxHealth) {
                        heartTimer.clear();
                        return;
                    }
                    // Else increments health.
                    health ++;
                    // Checks if the number exceeds the max health.
                    if (health > storage.maxHealth)
                        health = storage.maxHealth;
                    // Updates health accordingly.
                    storage.setHealth(health);
                    updateHearts();
                }
            }, 0.2f * i);
    }

    // This large method is used to move Daur by a constant rate as opposed to short bursts. Also checks to see if Daur
    // is talking with a character.
    private void processKeys() {
        // This moves Daur to the left if the left arrow key is pressed. The amount differs based on whether Daur is
        // swimming, or simply running.
        if (Gdx.input.isKeyPressed(storage.moveLeft) && !Gdx.input.isKeyPressed(storage.moveRight) && !stun) {
            // If Daur is not already moving in the y direction, and is not pushing on something, sets his direction to
            // the left.
            if (vel.y == 0 && state != PUSHING)
                dir = LEFT;

            // If Daur is already pushing to the left, sets the direction also to the left. This is purely for the
            // purpose of ensuring that Daur is still facing left when he stops pushing.
            if (state == PUSHING && collisionDir == -1)
                dir = LEFT;

            // Sets Daur's state to running if not pushing or jumping.
            if (state != JUMPING && state != PUSHING)
                setState(RUNNING, false);

            // This unused code only applies if Daur is on a platform. Essentially adds the platform's velocity to
            // Daur's own to ensure that he is moving in a proper fashion when on a platform.
            if (vel.x != 0 && Math.signum(vel.x) == dir && pX)
                vel.x = (1.5f + Math.abs(vel.x)) * Math.signum(dir);

                // If swimming, engages the swim method to properly move Daur. This is to ensure that Daur swims with drag,
                // and does not swim with a constant velocity.
            else if (swimming) {
                // Swims in the top-right direction.
                if (Gdx.input.isKeyPressed(storage.moveUp))
                    swim(4);
                    // Swims in the bottom-right direction.
                else if (Gdx.input.isKeyPressed(storage.moveDown))
                    swim(5);
                    // Else swims in simply the left direction.
                else swim(0);
            }
            // Else if NOT swimming, simply allows Daur to move at a constant negative velocity.
            else
                SVX(-0.75f);
        }

        //Same as the left method, except for the right.
        else if (Gdx.input.isKeyPressed(storage.moveRight) && !Gdx.input.isKeyPressed(storage.moveLeft) && !stun) {
            if (vel.y == 0 && state != PUSHING)
                dir = RIGHT;

            if (state == PUSHING && collisionDir == 1)
                dir = RIGHT;

            if (state != JUMPING && state != PUSHING)
                setState(RUNNING, false);

            if (vel.x != 0 && Math.signum(vel.x) == dir && pX)
                vel.x = (1.5f + Math.abs(vel.x)) * Math.signum(dir);

            else if (swimming) {
                if (Gdx.input.isKeyPressed(storage.moveUp))
                    swim(6);
                else if (Gdx.input.isKeyPressed(storage.moveDown))
                    swim(7);
                else swim(1);
            } else
                SVX(0.75f);
        } else if (!stun && !pX && !swimming && ace.x == 0 && ace.y == 0)
            vel.x = 0;

        // Same but for the up direction.
        if (Gdx.input.isKeyPressed(storage.moveUp) && !Gdx.input.isKeyPressed(storage.moveDown) && !stun) {
            if (vel.x == 0 && state != PUSHING)
                dir = UP;

            if (state == PUSHING && collisionDir == 2)
                dir = UP;

            if (state != JUMPING && state != PUSHING)
                setState(RUNNING, false);

            if (vel.y != 0 && Math.signum(vel.y) == dir && pY)
                vel.y = (1.5f + Math.abs(vel.y)) * Math.signum(dir);

            else if (swimming) {
                if (Gdx.input.isKeyPressed(storage.moveLeft))
                    swim(4);
                else if (Gdx.input.isKeyPressed(storage.moveRight))
                    swim(5);
                else swim(2);
            } else
                SVY(0.75f);
        }

        // Same but for the down direction.
        else if (Gdx.input.isKeyPressed(storage.moveDown) && !Gdx.input.isKeyPressed(storage.moveUp) && !stun) {
            if (vel.x == 0 && state != PUSHING)
                dir = DOWN;

            if (state == PUSHING && collisionDir == -2)
                dir = DOWN;

            if (state != JUMPING && state != PUSHING)
                setState(RUNNING, false);

            if (vel.y != 0 && Math.signum(vel.y) == dir && pY)
                vel.y = (1.5f + Math.abs(vel.y)) * Math.signum(dir);

            else if (swimming) {
                if (Gdx.input.isKeyPressed(storage.moveLeft))
                    swim(6);
                else if (Gdx.input.isKeyPressed(storage.moveRight))
                    swim(7);
                else swim(3);
            } else
                SVY(-0.75f);
        } else if (!stun && !pY && !swimming)
            vel.y = 0;

        // If the Daur's velocity is not completely zero, moves him accordingly, and checks for any portals he may have
        // stepped through while doing so.
        if ((vel.x != 0 || vel.y != 0 || ace.x != 0 || ace.y != 0 || jumpVelocity != 0 || platformVelX != 0 ||
                platformVelY != 0) && state != FALLING && state != DROWNING) {
            tryMove();
            // Only checks for teleportation if grounded.
            if (grounded) {
                checkAccess();
                checkPortals();
                checkTeleporters();
                collidesConsumable();
            }
            checkTriggers();
            // If on platform but immobile, sets state to Idle anyway.
            if (vel.x == 0 && vel.y == 0 && onPlatform)
                setState(IDLE, false);
        }
        // Else sets his state to idle.
        else
            setState(IDLE, false);

        // If the user has pressed the talk button, and the user has not pressed the talk button recently (to ensure the
        // user does not accidently skip dialogue or start dialogue twice).
        if (Gdx.input.isKeyPressed(storage.talk) && !talkPressed) {
            // Sets the button press flag to be true.
            talkPressed = true;

            // Increments the line if Daur is currently engaged in dialogue.
            if (screen.currentTextBox != null)
                screen.currentTextBox.incrementLine();
                // Else if Daur is NOT engaged in dialogue, checks to see whether he passes the conditions to do so.
            else if (!stun)
                checkEvents();

            // Resets the button press flag after 0.25 seconds.
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    resetPressed();
                }
            }, 0.25f);
        }
        // Checks if the user is holding down a key necessary to channel a spell.
        checkChanneling();
    }

    // Allows Daur to swim, with a drag effect similar to Link's Awakening.
    private void swim(final int direction) {
        // Resets channeling.
        stopChanneling();
        // Clears drag screen.globalTimer for fresh delayed events.
        dragTimer.clear();
        // Sets the velocity of Daur depending on the direction he's facing. NOTE: the velocity components are consistent
        // throughout. Meaning, though they might change in sign, they are of the same value.
        switch (direction) {
            case 0:
                SVX(-0.75f);
                break;
            case 1:
                SVX(0.75f);
                break;
            case 2:
                SVY(0.75f);
                break;
            case 3:
                SVY(-0.75f);
                break;
            case 4:
                SVX(-0.75f);
                SVY(0.75f);
                break;
            case 5:
                SVX(-0.75f);
                SVY(-0.75f);
                break;
            case 6:
                SVX(0.75f);
                SVY(0.75f);
                break;
            case 7:
                SVX(0.75f);
                SVY(-0.75f);
                break;
        }
        // Stuns the Daur so he CANNOT move while in the midst of swimming.
        stun();
        // Creates the drag screen.globalTimer and launches the drag screen.globalTimer so that Daur is incrementally slowed until he stops.
        for (int i = 0; i < 4; i++)
            dragTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    // Reduce Daur's velocity over a period of time.
                    drag(direction);
                }
                // Note that this increases the time each iteration, so that the drag effect occurs over a large interval.
            }, 0.08f + 0.08f * i);
        dragTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Freezes Daur so that his velocity is zero.
                freeze();
            }
            // Note that this increases the time each iteration, so that the drag effect occurs over a large interval.
        }, 0.4f);
        dragTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Gets rid of the stun after a sufficient amount of time (when Daur's velocity is zero).
                unStun();
            }
        }, 0.5f);
        // Starts the drag timer.
        dragTimer.start();
    }

    // Decreases Daur's velocity while in the water in accordance with his direction.
    private void drag(int direction) {
        switch (direction) {
            // Note that the signage of the float that is being summed is the OPPOSITE of the previous method.
            case 0:
                vel.x /= 1.2f;
                break;
            case 1:
                vel.x /= 1.2f;
                break;
            case 2:
                vel.y /= 1.2f;
                break;
            case 3:
                vel.y /= 1.2f;
                break;
            case 4:
                vel.x /= 1.2f;
                vel.y /= 1.2f;
                break;
            case 5:
                vel.x /= 1.2f;
                vel.y /= 1.2f;
                break;
            case 6:
                vel.x /= 1.2f;
                vel.y /= 1.2f;
                break;
            case 7:
                vel.x /= 1.2f;
                vel.y /= 1.2f;
                break;
        }
    }

    // Checks if the player is holding the necessary key to continue channeling the current spell.
    private void checkChanneling() {
        // For the shield.
        if (shielding)
            // If Daur is not holding the shield button, but is shielding, stops shielding and resets the spell cooldown.
            if (!(Gdx.input.isKeyPressed(storage.slotOne) && storage.item1 instanceof ShieldItem) &&
                    !(Gdx.input.isKeyPressed(storage.slotTwo) && storage.item2 instanceof ShieldItem) &&
                    !(Gdx.input.isKeyPressed(storage.slotThree) && storage.item3 instanceof ShieldItem)) {
                shielding = false;
                screen.effects.remove(shield);
                coolDown(1);
                setState(IDLE, true);
            }
    }

    // Stops all channeling. This occurs due to death primarily.
    private void stopChanneling() {
        // If shielding, stops shielding.
        if (shielding) {
            shielding = false;
            screen.effects.remove(shield);
            coolDown(1);
            if (state == CASTING)
                setState(IDLE, true);
        }
    }

    // Ends the jump of Daur.
    private void endJump() {
        // If not jumping, return.
        if (state != JUMPING && state != JUMPATTACKING)
            return;
        // Else continues.
        grounded = true;
        setState(IDLE, true);
        // Sets Y to shadow's Y, and zeroes both velocities.
        setY(shadow.getY());
        jumpVelocity = 0;
        SVY(0);
        // Removes shadow.
        screen.effects.remove(shadow);
        // Checks to see if Daur is in any new tile or a platform.
        detectPlatform();
        detectConditions();
    }

    // Resets all events. This occurs if Daur dies or exits a portal.
    private void resetEvents() {
        storage.FDstorage.resetEvents();
    }

    //Creates all the animations of Daur with their corresponding frames. Also, animations are based on direction.
    protected void createAnimations() {
        if (dir == UP) {
            idle = new Animation(0.25f, FU1, FU2);
            run = new Animation(0.25f, FU1, FU2);
            push = new Animation(0.25f, PU1, PU2);
            cast = new Animation(0.5f, PU1);
            attack = new Animation(0.15f, AU1, AU2, AU2, AU2);
            jump = new Animation(0.1f, JU1, JU2, JU3, FU2, FU2, FU2);
            // Swim animation differs based on whether Daur is moving.
            if (vel.x == 0 && vel.y == 0)
                swim = new Animation(0.5f, SU1);
            else
                swim = new Animation(0.5f, SU2);
        } else if (dir == DOWN) {
            idle = new Animation(0.25f, FD1, FD2);
            run = new Animation(0.25f, FD1, FD2);
            push = new Animation(0.25f, PD1, PD2);
            cast = new Animation(1, PD1);
            attack = new Animation(0.15f, AD1, AD2, AD2, AD2);
            jump = new Animation(0.1f, JD1, JD2, JD3, FD2, FD2, FD2);
            if (vel.x == 0 && vel.y == 0)
                swim = new Animation(0.5f, SD1);
            else
                swim = new Animation(0.5f, SD2);
        } else if (dir == RIGHT) {
            idle = new Animation(0.25f, FR1, FR2);
            run = new Animation(0.25f, FR1, FR2);
            push = new Animation(0.25f, PR1, PR2);
            cast = new Animation(1, PR1);
            attack = new Animation(0.15f, AR1, AR2, AR2, AR2);
            jump = new Animation(0.1f, JR1, JR2, JR3, FR2, FR2, FR2);
            if (vel.x == 0 && vel.y == 0)
                swim = new Animation(0.5f, SR1);
            else
                swim = new Animation(0.5f, SR2);
        } else if (dir == LEFT) {
            idle = new Animation(0.25f, FL1, FL2);
            run = new Animation(0.25f, FL1, FL2);
            push = new Animation(0.25f, PL1, PL2);
            cast = new Animation(1, PL1);
            attack = new Animation(0.15f, AL1, AL2, AL2, AL2);
            jump = new Animation(0.1f, JL1, JL2, JL3, FL2, FL2, FL2);
            if (vel.x == 0 && vel.y == 0)
                swim = new Animation(0.5f, SL1);
            else
                swim = new Animation(0.5f, SL2);
        }
        falling = new Animation(0.33333f, F1, F2, F3);
        drowning = new Animation(1, DR);
        itemAQ = new Animation(1, IA);
        knockout = new Animation(1, KO);
    }

    // This method periodically sets the frame of Daur dependent on both the state and the animationTime.
    protected void chooseSprite() {
        Animation anim = run;

        // Sets the animation of Daur depending on state.
        if (state == IDLE)
            anim = idle;
        if (state == RUNNING)
            anim = run;
        if (state == PUSHING)
            anim = push;
        if (state == CASTING)
            anim = cast;
        if (state == ATTACKING || state == JUMPATTACKING)
            anim = attack;
        if (state == JUMPING)
            anim = jump;
        if (state == SWIMMING)
            anim = swim;
        if (state == FALLING)
            anim = falling;
        if (state == DROWNING)
            anim = drowning;
        if (state == ITEMAQ)
            anim = itemAQ;
        if (state == KNOCKOUT || state == DEAD)
            anim = knockout;

        // Sets the frame of Daur depending on how much time has passed since Daur has received a new animation.
        setRegion(anim.getKeyFrame(animationTime, true));
        // Sets size of the Daur sprite depending on the size of the current frame * 7/8.
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 7 / 8, anim.getKeyFrame(animationTime, true).getRegionHeight() * 7 / 8);
    }

    // Kills and removes the Daur instance from the game for a short time. Also stuns Daur to ensure the player does not
    // move him while he is dead.
    public void death() {
        if (dying)
            return;
        // Forces idle state and then stuns/freezes Daur.
        forceState(IDLE);
        stun();
        freeze();
        // Makes Daur face down.
        setDirection(-2);
        // Sets dying to true.
        dying = true;
        // Dying animation.
        selfDestruct();
        // Removes the UI from the rendering list temporarily.
        screen.UIS.removeValue(blueBar, false);
        screen.UIS.removeValue(blueBar.bar, false);
        for (Heart heart : hearts)
            screen.UIS.removeValue(heart, false);
        // Causes defeat mask to fade in.
        screen.defeatMask(true);
        // Changes Daur's current frame to the death frame.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(DEAD, true);
                chooseSprite();
            }
        }, 1.25f);
        // Sets the screen to the death screen.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.defeat();
                screen.defeatMask(false);
            }
        }, 1.75f);
    }

    // Same as the bosses methods.
    private void selfDestruct() {
        for (float time = 0.1f; time <= 2; time += 0.2f)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    destructing = true;
                }
            }, time);
        for (float time = 0.2f; time <= 2; time += 0.2f)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    destructing = false;
                }
            }, time);
    }

    // Respawns Daur by putting him in the most recent respawn point
    public void respawn() {
        // Sets state to IDLE, and faces up.
        forceState(IDLE);
        setDirection(2);
        // Readds the UI to the rendering list.
        screen.UIS.add(blueBar.bar);
        screen.UIS.add(blueBar);
        for (Heart heart : hearts)
            screen.UIS.add(heart);
        // Resets all triggers and sets dying to false.
        resetEvents();
        // Resets all objects in the current world.
        if (screen.getWorld(map) instanceof UpperWorld)
            screen.setTileMap(0);
        else if (screen.getWorld(map) instanceof UnderWorld)
            screen.setTileMap(1);
        else
            screen.setTileMap(2);
        dying = false;
        // Restores six hearts, and moves self to the respawn point.
        health = 6;
        storage.setHealth(health);
        updateHearts();
        setPosition(respawnPoint.x, respawnPoint.y);
        screen.setCameraFast(2);
        // Transitions screen. After one second, unstuns Daur.
        screen.transition(Color.BLACK);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
                destructing = false;
            }
        }, 0.25f);
    }

    // Updates the time based on how much time has passed since the game has been previously updated.
    protected void updateTime(float deltaTime) {
        // Updates the animation time if not frozen.
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        // Updates block time if pushing.
        if (state == PUSHING)
            blockTime += deltaTime;
        else
            blockTime = 0;
        // Updates shield time if shielding.
        if (shielding) {
            // Updates the time and the corresponding alpha of the shield.
            shieldTime += deltaTime;
            shield.setAlpha(1 - shieldTime / 5);
            // If Daur has been continuously shielding for more than 3 seconds, stops the shield, and sets his spells and
            // cooldown.
            if (shieldTime > 3) {
                shielding = false;
                screen.effects.remove(shield);
                coolDown(1);
                setState(IDLE, true);
            }
        }
        else
            shieldTime = 0;
    }

    // Sets the state depending ona  few conditions.
    protected void setState(int cState, boolean override) {
        // If the state is not set to override mode, the current state must not be required to be overriden. If the state
        // is set to override mode the state must also have first priority over the current one.
        if (cState == state || (!override && overrideCheck()) || (override && priorities(cState)))
            return;

        // If all checks are passed, the current state is set to the new one.
        state = cState;
        if (state != IDLE)
            animationTime = 0;
    }

    // If the Daur's current state is ANY of the following, the new state will not be set as the current one.
    protected boolean overrideCheck() {
        return (state == ATTACKING || state == SWIMMING || state == FALLING || state == DROWNING || state == ITEMAQ ||
                state == CASTING || state == KNOCKOUT || state == DEAD || state == JUMPING || state == JUMPATTACKING);
    }

    // Overrides the current state if necessary based on a matter of priorities.
    protected boolean priorities(int cState) {
        switch (cState) {
            // If Daur wants to go to casting state, but is jumping, the state change is denied.
            case CASTING:
                if (state == JUMPING)
                    return true;
                break;
        }
        return false;
    }

    // Forces the state (does NOT check). Swimming is the sole exception.
    public void forceState(int cState) {
        // If swimming and the game is trying to make Daur go idle, ensures that Daur continues to swim.
        if (cState == IDLE && swimming)
            state = SWIMMING;
        else
            state = cState;
    }

    // Sets the spawn point.
    public void setSpawnPoint(float x, float y) {
        spawnPoint = new Point((int) x, (int) y);
    }

    // Sets the respawn point for Daur.
    public void setRespawnPoint() {
        respawnPoint = new Point((int) getX(), (int) getY());
        storage.setRespawnPoint(respawnPoint.x, respawnPoint.y);
    }

    // Sets whether Daur is transitioning.
    public void setTransitioning(boolean transitioning) {
        this.transitioning = transitioning;
    }

    //Subtracts health from Daur.
    public void loseHealth(int h) {
        // If Daur is invulnerable or dying or transitioning, he cannot lose health so the method returns.
        if (invulnerability || dying || transitioning)
            return;
        // Otherwise, subtract the health by the damage, which is first mitigated by the armor.
        health -= (h - armor);
        // Updates the health UI.
        updateHearts();
        // Sets Daur's state to invulnerable for now.
        invulnerability = true;
        // Flickers the sprite to indicate to the user Daur is hurt if health is greater than 0.
        if (health > 0)
            flickerSprite();
        // Sets Daur to a vulnerable status after one second.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                invulnerability = false;
            }
        }, 1);

        // If Daur has no more health, kills him.
        if (health == 0)
            death();
    }

    // This makes the sprite flicker from inverted to normal over a period of one second.
    private void flickerSprite() {
        // Sets the sprite to inverted. This informs the game screen to draw the sprite with inverted colors.
        inverted = true;
        // Reverts the sprite 0.2 seconds later.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.2f);
        // Inverts the sprite 0.2 seconds later.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = true;
            }
        }, 0.4f);
        // Etc.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.6f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = true;
            }
        }, 0.8f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 1);
    }

    // This is the method responsible for hurting Daur should he stray too close to an enemy.
    private void collidesEnemy() {
        // As usual, if Daur is invulnerable, nothing occurs. If in the air, the same result is achieved.
        if (invulnerability || !grounded || transitioning)
            return;

        // Gets every character in the current screen. Note that this does NOT use the same iterator that draws Daur. This
        // is because doing so throws an exception.
        for (Character character : screen.drawnSprites)
            // If the iterated character is an enemy, does further checks.
            if (character instanceof Enemy) {
                // Casts for method purposes.
                Enemy enemy = (Enemy) character;
                // If the enemy is dead, or transparent (cannot collide), returns.
                if (enemy.isDead() || enemy.isTransparent())
                    return;
                for (float step = 0; step < getWidth() - 1; step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                        // If Daur is inside the enemy's bounding rectangle, causes damage collision to Daur.
                        if (character.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2))
                            damageCollision(enemy);
            }
    }

    // Causes damage to Daur, as well as jettisoning him away from the enemy.
    public void damageCollision(Sprite sprite) {
        // If invulnerable or falling or drowning or dying returns the method to prevent any damage.
        if (invulnerability || state == FALLING || state == DROWNING || dying)
            return;
        // If the damage collision was a result of a projectile, does not get hurt.
        if (sprite instanceof Projectile && !grounded)
            return;
        // If the damage collision was due to an enemy that is grounded, but Daur isn't, ignores damage.
        if (sprite instanceof Enemy && !grounded && ((Enemy) sprite).isGrounded())
            return;
        // Ends jump if necessary.
        endJump();
        // Gets the angle between Daur and the sprite. Note that the angle is from Daur's perspective.
        float angle = (float) Math.atan2(getY() - sprite.getY(), getX() - sprite.getX());
        // Sets the velocity of Daur to the cosine and sine of the angle, causing him to fly away from the sprite.
        vel.x = (float) (4 * Math.cos(angle));
        vel.y = (float) (4 * Math.sin(angle));
        // Stuns Daur so the player cannot interrupt the collision.
        stun();
        // Lose one health.
        loseHealth(1);
        // After 0.1 seconds, unstuns Daur and stops his movement.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Note that the unstun only occurs if Daur is NOT falling.
                if (state != FALLING && state != DROWNING) {
                    vel.x = 0;
                    vel.y = 0;
                    unStun();
                }
            }
        }, 0.1f);
    }

    // Depending on the health of Daur, fills up the hearts in the UI bar.
    private void updateHearts() {
        // Empties all hearts to begin with by setting their state to two.
        for (int i = 0; i < hearts.size(); i++)
            hearts.get(i).setState(2);

        // Since each heart has two parts, fills all hearts up till half the health value. For example, a health value
        // of eight would have four hearts.
        for (int i = 0; i < health / 2; i++)
            hearts.get(i).setState(0);

        // If the health value is an odd number, fills the last heart only half way.
        if (health % 2 == 1)
            hearts.get(health / 2).setState(1);

        storage.setHealth(health);
    }

    // Checks if Daur's bounding rectangle intersects with any other character's, and treats this as a collision if so.
    protected boolean collidesCharacter() {
        if (swimming) {
            for (Character character : screen.drawnSprites)
                for (float step = 0; step < getWidth() - 5; step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < getHeight() / 2; step2 += layer.getTileHeight() / 16)
                        if (character.getBoundingRectangle().contains(getX() + 2.5f + step, getY() + 1 + step2) && !character.equals(this) && !(character instanceof Enemy))
                            return true;
        }
        else {
            for (Character character : screen.drawnSprites)
                for (float step = 0; step < getWidth() - 2; step += layer.getTileWidth() / 16)
                    for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                        if (character.getBoundingRectangle().contains(getX() + 2 + step, getY() + 1 + step2) && !character.equals(this) && !(character instanceof Enemy))
                            return true;
        }
        return false;
    }

    // Checks for every type of interactable dialogue.
    private void checkEvents() {
        // Breaks channeling immediately.
        stopChanneling();
        // Checks all interactable dialogue.
        checkTalk();
        checkSign();
        checkBooks();
        checkChests();
        checkKeyHoles();
    }

    // Checks if Daur is past any trigger checks. If so, launches the corresponding trigger.
    private void checkTriggers() {
        com.inoculates.fatesreprise.Worlds.World world;
        // If Daur is in the overworld.
        if (screen.map == screen.world1.getMap())
            world = screen.world1;
            // If Daur is in the underword.
        else if (screen.map == screen.world2.getMap())
            world = screen.world2;
            // If Daur is in a house.
        else
            world = screen.world3;

        // Iterates over every trigger. Note that the method is different for if Daur is grounded or not.
        if (grounded)
            for (int i = 0; i < world.getTriggerSize(); i++)
                // Gets every point in the Daur sprite from left to right.
                for (float x = getX(); x <= getX() + getWidth(); x++) {
                    float y;
                    // If the trigger is to Daur's bottom, set's Daur's y point as his bottom-most point.
                    if (world.getTrigger(i).getRectangle().getY() < getY())
                        y = getY();
                        // Otherwise sets Daur's y point as his top-most point.
                    else
                        y = getHeight();
                    // If any trigger contains Daur's x point and y point, launches the trigger.
                    if (world.getTrigger(i).getRectangle().contains(x, y))
                        world.trigger(world.getTrigger(i));
                }
        else
            for (int i = 0; i < world.getTriggerSize(); i++)
                // Gets every point in the Daur sprite from left to right.
                for (float x = shadow.getX(); x <= shadow.getX() + shadow.getWidth(); x++) {
                    float y;
                    // If the trigger is to Daur's bottom, set's Daur's y point as his bottom-most point.
                    if (world.getTrigger(i).getRectangle().getY() < getY())
                        y = shadow.getY();
                        // Otherwise sets Daur's y point as his top-most point.
                    else
                        y = shadow.getHeight();
                    // If any trigger contains Daur's x point and y point, launches the trigger.
                    if (world.getTrigger(i).getRectangle().contains(x, y))
                        world.trigger(world.getTrigger(i));
                }
    }

    // Checks if Daur is currently in any portal, and transports him if so.
    private void checkPortals() {
        // If Daur is in the over world and is transported to a house.
        if (screen.map == screen.world1.getMap())
            // Iterates over every portal.
            for (int i = 0; i < screen.world1.getPortalSize(); i++)
                // Gets every point in the Daur sprite from left to right.
                for (float x = getX(); x <= getX() + getWidth(); x++) {
                    float y;
                    // If the portal is to Daur's bottom, set's Daur's y point as his bottom-most point.
                    if (screen.world1.getPortal(i, true).getY() < getY())
                        y = getY();
                        // Otherwise sets Daur's y point as his top-most point.
                    else
                        y = getHeight();
                    // If any portal contains Daur's x point and y point, transports him.
                    if (screen.world1.getPortal(i, true).contains(x, y)) {
                        // Sets the new tile map to the houses one.
                        screen.setTileMap(2);
                        // Sets Daur's map to the new houses map.
                        setMap(screen.map);

                        // Sets the new position of Daur into the portal's position.
                        setX(screen.world3.getPortal(i, false).getX());
                        setY(screen.world3.getPortal(i, false).getY());
                        // Sets the new respawn point to that same portal's position.
                        respawnPoint = new Point((int) getX(), (int) getY());
                        storage.setRespawnPoint(respawnPoint.x, respawnPoint.y);

                        // Resets all events.
                        resetEvents();
                        // Sets the mask to black to allow for a transition.
                        screen.transition(Color.BLACK);
                        // Sets the camera position quickly.
                        screen.setCameraFast(2);
                    }
                }

        // Same but from houses to overworld.
        if (screen.map == screen.world3.getMap())
            for (int i = 0; i < screen.world3.getPortalSize(); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++) {
                    float y;
                    if (screen.world3.getPortal(i, true).getY() < getY())
                        y = getY();
                    else
                        y = getHeight();
                    if (screen.world3.getPortal(i, true).contains(x, y)) {
                        screen.setTileMap(0);
                        setMap(screen.map);

                        setX(screen.world1.getPortal(i, false).getX());
                        setY(screen.world1.getPortal(i, false).getY());
                        respawnPoint = new Point((int) getX(), (int) getY());
                        storage.setRespawnPoint(respawnPoint.x, respawnPoint.y);

                        resetEvents();
                        screen.transition(Color.BLACK);
                        screen.setCameraFast(0);
                    }
                }
    }

    // Same but for underworld to overworld and vice versa.
    private void checkAccess() {
        if (screen.map == screen.world1.getMap())
            for (int i = 0; i < screen.world1.getAccessSize(); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    if (screen.world1.getAccess(i, true).contains(x, getY())) {
                        screen.setTileMap(1);
                        setMap(screen.map);

                        setX((int) (screen.world2.getAccess(i, false).getX() / layer.getTileWidth()) * layer.getTileWidth());
                        setY((int) (screen.world2.getAccess(i, false).getY() / layer.getTileHeight()) * layer.getTileHeight());
                        respawnPoint = new Point((int) getX(), (int) getY());
                        storage.setRespawnPoint(respawnPoint.x, respawnPoint.y);

                        resetEvents();
                        screen.transition(Color.BLACK);
                        screen.setCameraFast(1);
                    }

        if (screen.map == screen.world2.getMap()) {
            // Checks for exits IN ADDITION to stairs.
            // Checks for exits first.
            for (int i = 0; i < screen.world2.getAccessSize(); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    if (screen.world2.getAccess(i, true).contains(x, getY())) {
                        screen.setTileMap(0);
                        setMap(screen.map);

                        setX((int) (screen.world1.getAccess(i, false).getX() / layer.getTileWidth()) * layer.getTileWidth());
                        setY((int) (screen.world1.getAccess(i, false).getY() / layer.getTileHeight()) * layer.getTileHeight());
                        respawnPoint = new Point((int) getX(), (int) getY());
                        storage.setRespawnPoint(respawnPoint.x, respawnPoint.y);

                        resetEvents();
                        screen.transition(Color.BLACK);
                        screen.setCameraFast(0);
                    }
            // Note that this stairs portion is only for intra-world purposes. Specifically, this chunk of code refers
            // to Daur going upstairs.
            for (int i = 0; i < screen.world2.getStairsSize(); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    if (screen.world2.getStairs(i, true).contains(x, getCY())) {
                        setX((int) (screen.world2.getStairs(i, false).getX() / layer.getTileWidth()) * layer.getTileWidth());
                        setY((int) (screen.world2.getStairs(i, false).getY() / layer.getTileHeight()) * layer.getTileHeight() - layer.getTileHeight());

                        screen.transition(Color.BLACK);
                        screen.setCameraFast(0);
                        storage.setLevel(1);
                    }
            // Same but for downstairs.
            for (int i = 0; i < screen.world2.getStairsSize(); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    if (screen.world2.getStairs(i, false).contains(x, getCY())) {
                        setX((int) (screen.world2.getStairs(i, true).getX() / layer.getTileWidth()) * layer.getTileWidth());
                        setY((int) (screen.world2.getStairs(i, true).getY() / layer.getTileHeight()) * layer.getTileHeight() - layer.getTileHeight());

                        screen.transition(Color.BLACK);
                        screen.setCameraFast(0);
                        storage.setLevel(-1);
                    }

        }
    }

    // Checks if Daur is currently in a teleporter. If so, transports to the other teleporter.
    private void checkTeleporters() {
        if (screen.map == screen.world2.getMap()) {
            for (int i = 0; i < screen.world2.getTeleporterSize(); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++) {
                    // For Teleporter Ones.
                    if (screen.world2.getTeleporter(i, true).contains(x, getCY())) {
                        // Moves Daur to the corresponding teleporter.
                        setX((int) (screen.world2.getTeleporter(i, false).getX() / layer.getTileWidth()) *
                                layer.getTileWidth() + layer.getTileWidth() / 2 - getWidth() / 2);
                        setY((int) (screen.world2.getTeleporter(i, false).getY() / layer.getTileHeight()) *
                                layer.getTileHeight() - 24);

                        screen.transition(Color.WHITE);
                        screen.setCameraFast(0);
                    }
                    // For Teleporter Twos.
                    if (screen.world2.getTeleporter(i, false).contains(x, getCY())) {
                        // Moves Daur to the corresponding teleporter.
                        setX((int) (screen.world2.getTeleporter(i, true).getX() / layer.getTileWidth()) *
                                layer.getTileWidth() + layer.getTileWidth() / 2 - getWidth() / 2);
                        setY((int) (screen.world2.getTeleporter(i, true).getY() / layer.getTileHeight()) *
                                layer.getTileHeight() - 24);

                        screen.transition(Color.WHITE);
                        screen.setCameraFast(0);
                    }
                }
        }
    }

    // Checks if Daur is currently in a fog spot, meaning the area around him becomes darker.
    private void checkFog() {
        // If the screen is transitioning, returns.
        if (transitioning)
            return;

        // If in the overworld.
        if (screen.map == screen.world1.getMap()) {
            // Gets all fog objects.
            for (int i = 0; i < screen.world1.getFogSize(true); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    for (float y = getY(); y <= getY() + getHeight(); y++)
                        // If the fog object contains Daur, fogs the area around Daur, making it darker.
                        if (screen.world1.getFog(i, true).contains(x, y)) {
                            screen.mask.setColor(Color.BLACK);
                            // Sets the alpha in accordance with the current fog amount.
                            screen.mask.setAlpha(screen.world1.getFogAmount(i));
                        }

            // Gets all fog OUT objects.
            for (int i = 0; i < screen.world1.getFogSize(false); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    for (float y = getY(); y <= getY() + getHeight(); y++)
                        // If the fog out object contains Daur, erases all fog from the area around Daur.
                        if (screen.world1.getFog(i, false).contains(x, y))
                            screen.mask.setAlpha(0);
        }

        // Same but for the underworld and houses tile map.
        if (screen.map == screen.world2.getMap()) {
            for (int i = 0; i < screen.world2.getFogSize(true); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    for (float y = getY(); y <= getY() + getHeight(); y++)
                        if (screen.world2.getFog(i, true).contains(x, y)) {
                            screen.mask.setColor(Color.BLACK);
                            screen.mask.setAlpha(screen.world2.getFogAmount(i));
                        }

            for (int i = 0; i < screen.world2.getFogSize(false); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    for (float y = getY(); y <= getY() + getHeight(); y++)
                        if (screen.world2.getFog(i, false).contains(x, y)) {
                            screen.mask.setAlpha(0);
                        }
        }

        if (screen.map == screen.world3.getMap()) {
            for (int i = 0; i < screen.world3.getFogSize(true); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    for (float y = getY(); y <= getY() + getHeight(); y++)
                        if (screen.world3.getFog(i, true).contains(x, y)) {
                            screen.mask.setColor(Color.BLACK);
                            screen.mask.setAlpha(screen.world3.getFogAmount(i));
                        }

            for (int i = 0; i < screen.world3.getFogSize(false); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    for (float y = getY(); y <= getY() + getHeight(); y++)
                        if (screen.world3.getFog(i, false).contains(x, y)) {
                            screen.mask.setAlpha(0);
                        }
        }
    }

    // Checks if Daur is initiating dialogue with another character.
    private void checkTalk() {
        // Event that causes the dialogue.
        Event event;

        // Checks every character in an iterator.
        for (Character character : screen.drawnSprites) {
            // If character is of the villager class.
            if (character instanceof Villager) {
                // Casts character.
                Villager villager = (Villager) character;
                // If Daur is both facing the character, and is one tile below him, initiates the dialogue.
                if (facingObject(villager) && inRange(villager))
                    event = new VillagerEvents(map, screen, storage, villager);
            }
            // Checks if the character is the fairy queen.
            if (character instanceof FairyQueen && facingObject(character) && inRange(character))
                event = new FairyEvent(map, screen, storage);

            // Checks if the character is the first sage.
            if (character instanceof Druni && facingObject(character) && inRange(character))
                event = new DruniMeeting(map, screen, (Druni) character);
        }

        // Checks if Daur is trying to talk to a character over the counter.
        for (Rectangle counter : screen.world3.counters)
            // If the counter rectangle contains Daur.
            if (counter.contains(getX() + getWidth() / 2, getY() + layer.getTileHeight()) && dir == 2) {
                switch (screen.world3.counters.indexOf(counter)) {
                    // Depending on the type of counter, initiates a dialogue for the corresponding character.
                    case 0:
                        for (Character character : screen.drawnSprites) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 20)
                                    event = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 1:
                        for (Character character : screen.drawnSprites) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 21)
                                    event = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 2:
                        for (Character character : screen.drawnSprites) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 22)
                                    event = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 3:
                        for (Character character : screen.drawnSprites) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 23)
                                    event = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 4:
                        for (Character character : screen.drawnSprites) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 24)
                                    event = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                }
            }
    }

    // This method fires when Daur is knocked down by something (This could be through death or incapacitation).
    public void knockOut() {
        // Sets state to knockout, then freezes and stuns Daur (to prevent movement).
        setState(KNOCKOUT, true);
        freeze();
        stun();
        stopChanneling();

        // Unstuns after 0.75 seconds of being KO'd.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(IDLE, true);
                unStun();
            }
        }, 0.75f);
    }

    // Same as some of the previous methods but for signs.
    private void checkSign() {
        SignEvents events;
        int value = -1;
        float tileDX = (int) ((getX() + getWidth() / 2) / layer.getTileWidth()) * layer.getTileWidth();
        float tileDY = (int) (getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight();

        TiledMapTileLayer.Cell cell = layer.getCell((int) (tileDX / layer.getTileWidth()), (int) (tileDY / layer.getTileHeight()));

        if (map == screen.world1.getMap())
            for (MapObject object : map.getLayers().get("Signs").getObjects())
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                    float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();

                    // Sets the value of the sign, which tells the game which dialogue to use for the sign.
                    if (x == tileDX && y == tileDY)
                        value = Integer.parseInt(rectObject.getProperties().get("sign").toString());
                }

        // If there is a sign existing, creates a dialogue based on the value obtained.
        if (value != -1 && dir == UP)
            events = new SignEvents(map, screen, value);
    }

    // Nearly the same method as the last, except for books.
    private void checkBooks() {
        BookEvents events;
        int value = -1;
        float tileDX = (int) ((getX() + getWidth() / 2) / layer.getTileWidth()) * layer.getTileWidth();
        float tileDY = (int) (getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight();

        TiledMapTileLayer.Cell cell = layer.getCell((int) (tileDX / layer.getTileWidth()), (int) (tileDY / layer.getTileHeight()));

        if (map == screen.world3.getMap())
            for (MapObject object : map.getLayers().get("Books").getObjects())
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                    float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();

                    if (x == tileDX && y == tileDY)
                        value = Integer.parseInt(rectObject.getProperties().get("book").toString());
                }

        if (value != -1 && dir == UP)
            events = new BookEvents(map, screen, value);
    }

    // Checks the item in the current slot, and executes a method based on that item.
    public void checkItem(int input) {
        // If Daur is stunned, he is unable to use any items.
        if (stun)
            return;
        Item item = null;
        // Sets the item depending on the input button clicked (whether the user pressed the 1st item button, 2nd, or
        // 3rd.
        if (input == storage.slotOne)
            item = storage.item1;
        else if (input == storage.slotTwo)
            item = storage.item2;
        else if (input == storage.slotThree)
            item = storage.item3;

        // If the user has used a sword item.
        if (item instanceof BasicSwordItem && !attacking)
            swordAttack();
        // Makes sure Daur cannot cast spells during cooldown.
        if (spellCooldown)
            return;
        // If the user has cast a spell.
        castSpell(item);
    }

    // Checks for any interactions with chests.
    private void checkChests() {
        // Checks every character in an iterator.
        for (Interactable interactable : screen.interactables) {
            // If character is of the villager class.
            if (interactable instanceof Chest) {
                // Casts character.
                Chest chest = (Chest) interactable;
                // If Daur is both facing the character, and is one tile below him, initiates the dialogue.
                if (facingObject(chest) && inRange(chest))
                    chest.open();
            }
        }
    }

    // Similar to the book and sign methods.
    private void checkKeyHoles() {
        Event keyHoleEvent;
        int value = -1;
        float tileDX = (int) ((getX() + getWidth() / 2) / layer.getTileWidth()) * layer.getTileWidth();
        float tileDY = (int) (getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight();

        TiledMapTileLayer.Cell cell = layer.getCell((int) (tileDX / layer.getTileWidth()), (int) (tileDY / layer.getTileHeight()));

        if (map == screen.world1.getMap())
            for (MapObject object : map.getLayers().get("Interactables").getObjects())
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                    float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();

                    if (x == tileDX && y == tileDY)
                        value = Integer.parseInt(rectObject.getProperties().get("keyhole").toString());
                }

        // Launches the keyhole event based on the value of the keyhole rectangle map object.
        if (value != -1 && dir == UP) {
            switch (value) {
                // Great Hollow Keyhole. Launches Great Hollow Opening event.
                case 0:
                    for (Item item : storage.questItems)
                        if (item instanceof GreatHollowKey) {
                            // Removes key and starts event.
                            keyHoleEvent = new GreatHollowOpening(screen.map, screen, screen.storage);
                            storage.questItems.remove(item);
                            // Advances story; sets main quest to 2.
                            storage.setMainQuestStage();
                            return;
                        }
                    break;
            }
        }
    }

    private void castSpell(Item item) {
        // If not on the ground, returns.
        if (!grounded)
            return;
        // Checks what spell is being used based on the item. In this instance, Daur is casting concussive shot.
        if (item instanceof ConcussiveShotItem)
            // Launches the appropriate method.
            launchShot();
        else if (item instanceof ShieldItem)
            shield();
        else if (item instanceof WindSickleItem)
            windSickles();
        else if (item instanceof ZephyrsWispItem)
            jump();
        // Note that if the item is a minor health potion, returns to avoid setting state to casting.
        else if (item instanceof MinorHealthPotionItem) {
            restoreHealth();
            return;
        }
        else
            return;

        // Sets the state to casting.
        setState(CASTING, true);
    }

    // Method responsible for the sword attack for Daur.
    private void swordAttack() {
        // Sets Daur's state to attacking if grounded.
        if (grounded) {
            setState(ATTACKING, true);
            // Makes Daur immobile and uninteractable for a while if grounded.
            vel.y = 0;
            vel.x = 0;
            stun();
        }
        // Otherwise state is set to jump attacking.
        else
            setState(JUMPATTACKING, true);
        // Breaks all channeling.
        stopChanneling();
        // Creates the actual sword itself on the screen.
        spawnSword();
        // Informs the game Daur is attacking.
        attacking = true;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // After 0.16875 seconds, sets Daur's state idle, unstuns him, informs the game he is no longer attacking
                // and removes the sword. The state changes only if Daur is still attacking.
                if (state == ATTACKING) {
                    setState(IDLE, true);
                    unStun();
                }
                // If jump attacking, sets state to jumping.
                if (state == JUMPATTACKING)
                    setState(JUMPING, true);
                // Sets attacking to false.
                attacking = false;
            }
        }, 0.16875f);
    }

    // Creates the sword, setting its direction based on Daur's direction.
    private void spawnSword() {
        int direction = -1;
        switch (dir) {
            // Sets the sword's direction (0 being to the top, 1 to the bottom, 2 to the right, and 3 to the bottom.
            case 2:
                direction = 0;
                break;
            case -2:
                direction = 1;
                break;
            case 1:
                direction = 2;
                break;
            case -1:
                direction = 3;
                break;
        }
        // Creates the sword itself with the direction.
        sword = new BasicSword(screen, map, atlas, this, direction);
    }

    // Launches the concussive shot.
    private void launchShot() {
        // Causes Daur's spells to go on cooldown, renders him immobile, and stuns him.
        vel.x = 0;
        vel.y = 0;
        stun();
        coolDown(1.2f);

        com.inoculates.fatesreprise.Spells.ConcussiveShot cShot = null;
        // Sets the concussive shot's direction depending on Daur's direction.
        if (dir == RIGHT)
            cShot = new com.inoculates.fatesreprise.Spells.ConcussiveShot(screen, map, screen.daurAtlases.get(4), this, 0);
        if (dir == UP)
            cShot = new com.inoculates.fatesreprise.Spells.ConcussiveShot(screen, map, screen.daurAtlases.get(4), this, 1);
        if (dir == LEFT)
            cShot = new com.inoculates.fatesreprise.Spells.ConcussiveShot(screen, map, screen.daurAtlases.get(4), this, 2);
        if (dir == DOWN)
            cShot = new com.inoculates.fatesreprise.Spells.ConcussiveShot(screen, map, screen.daurAtlases.get(4), this, 3);
        // Offsets the concussive shot by a certain amount depending also on direction.
        cShot.setInitialPosition(dir);

        // Unstuns Daur after 0.5 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(IDLE, true);
                unStun();
            }
        }, 0.5f);
    }

    // Creates a shield that prevents enemies from getting too close and shields Daur from all projectiles.
    private void shield() {
        shielding = true;
        shield = new Shield(screen, map, screen.daurAtlases.get(3), this);
        screen.effects.add(shield);
    }

    // Launches two wind sickles which converge on one point, slicing anything in their paths.
    private void windSickles() {
        vel.x = 0;
        vel.y = 0;
        stun();
        coolDown(1.75f);

        WindSickle sickle = new WindSickle(screen, map, screen.daurAtlases.get(4), this, dir, true);
        screen.spells.add(sickle);
        WindSickle sickle2 = new WindSickle(screen, map, screen.daurAtlases.get(4), this, dir, false);
        screen.spells.add(sickle2);

        // Unstuns Daur after 0.5 seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(IDLE, true);
                unStun();
            }
        }, 0.5f);
    }

    // Jumps, avoiding collision with grounded enemies, as well as any holes or deep water for 0.85 seconds.
    private void jump() {
        // If already in the air, cannot go in the air again.
        if (!grounded)
            return;
        // Breaks channeling.
        stopChanneling();
        // Starts cooldown.
        coolDown(0.2f);
        // Sets state to jumping.
        setState(JUMPING, true);
        // Sets grounded to false to avoid any collisions with enemies or holes.
        grounded = false;
        // Sets Daur's velocity upwards and acceleration downwards so it appears as though he is jumping.
        jumpVelocity = 2;
        // Adds the shadow below Daur, so that it appears as though he is in the air.
        shadow.setPosition(getCX() - shadow.getWidth() / 2, getY());
        screen.effects.add(shadow);
    }

    // Restores six health (three hearts).
    private void restoreHealth() {
        // Increases health by six and updates accordingly.
        fillHearts(6);
        // Removes the potion from the game, as it is consumed.
        if (storage.item1 instanceof MinorHealthPotionItem)
            storage.setItem1(null);
        if (storage.item2 instanceof MinorHealthPotionItem)
            storage.setItem2(null);
        if (storage.item3 instanceof MinorHealthPotionItem)
            storage.setItem3(null);
    }

    // Sets the cooldown, and then resets it after a given amount of seconds.
    private void coolDown(final float delay) {
        spellCooldown = true;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                refresh();
            }
        }, delay);
    }

    // This method is purely for the purpose of running concurrent threads (Timer).
    private void refresh() {
        spellCooldown = false;
    }

    // Returns true if Daur has swim gear; otherwise, returns false.
    private boolean canSwim() {
        for (Item item : storage.questItems)
            if (item instanceof SwimGear)
                return true;
        return false;
    }

    // Overrides super method to prevent unstunning while knocked out.
    public void unStun() {
        if (state != KNOCKOUT && !dying)
            stun = false;
    }

    // Checks if Daur is actually facing the object he is interacting with properly.
    protected boolean facingObject(Sprite object) {
        // Gets the center of Daur.
        float posX = getX() + getWidth() / 2, posY = getY() + getHeight() / 2;
        // Boolean that basically states that Daur is between the left and right of the interacted object.
        boolean inBoundsX = posX > object.getX() && posX < object.getX() + object.getWidth(),
                // Same but for top and bottom.
                inBoundsY = posY > object.getY() && posY < object.getY() + object.getHeight();
        // Basically returns if Daur is facing the object.
        return (dir == RIGHT && object.getX() > getX() && inBoundsY) || (dir == LEFT && getX() > object.getX() && inBoundsY) ||
                (dir == UP && object.getY() > getY() && inBoundsX) || (dir == DOWN && getY() > object.getY() && inBoundsX);
    }

    // Checks if Daur is within a certain range of the object.
    protected boolean inRange(Sprite object) {
        // Gets the distance between the object and Daur.
        float dX = Math.abs(object.getX() - getX()), dX2 = Math.abs(object.getX() + object.getWidth() - getX() - getWidth());
        float dY = Math.abs(object.getY() - getY()), dY2 = Math.abs(object.getY() + object.getHeight() - getY() - getHeight());
        // Gets the center of Daur.
        float posX = getX() + getWidth() / 2, posY = getY() + getHeight() / 2;
        // If Daur is to the left of the object, to the right, to the bottom, or the top.
        boolean isLeft = getX() < object.getX(), isRight = getX() > object.getX(), isDown = getY() < object.getY(),
                isUp = getY() > object.getY();
        // Same as the previous method.
        boolean inBoundsX = posX > object.getX() && posX < object.getX() + object.getWidth(),
                inBoundsY = posY > object.getY() && posY < object.getY() + object.getHeight();

        // Checks if Daur is within a close distance from the character.
        return ((dX < getWidth() && isLeft && inBoundsY) || (dX2 < getWidth() && isRight && inBoundsY) || (dY < getHeight() &&
                isDown && inBoundsX) || (dY2 < getHeight() && isUp && inBoundsX));
    }

    public boolean isGrounded() {
        return grounded;
    }

    public boolean isDead() {
        return dying;
    }

    private void resetPressed() {
        talkPressed = false;
    }

}
