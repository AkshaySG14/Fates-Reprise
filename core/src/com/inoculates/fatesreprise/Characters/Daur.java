
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
import com.inoculates.fatesreprise.Effects.Grass;
import com.inoculates.fatesreprise.Effects.Ripple;
import com.inoculates.fatesreprise.Events.*;
import com.inoculates.fatesreprise.Interactables.Block;
import com.inoculates.fatesreprise.Interactables.Interactable;
import com.inoculates.fatesreprise.Items.BasicSwordItem;
import com.inoculates.fatesreprise.Items.ConcussiveShotItem;
import com.inoculates.fatesreprise.Items.Item;
import com.inoculates.fatesreprise.MeleeWeapons.BasicSword;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.UI.RedBarUI;
import com.inoculates.fatesreprise.UI.Heart;
import com.inoculates.fatesreprise.UI.UI;

import java.awt.*;
import java.util.ArrayList;

//Daur class responsible for every action of the main character, Daur. The most complicated character class.
public class Daur extends Character {
    //All of these static integers represent the various states Daur goes through.
    //Each state Daur is set to will cause Daur to have a different animation.
    private final int ATTACKING = 2, RUNNING = 3, PUSHING = 4, JUMPING = 5, SWIMMING = 6, FALLING = 7,
            ITEMAQ = 8, CASTING = 9;

    //These two floats are involved in jumping and falling. The JUMP_VELOCITY is the starting jump velocity, while
    private final float JUMP_VELOCITY = 2.8f;

    //Animations of Daur, which as mentioned are determined by the state
    Animation idle, run, push, cast, attack, jump, swim, falling, itemAQ;

    // The collision direction of Daur, which determines the orientation of his frames and animations when he is pushing
    // on something. This is to ensure that regardless of whatever direction the player is moving Daur in, he will remain
    // pushing in the same direction.
    int collisionDir;
    // THe currency and mana of Daur respectively.
    private int coins, mana;

    // Whether Daur is currently invulnerable (cannot be harmed).
    public boolean invulnerability = false;

    // Whether Daur is slowed, on grass, in dialogue, displaying his wounded animation (flashing colors), falling down
    // a hole, attacking, his spell's on cooldown, and swimming.
    private boolean slowed = false, onGrass = false;
    private boolean talkPressed = false;
    private boolean transitioning = false;
    private boolean fallingHole = false;
    private boolean attacking = false;
    private boolean spellCooldown = false;
    private boolean swimming = false;

    // The max possible speed of Daur, and the amount of time Daur has to push on a block for it to move.
    private float maxSpeed = 0, blockTime = 0;

    // This timer creates a concurrent thread that is responsible for the slowing effect when Daur swims.
    private Timer dragTimer = new Timer();
    // The grass effect that will stick to Daur.
    private Grass grass;
    // The ripple effect that will also stick to Daur.
    private Ripple ripple;
    // The block that Daur is currently moving.
    private Block moveBlock;
    // Daur's sword, that appears when swung.
    private BasicSword sword;
    // The array list that represents the heart UI elemnt shown to the user.
    private ArrayList<Heart> hearts = new ArrayList<Heart>();
    // The respawn point of the Daur (if he dies), and the spawn point (if he falls down a hole and must be spawned).
    private Point respawnPoint, spawnPoint;

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

    TextureAtlas.AtlasRegion F1 = atlas.findRegion("falling1"), F2 = atlas.findRegion("falling2"), F3 = atlas.findRegion("falling3");

    TextureAtlas.AtlasRegion IA = atlas.findRegion("itemacquired");

    // Initializes the Daur class by setting Daur's atlas, layer, screen, map, frames, and animations. Additionally sets
    // the mana, coins, and health in case a game has been loaded. Also creates the effects and the UI for the game.
    public Daur(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, screen.storage);
        coins = storage.coins;
        health = storage.health;
        mana = storage.mana;

        // Creates effects.
        grass = new Grass(screen, map, screen.daurAtlases.get(3), this);
        ripple = new Ripple(screen, map, screen.daurAtlases.get(3), this);

        // Creates UI bar for the user to see the various UI elements.
        RedBarUI bar = new RedBarUI(screen, screen.daurAtlases.get(1));
        screen.UIS.add(bar);

        // Creates each individual heart and adds to the UI bar.
        for (int i = 0; i < storage.health / 2; i++) {
            Heart heart = new Heart(screen, screen.daurAtlases.get(1), bar, i);
            hearts.add(heart);
            screen.UIS.add(heart);
        }
        updateHearts();
    }

    // Checks for key inputs, collision of enemies, ad checks whether Daur is currently in fog.
    protected void update(float deltaTime) {
        processKeys();
        collidesEnemy();
        checkFog();
    }

    // This method moves Daur depending on the x and y components of its velocity. Additionally, it checks for any
    // collisions, and for any change in condition (like slow).
    private void tryMove() {
        checkCollisions();
        detectConditions();
    }

    // Checks whether Daur's condition has changed depending on its position.
    private void detectConditions() {
        // Checks if Daur is slowed.
        if (detectSlow()) {
            slowed = true;
            setModifier(0.35f, 0.35f);
        // If currently slowed, but not in a slow cell, removes the slow effect.
        } else if (slowed) {
            slowed = false;
            resetModifier(0.35f, 0.35f);
        }

        // Checks if any grass has been detected (if there is no grass type there is no grass).
        if (detectGrass() != 4) {
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
        if (detectShallowWater()) {
            if (!screen.effects.contains(ripple)) {
                screen.effects.add(ripple);
                ripple.setPosition(getX(), getY());
                ripple.setAnimationTime(animationTime);
            }
            // If the Daur has a ripple effect, but is not in shallow water, the game removes the effect.
        } else if (screen.effects.contains(ripple))
            screen.effects.remove(ripple);

        // If Daur is in deep enough water to swim, Daur will begin to swim, and his state will be set accordingly.
        if (detectWater()) {
            swimming = true;
            setState(SWIMMING, true);
            // Else if daur was previously swimming, ensures that Daur is no longer swimming, and allows him to move
            // freely. Also immediately sets his tate to idle.
        } else if (swimming) {
            unStun();
            swimming = false;
            setState(IDLE, true);
        }

        // If the Daur is currently in a hole, causes him to fall.
        if (detectHole() != null) {
            // Creates the boolean variable isFalling, to check if Daur is close enough to fall, or only close enough
            // to gravitate.
            boolean isFalling = false;
            // Gets the middle of the hole cell.
            Point holePoint = detectHole();
            // Distance from Daur to the middle of the hole cell.
            float distanceX = Math.abs(holePoint.x - getX() - getWidth() / 2);
            float distanceY = Math.abs(holePoint.y - getY() - getHeight() / 2);
            // If Daur is sufficiently close in both the x and y respects, causes him to fall.
            if (distanceX < getWidth() / 3 && distanceY < getHeight() / 3) {
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

    // This is what occurs if Daur were to fall down a hole.
    private void fallHole(Point hole) {
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
        stun();

        // After one second of falling, Daur's health will be reduced, he will flicker to show he has been hurt,
        // his position will be reset to the spawn point, he will be unstunned, and his state will be set to idle.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                loseHealth(1);
                flickerSprite();
                setPosition(spawnPoint.x, spawnPoint.y);
                unStun();
                setState(IDLE, true);
            }
        }, 1);
        timer.start();
    }

    // Every new frame, Daur's position is reset due to the vastly different sizes of his sprites during his falling
    // animation.
    private void resetPosition(final Point hole) {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
            }
        }, 0.000001f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
            }
        }, 0.33333333f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setPosition(hole.x - getWidth() / 2, hole.y - getHeight() / 2);
            }
        }, 0.666666666f);
        timer.start();
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
        float oldX = getX(), oldY = getY();
        // The boolean for collisions.
        boolean collisionX = false, collisionY = false;

        // Accelerates Daur accordingly.
        vel.x += ace.x;

        // Ensures that Daur does not accelerate out of control by setting a maximum speed.
        if (ace.x != 0 && Math.abs(vel.x) > maxSpeed)
            vel.x = Math.signum(vel.x) * maxSpeed;

        // Adds velocity to x value.
        setX(getX() + vel.x);

        // Detects collision and if there is one, moves Daur back. The first if statement is if Daur is moving to the
        // left and the second if to the right.
        if (vel.x < 0)
            // If Daur has collided with terrain or an interactable.
            collisionX = collidesLeft() || collidesInteractable() ||
                    // If Daur is stunned, ensures that he does not go to another cell. This is to prevent the user from
                    // feeling too confused.
                    (getX() < (storage.cellX - 1) * layer.getTileWidth() * 10 && stun);
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesInteractable() ||
                    (getX() + getWidth() > storage.cellX * layer.getTileWidth() * 10 && stun);

        // If Daur has collided in any way on the x-axis. NOTE: this is only for terrain.
        if (collisionX) {
            // Sets the collision direction of Daur depending on the x component of his velocity.
            if (vel.x > 0)
                collisionDir = 1;
            else collisionDir = -1;

            // If collision occurs, sets the x value of Daur back to the old one, and causes him to stop.
            setX(oldX);
            vel.x = 0;

            // If Daur has collided while swimming, also sets the y component of his velocity to zero, unstuns him to
            // allow movement, and clears all instances of drag.
            if (swimming) {
                dragTimer.clear();
                unStun();
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
                vel.y = 0;
            }
        }

        // Similar to the x version, but for the y component of Daur's velocity.
        vel.y += ace.y;

        if (ace.y != 0 && Math.abs(vel.y) > maxSpeed)
            vel.y = Math.signum(vel.y) * maxSpeed;

        //Adds velocity to y value.
        setY(getY() + vel.y);

        //Detects collision on the y axis.
        if (vel.y < 0)
            collisionY = collidesBottom() || collidesInteractable() ||
                    (getY() < (storage.cellY - 1) * layer.getTileHeight() * 10 && stun);

        else if (vel.y > 0)
            collisionY = collidesTop() || collidesInteractable() ||
                    (getY() + getHeight() > storage.cellY * layer.getTileHeight() * 10 - 16 && stun);

        if (collisionY) {
            if (vel.y > 0)
                collisionDir = 2;
            else collisionDir = -2;

            setY(oldY);
            vel.y = 0;

            if (swimming) {
                dragTimer.clear();
                unStun();
                setX(oldX);
                vel.x = 0;
            } else
                setState(PUSHING, false);
        }

        if (collidesCharacter()) {
            setY(oldY);
            vel.y = 0;

            if (swimming) {
                dragTimer.clear();
                unStun();
                setX(oldX);
                vel.x = 0;
            }
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
                        return true;
                    }
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
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        unStun();
                    }
                }, 0.25f);
                timer.start();
            }
        }
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
        if (vel.x != 0 || vel.y != 0) {
            tryMove();
            checkPortals();
            checkAccess();
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
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    resetPressed();
                }
            }, 0.25f);
            timer.start();
        }
    }

    // Allows Daur to swim, with a drag effect similar to Link's Awakening.
    private void swim(final int direction) {
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
        // Creates the drag timer and launches the drag timer so that Daur is incrementally slowed until he stops.
        for (int i = 0; i < 5; i++)
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
                vel.x += 0.15f;
                break;
            case 1:
                vel.x -= 0.15f;
                break;
            case 2:
                vel.y -= 0.15f;
                break;
            case 3:
                vel.y += 0.15f;
                break;
            case 4:
                vel.x += 0.15f;
                vel.y -= 0.15f;
                break;
            case 5:
                vel.x += 0.15f;
                vel.y += 0.15f;
                break;
            case 6:
                vel.x -= 0.15f;
                vel.y -= 0.15f;
                break;
            case 7:
                vel.x -= 0.15f;
                vel.y += 0.15f;
                break;
        }
    }

    //Creates all the animations of Daur with their corresponding frames. Also, animations are based on direction.
    protected void createAnimations() {
        if (dir == UP) {
            idle = new Animation(0.25f, FU1, FU2);
            run = new Animation(0.25f, FU1, FU2);
            push = new Animation(0.25f, PU1, PU2);
            cast = new Animation(0.5f, PU1);
            attack = new Animation(0.15f, AU1, AU2, AU2, AU2);
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
            if (vel.x == 0 && vel.y == 0)
                swim = new Animation(0.5f, SL1);
            else
                swim = new Animation(0.5f, SL2);
        }
        falling = new Animation(0.33333f, F1, F2, F3);
        itemAQ = new Animation(1, IA);
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
        if (state == ATTACKING)
            anim = attack;
        if (state == JUMPING)
            anim = jump;
        if (state == SWIMMING)
            anim = swim;
        if (state == FALLING)
            anim = falling;
        if (state == ITEMAQ)
            anim = itemAQ;

        // Sets the frame of Daur depending on how much time has passed since Daur has received a new animation.
        setRegion(anim.getKeyFrame(animationTime, true));
        // Sets size of the Daur sprite depending on the size of the current frame * 7/8.
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 7 / 8, anim.getKeyFrame(animationTime, true).getRegionHeight() * 7 / 8);
    }

    // Kills and removes the Daur instance from the game for a short time. Also stuns Daur to ensure the player does not
    // move him while he is dead.
    public void death() {
        if (state == DEAD)
            return;
        setState(DEAD, true);
        stun = true;
        vel.x = 0;
        vel.y = 0;
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
        return (state == ATTACKING || state == SWIMMING || state == FALLING || state == ITEMAQ || state == CASTING);
    }

    //Overrides the current state if necessary based on a matter of priorities.
    protected boolean priorities(int cState) {
        switch (cState) {

        }
        return false;
    }

    // Sets the spawn point.
    public void setSpawnPoint(float x, float y) {
        spawnPoint = new Point((int) x, (int) y);
    }

    //Subtracts health from Daur.
    public void loseHealth(int h) {
        // If Daur is invulnerable, he cannot lose health so the method returns.
        if (invulnerability)
            return;
        // Otherwise, subtract the health by the damage, which is first mitigated by the armor.
        health -= (h - armor);
        // Updates the health UI.
        updateHearts();
        // Sets Daur's state to invulnerable for now.
        invulnerability = true;
        // Flickers the sprite to indicate to the user Daur is hurt.
        flickerSprite();
        // Sets Daur to a vulnerable status after one second.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                invulnerability = false;
            }
        }, 1);
        timer.start();

        // If Daur has no more health, kills him.
        if (health == 0)
            death();
    }

    // This makes the sprite flicker from inverted to normal over a period of one second.
    private void flickerSprite() {
        // Sets the sprite to inverted. This informs the game screen to draw the sprite with inverted colors.
        inverted = true;
        // Reverts the sprite 0.2 seconds later.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.2f);
        // Inverts the sprite 0.2 seconds later.
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = true;
            }
        }, 0.4f);
        // Etc.
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.6f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = true;
            }
        }, 0.8f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 1);
        timer.start();

    }

    // This is the method responsible for hurting Daur should he stray too close to an enemy.
    private void collidesEnemy() {
        // As usual, if Daur is invulnerable, nothing occurs.
        if (invulnerability)
            return;

        // Gets every character in the current map. Note that this does NOT use the same iterator that draws Daur. This
        // is because doing so throws an exception.
        for (Character character : screen.charIterator)
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
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                vel.x = 0;
                vel.y = 0;
                unStun();
            }
        }, 0.1f);
        timer.start();
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
        for (Character character : screen.charIterator)
            for (float step = 0; step < getWidth() - 1; step += layer.getTileWidth() / 16)
                for (float step2 = 0; step2 < getHeight() - 5; step2 += layer.getTileHeight() / 16)
                    if (character.getBoundingRectangle().contains(getX() + 1 + step, getY() + 1 + step2) && !character.equals(this) && !(character instanceof Enemy))
                        return true;
        return false;
    }

    // Checks for every type of interactable dialogue.
    private void checkEvents() {
        checkTalk();
        checkSign();
        checkBooks();
        checkCuckooSign();
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
                    // If any portal contains Daur's x point and y point,
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

                        // Sets the mask to black to allow for a transition.
                        transition(Color.BLACK);
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

                        transition(Color.BLACK);
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

                        transition(Color.BLACK);
                        screen.setCameraFast(1);
                    }

        if (screen.map == screen.world2.getMap())
            for (int i = 0; i < screen.world2.getAccessSize(); i++)
                for (float x = getX(); x <= getX() + getWidth(); x++)
                    if (screen.world2.getAccess(i, true).contains(x, getY())) {
                        screen.setTileMap(0);
                        setMap(screen.map);

                        setX((int) (screen.world1.getAccess(i, false).getX() / layer.getTileWidth()) * layer.getTileWidth());
                        setY((int) (screen.world1.getAccess(i, false).getY() / layer.getTileHeight()) * layer.getTileHeight());
                        respawnPoint = new Point((int) getX(), (int) getY());

                        transition(Color.BLACK);
                        screen.setCameraFast(0);
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

    // Checks if Daur is reading the cuckoo sign. If so, the cuckoo dialogue will occur.
    private void checkCuckooSign() {
        // Event that causes the dialogue.
        CuckooSignEvent events;
        // Gets the tile IN FRONT of Daur.
        float tileDX = (int) ((getX() + getWidth() / 2) / layer.getTileWidth()) * layer.getTileWidth();
        float tileDY = (int) (getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight();

        // Gets the cell cell that holds the tile.
        TiledMapTileLayer.Cell cell = layer.getCell((int) (tileDX / layer.getTileWidth()), (int) (tileDY / layer.getTileHeight()));

        // If the cell exists and holds the key csign, and Daur is facing up, creates the dialogue box.
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("csign") && dir == UP)
            events = new CuckooSignEvent(map, screen);
    }

    // Checks if Daur is initiating dialogue with another character.
    private void checkTalk() {
        // Event that causes the dialogue.
        VillagerEvents events;

        // Checks every character in an iterator.
        for (Character character : screen.charIterator)
            // If character is of the villager class.
            if (character instanceof Villager) {
                // Casts character.
                Villager villager = (Villager) character;
                // If Daur is both facing the character, and is one tile below him, initiates the dialogue.
                if (facingCharacter(villager) && inRange(villager))
                    events = new VillagerEvents(map, screen, storage, villager);
            }

        // Checks if Daur is trying to talk to a character over the counter.
        for (Rectangle counter : screen.world3.counters)
            // If the counter rectangle contains Daur.
            if (counter.contains(getX() + getWidth() / 2, getY() + layer.getTileHeight()) && dir == 2) {
                switch (screen.world3.counters.indexOf(counter)) {
                    // Depending on the type of counter, initiates a dialogue for the corresponding character.
                    case 0:
                        for (Character character : screen.charIterator) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 20)
                                    events = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 1:
                        for (Character character : screen.charIterator) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 21)
                                    events = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 2:
                        for (Character character : screen.charIterator) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 22)
                                    events = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 3:
                        for (Character character : screen.charIterator) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 23)
                                    events = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                    case 4:
                        for (Character character : screen.charIterator) {
                            if (character instanceof Villager) {
                                Villager villager = (Villager) character;
                                if (villager.getVillager() == 24)
                                    events = new VillagerEvents(map, screen, storage, villager);
                            }
                        }
                        break;
                }
            }
    }

    // This method causes the screen to fade to the given color, then fade out once again.
    private void transition(Color color) {
        // Sets color of the mask, makes it opaque, and sets the position to the camera position.
        screen.mask.setColor(color);
        screen.mask.setAlpha(1);
        screen.mask.setPosition(screen.camera.position.x - screen.camera.viewportWidth / 2, screen.camera.position.y - screen.camera.viewportHeight / 2);

        // Tells the game that it is transitioning.
        transitioning = true;
        vel.x = 0;
        vel.y = 0;
        // Ensures that Daur does not move during the transition.
        stun();

        // Creates a timer that unstuns Daur, breaks the game out of transitioning, sets the mask to be transparent, and
        // finally renews every UI position.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
                transitioning = false;
                screen.mask.setAlpha(0);
                for (UI ui : screen.UIS)
                    ui.renewPosition();
            }
        }, 0.25f);
        timer.start();
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

    private void castSpell(Item item) {
        // Checks what spell is being used based on the item. In this instance, Daur is casting concussive shot.
        if (item instanceof ConcussiveShotItem)
            // Launches the appropriate method.
            launchShot();
        else return;

        // Sets the state to casting, stuns Daur, and renders him immobile.
        setState(CASTING, true);
        stun();
        vel.x = 0;
        vel.y = 0;

        // Unstuns Daur after 0.5 seconds.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(IDLE, true);
                unStun();
            }
        }, 0.5f);
        timer.start();
    }

    // Method responsible for the sword attack for Daur.
    private void swordAttack() {
        // Sets Daur's state to attacking.
        setState(ATTACKING, true);
        // Makes Daur immobile and uninteractable for a while.
        vel.y = 0;
        vel.x = 0;
        stun();
        // Creates the actual sword itself on the screen.
        spawnSword();
        // Informs the game Daur is attacking.
        attacking = true;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // After 0.225 seconds, sets Daur's state idle, unstuns him, infroms the game he is no longer attacking
                // and removes the sword.
                setState(IDLE, true);
                unStun();
                attacking = false;
                screen.meleeWeapons.remove(sword);
            }
        }, 0.225f);
        timer.start();
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
        // Adds the sword to the render list.
        screen.meleeWeapons.add(sword);
    }

    // Launches the concussive shot.
    private void launchShot() {
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
        // Causes Daur's spells to go on cooldown.
        coolDown();
    }

    // Sets the cooldown, and then resets it after 2 seconds.
    private void coolDown() {
        spellCooldown = true;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                refresh();
            }
        }, 2);
        timer.start();
    }

    // This method is purely for the purpose of running concurrent threads (Timer).
    private void refresh() {
        spellCooldown = false;
    }

    // Checks if Daur is actually facing the character properly.
    protected boolean facingCharacter(Character character) {
        // Gets the center of Daur.
        float posX = getX() + getWidth() / 2, posY = getY() + getHeight() / 2;
        // Boolean that basically states that Daur is between the left and right of the interacted character.
        boolean inBoundsX = posX > character.getX() && posX < character.getX() + character.getWidth(),
                // Same but for top and bottom.
                inBoundsY = posY > character.getY() && posY < character.getY() + character.getHeight();
        // Basically returns if Daur is facing the character.
        return (dir == RIGHT && character.getX() > getX() && inBoundsY) || (dir == LEFT && getX() > character.getX() && inBoundsY) ||
                (dir == UP && character.getY() > getY() && inBoundsX) || (dir == DOWN && getY() > character.getY() && inBoundsX);
    }

    // Checks if Daur is within a certain range of the character.
    protected boolean inRange(Character character) {
        // Gets the distance between the character and Daur.
        float dX = Math.abs(character.getX() - getX()), dX2 = Math.abs(character.getX() + character.getWidth() - getX() - getWidth());
        float dY = Math.abs(character.getY() - getY()), dY2 = Math.abs(character.getY() + character.getHeight() - getY() - getHeight());
        // Gets the center of Daur.
        float posX = getX() + getWidth() / 2, posY = getY() + getHeight() / 2;
        // If Daur is to the left of the character, to the right, to the bottom, or the top.
        boolean isLeft = getX() < character.getX(), isRight = getX() > character.getX(), isDown = getY() < character.getY(),
                isUp = getY() > character.getY();
        // Same as the previous method.
        boolean inBoundsX = posX > character.getX() && posX < character.getX() + character.getWidth(),
                inBoundsY = posY > character.getY() && posY < character.getY() + character.getHeight();

        // Checks if Daur is within a close distance from the character.
        return ((dX < getWidth() && isLeft && inBoundsY) || (dX2 < getWidth() && isRight && inBoundsY) || (dY < getHeight() &&
                isDown && inBoundsX) || (dY2 < getHeight() && isUp && inBoundsX));
    }

    private void resetPressed() {
        talkPressed = false;
    }

}
