
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Projectiles.FireBall;
import com.inoculates.fatesreprise.Projectiles.MagicBullet;
import com.inoculates.fatesreprise.Projectiles.MagicSphere;
import com.inoculates.fatesreprise.Projectiles.WhirlingSpheres;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Spells.Spell;

import java.util.ArrayList;

// This is the master wizard boss, who can shoot three types of spells at Daur, each with different effects..
public class MasterWizard extends Enemy {
    // The state where the wizard is casting his bolt.
    protected static final int CASTING = 6;
    private boolean damaged = false;
    private Animation spell;
    private ArrayList<Rectangle> tpSpots = new ArrayList<Rectangle>();

    TextureAtlas.AtlasRegion FU = atlas.findRegion("masterwizardU"), FSU1 = atlas.findRegion("masterwizardUD1"),
            FSU2 = atlas.findRegion("masterwizardUD2"), FD = atlas.findRegion("masterwizardD"),
            FSD1 = atlas.findRegion("masterwizardSD1"), FSD2 = atlas.findRegion("masterwizardSD2");

    float checkTime = 0;

    public MasterWizard(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 12);
        setState(IDLE, false);
        setAlpha(0);
        invulnerability = true;
        transparent = true;
        dir = DOWN;
        createAnimations();
        chooseSprite();
        addSpots();
    }

    // Adds all the teleportation spots for fighting purposes.
    private void addSpots() {
        for (MapObject object : screen.map.getLayers().get("Markers").getObjects())
            if (object instanceof RectangleMapObject) {
                // Casts the rectangular object into a normal rectangle.
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                // Gets the bottom left of the rectangle.
                float x = (int) (rect.x / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.y / layer.getTileHeight()) * layer.getTileHeight();
                Rectangle tpRect = new Rectangle(x, y, layer.getTileWidth(), layer.getTileHeight());
                // If the rectangle map object is a Master Wizard marker, adds it to the tp spot list.
                if (object.getProperties().containsKey("MWmarker"))
                    tpSpots.add(tpRect);
            }
    }

    // Every two seconds, attacks Daur.
    protected void update(float deltaTime) {
        if (checkTime > 2) {
            // Picks a random spot to attack Daur, with a random spot.
            attack();
            checkTime = 0;
        }
    }

    protected void tryMove() {

    }

    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        if (state != CASTING && !stun)
            checkTime += deltaTime;
    }

    // Randomly selects a teleporting spot, telepots to it, and then does a random attack.
    private void attack() {
        // Gets a random number between 0 and 10. Uses this number to get a random teleport spot.
        int random = (int) (Math.random() * 11);
        Rectangle tpSpot = tpSpots.get(random);
        // Sets position to the middle of the rectangle.
        setPosition(tpSpot.x + 8 - getWidth() / 2, tpSpot.y + 8 - getHeight() / 2);

        // Phases in and then casts a random spell.
        phase(true);
        // Plays the appearance sound.
        storage.sounds.get("mysterious").play(1.0f);

        // After 1.5 seconds phases back out.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Phases the wizard out.
                phase(false);
                // Resets state to idle.
                setState(IDLE, true);
            }
        }, 1.5f);

        // Sets the state to casting.
        setState(CASTING, false);

        // Creates a second random integer and then casts a spell accordingly after 0.8 seconds.
        final int random2 = (int) (Math.random() * 10);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // If damaged returns.
                if (damaged)
                    return;
                // A 40% chance of casting spell one.
                if (random2 < 4)
                    castSpellOne();
                // A 30% chance of casting spell two.
                else if (random2 < 7)
                    castSpellTwo();
                // A 30% chance of casting spell three.
                else
                    castSpellThree();
            }
        }, 1.1f);
    }

    // Fires a slow moving sphere at Daur. If it collides with anything but Daur, splits into four smaller shards.
    private void castSpellOne() {
        // Gets angle between Daur and itself.
        final float angle = (float) Math.atan2(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2,
                screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
        // Launches a Magic Sphere using this angle, and adds it to the rendering list.
        MagicSphere sphere = new MagicSphere(screen, map, screen.daurAtlases.get(5), this, angle);
        screen.projectiles.add(sphere);
        // Plays spell launching sound.
        storage.sounds.get("electricity").play(1.0f);

    }

    // Fires a rotating spell at Daur. After a certain amount of time the spell will split into three fast-moving shards.
    private void castSpellTwo() {
        // Gets angle between Daur and itself.
        final float angle = (float) Math.atan2(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2,
                screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
        // Launches a Magic Sphere using this angle, and adds it to the rendering list.
        WhirlingSpheres spheres = new WhirlingSpheres(screen, map, screen.daurAtlases.get(5), this, angle);
        screen.projectiles.add(spheres);
        // Plays spell launching sound.
        storage.sounds.get("electricity").play(1.0f);
    }

    // Fires an extremely fast spear at Daur. Difficult to evade.
    private void castSpellThree() {
        // Gets angle between Daur and itself.
        final float angle = (float) Math.atan2(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2,
                screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
        // Launches a Magic Sphere using this angle, and adds it to the rendering list.
        MagicBullet bullet = new MagicBullet(screen, map, screen.daurAtlases.get(5), this, angle);
        screen.projectiles.add(bullet);
        // Plays spell launching sound.
        storage.sounds.get("electricity").play(1.0f);
    }

    // Depending on the boolean in, phases the wizard in and out.
    public void phase(boolean in) {
        // Note that every task resets the direction, so that the wizard tracks daur, and further increases/decreases
        // the transparency of the wizard.
        if (in) {
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(0.25f);
                    resetDirection();
                }
            }, 0.2f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(0.5f);
                    resetDirection();
                }
            }, 0.4f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(0.75f);
                    resetDirection();
                }
            }, 0.6f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(1);
                    resetDirection();
                    invulnerability = false;
                    transparent = false;
                }
            }, 0.8f);
        }
        else {
            // Unlike the wizard class, sets invulnerability to true IMMEDIATELY when phasing out.
            invulnerability = true;
            transparent = true;
            // The rest of the methods.
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (isDead()) return;
                    setAlpha(0.75f);
                }
            }, 0.2f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (isDead()) return;
                    setAlpha(0.5f);
                }
            }, 0.4f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (isDead()) return;
                    setAlpha(0.25f);
                }
            }, 0.6f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (isDead()) return;
                    setAlpha(0);
                }
            }, 0.8f);
        }
    }

    private void resetDirection() {
            if (screen.daur.getY() > getY())
                dir = UP;
            else
                dir = DOWN;
    }

    protected void createAnimations() {
        if (dir == UP) {
            idle = new Animation(0.5f, FU);
            spell = new Animation(0.8f, FSU1, FSU2);
        }
        else if (dir == DOWN) {
            idle = new Animation(0.5f, FD);
            spell = new Animation(0.8f, FSD1, FSD2);
        }
    }

    // If the boss is hit, does NOT attack and simply phases out.
    public void damageCollision(Sprite sprite, int dmg) {
        if (armor >= dmg || invulnerability || isDead())
            return;
        // Does not get affected by spells.
        if (sprite instanceof Spell)
            return;
        // Takes damage.
        loseHealth(dmg);
        // Phases out.
        phase(false);
        // Resets state to idle.
        setState(IDLE, true);
        // Sets damaged to true to prevent any delayed attacks or phases.
        damaged = true;
        // After 1.2 seconds, resets damaged.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                damaged = false;
            }
        }, 1.2f);
    }

    // Overrides method to prevent removal of invulnerability.
    public void loseHealth(int h) {
        if (!invulnerability) {
            health -= (h - armor);
            flickerSprite();
            // Only sets INVERTED to false.
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    inverted = false;
                }
            }, 0.6f);
            if (health == 0)
                death();
            // If hurt and not dead, plays boss hurt sound.
            else
                storage.sounds.get("bosshurt").play(0.75f);
        }
    }

    // Overrides death animation. Opens the door to the sage.
    protected void death() {
        freeze();
        stun();

        final Enemy enemy = this;
        if (isDead())
            return;
        setState(DEAD, true);
        // Makes the Master Wizard slightly transparent.
        setAlpha(0.5f);
        // Plays death sound.
        storage.sounds.get("bossdeath").play(1.0f);
        // After two seconds performs the death animation.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                selfDestruct();
            }
        }, 2);
        // Clears self away after four seconds.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Checks if this enemy a part of a triggering event.
                screen.checkClear(enemy);
                // Plays explosion sound.
                storage.sounds.get("boom1").play(1.0f);
                // Removes self from game.
                removeSelf();
            }
        }, 4);
    }

    // Same as the King Slime method.
    private void selfDestruct() {
        for (float time = 0.1f; time <= 2; time += 0.2f)
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    destructing = true;
                    // Mini explosion sound.
                    storage.sounds.get("bossminiexplosion").play(1.0f);
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

    // Overrides the stun collision method to prevent stunning.
    public void stunCollision(Sprite sprite, float time) {

    }

    protected boolean overrideCheck() {
        return (state == CASTING || state == DEAD);
    }
    //Overrides the current state if necessary.
    protected boolean priorities(int cState)
    {
        return state == DEAD;
    }

    //This method periodically sets the frame of the pixelknight dependent on both the state and the animationTime.
    protected void chooseSprite()
    {
        Animation anim = idle;

        if (state == IDLE || state == DEAD)
            anim = idle;
        if (state == FALLING)
            anim = fall;
        if (state == DROWNING)
            anim = drown;
        if (state == CASTING)
            anim = spell;

        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }
}
