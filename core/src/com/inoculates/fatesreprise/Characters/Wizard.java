
package com.inoculates.fatesreprise.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Interactables.Interactable;
import com.inoculates.fatesreprise.Projectiles.FireBall;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is an enemy that fades in and out of combat to attack Daur with a magic bolt.
public class Wizard extends Enemy {
    // The state where the wizard is casting his bolt.
    protected static final int CASTING = 6;
    private Animation spell;

    TextureAtlas.AtlasRegion FU = atlas.findRegion("wizardU"), FSU1 = atlas.findRegion("wizardSU1"),
            FSU2 = atlas.findRegion("wizardSU2"), FR = atlas.findRegion("wizardR"), FSR1 = atlas.findRegion("wizardSR1"),
            FSR2 = atlas.findRegion("wizardSR2"), FL = atlas.findRegion("wizardL"), FSL1 = atlas.findRegion("wizardSL1"),
            FSL2 = atlas.findRegion("wizardSL2"), FD = atlas.findRegion("wizardD"), FSD1 = atlas.findRegion("wizardSD1"),
            FSD2 = atlas.findRegion("wizardSD2");

    float checkTime = 0;

    public Wizard(GameScreen screen, TiledMap map, TextureAtlas atlas) {
        super(screen, map, atlas, 2);
        setState(IDLE, false);
        setAlpha(0);
        invulnerability = true;
        transparent = true;
    }

    protected void update(float deltaTime) {
        if (checkTime > 1) {
            // Calls the attack method with NO exclusions. This is to test whether the wizard can attack Daur.
            attack(new boolean[] {false, false, false, false});
            checkTime = 0;
        }
    }

    protected void tryMove() {

    }

    protected void updateTime(float deltaTime) {
        if (!frozen && state != IDLE)
            animationTime += deltaTime;
        if (state != CASTING) {
            checkTime += deltaTime;
        }
    }

    private void attack(boolean[] exclusions) {
        // Purpose of the is twofold. One to prevent the wizard from attacking if the player is out of the cell.
        // The second is to prevent the method from recursively looping infinitely when the player is out of the cell.

        if (!inRange())
            return;

        // Checks if all directions are blocked. If so, exits the method completely. This ends the recursion of this method.
        for (int i = 0; i < 4; i ++) {
            if (!exclusions[i])
                break;
            // If the loop reaches the end without breaking, that means that every direction is excluded (exclusions[0] to
            // exclusions[3] are all true).
            if (i == 3)
                return;
        }

        // Sets the base newX and newY, which will eventually be the new position for the wizard.
        float newX = getX();
        float newY = getY();

        // Gets a random integer from zero to three.
        int random = (int) (Math.random() * 4);

        // Checks to see if the direction is not already registered as blocked by the program before.
        while (exclusions[random])
            // Gets another random integer from zero to three. This is to keep the loop iterating.
            random = (int) (Math.random() * 4);

        // Sets the new x and y position for the wizard. This is dependent upon the randomly generated integer.
        switch (random) {
            case 0:
                // The wizard is placed above Daur.
                newX = screen.daur.getX() + screen.daur.getWidth() / 2 - getWidth() / 2;
                newY = screen.daur.getY() + screen.daur.getHeight() + 10;
                dir = DOWN;
                break;
            case 1:
                // The wizard is placed below Daur.
                newX = screen.daur.getX() + screen.daur.getWidth() / 2 - getWidth() / 2;
                newY = screen.daur.getY() - 10 - getHeight();
                dir = UP;
                break;
            case 2:
                // The wizard is placed to the right of Daur.
                newX = screen.daur.getX() + screen.daur.getWidth() + 10;
                newY = screen.daur.getY() + screen.daur.getHeight() / 2 - getHeight() / 2;
                dir = LEFT;
                break;
            case 3:
                // The wizard is placed to the left of Daur.
                newX = screen.daur.getX() - 10 - getWidth();
                newY = screen.daur.getY() + screen.daur.getHeight() / 2 - getHeight() / 2;
                dir = RIGHT;
                break;
        }

        // If the position is valid, sets the wizard's position as the new position. Also initiates phasing and casts.
        if (validPosition(newX, newY)) {
            setPosition(newX, newY);
            // Phases in and casts.
            phase(true);
            cast();
        }

        // Updates the exclusion array to include the new direction.
        else {
            exclusions[random] = true;
            // Recursion.
            attack(exclusions);
        }
    }

    // Fires the magic bolt at Daur.
    private void cast() {
        // Sets the object for this character. This is necessary for concurrent threading.
        final Character character = this;
        // Sets the state to casting.
        setState(CASTING, false);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Gets angle between Daur and itself.
                final float angle = (float) Math.atan2(screen.daur.getY() + screen.daur.getHeight() / 2 - getY() - getHeight() / 2,
                        screen.daur.getX() + screen.daur.getWidth() / 2 - getX() - getWidth() / 2);
                // Launches the fireball using this angle, and adds it to the rendering list.
                FireBall ball = new FireBall(screen, map, screen.daurAtlases.get(5), character, angle);
                screen.projectiles.add(ball);
                // Plays the launch sound.
                screen.storage.sounds.get("launch5").play(1.0f);
            }
        }, 0.8f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                // Phases the wizard out.
                phase(false);
                // Resets state to idle.
                setState(IDLE, true);
            }
        }, 1.2f);
        // Plays the appearance sound.
        screen.storage.sounds.get("pulse").play(1.0f);
    }

    // Depending on the boolean in, phases the wizard in and out.
    private void phase(boolean in) {
        // Note that every task resets the direction, so that the wizard tracks daur, and further increases/decreases
        // the transparency of the wizard.
        if (in) {
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(0.25f);
                    resetDirection(dir);
                }
            }, 0.2f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(0.5f);
                    resetDirection(dir);
                }
            }, 0.4f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(0.75f);
                    resetDirection(dir);
                }
            }, 0.6f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setAlpha(1);
                    resetDirection(dir);
                    invulnerability = false;
                    transparent = false;
                }
            }, 0.8f);
        }
        else {
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
                    invulnerability = true;
                    transparent = true;
                }
            }, 0.8f);
        }
    }

    private void resetDirection(int direction) {
        if (direction == RIGHT || direction == LEFT) {
            if (screen.daur.getX() > getX())
                dir = RIGHT;
            else
                dir = LEFT;
        }
        else {
            if (screen.daur.getY() > getY())
                dir = UP;
            else
                dir = DOWN;
        }
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
        else if (dir == RIGHT) {
            idle = new Animation(0.5f, FR);
            spell = new Animation(0.8f, FSR1, FSR2);
        }
        else if (dir == LEFT) {
            idle = new Animation(0.5f, FL);
            spell = new Animation(0.8f, FSL1, FSL2);
        }
    }

    private boolean inRange() {
        // If Daur is inside the cell.
        return screen.daur.getX() > getCellX() || screen.daur.getX() < getCellX() - 1 || screen.daur.getY() > getCellY()
                || screen.daur.getY() < getCellY() - 1;
    }

    private boolean validPosition(float x, float y) {
        // Checks if the selected position is blocked by either a tile or a character. Also checks if the position is
        // outside of the current cell. If any of the above is true, the method returns false.
        return !checkBlocked(x, y) && x > (getCellX() - 1) * layer.getTileWidth() &&
                x + getWidth() < getCellX() * layer.getTileWidth() * 10 &&
                y > (getCellY() - 1) * layer.getTileWidth() * 10 &&
                y + getHeight() < getCellY() * layer.getTileWidth() * 10 &&
                !checkCharBlocked(x, y) && !checkInteractableBlocked(x, y);
    }

    // If a character is blocking the location (such as Daur himself).
    private boolean checkCharBlocked(float x, float y) {
        for (float cX = x; cX < x + getWidth(); cX ++)
            for (float cY = y; cY < y + getHeight(); cY ++)
                for (Character character : screen.charIterator)
                    if (character.getBoundingRectangle().contains(x, y))
                        return true;
        return false;
    }

    // If an interactable is blocking the location.
    private boolean checkInteractableBlocked(float x, float y) {
        for (float cX = x; cX < x + getWidth(); cX ++)
            for (float cY = y; cY < y + getHeight(); cY ++)
                for (Interactable interactable : screen.interactables)
                    if (interactable.getBoundingRectangle().contains(x, y))
                        return true;
        return false;
    }

    // Checks if a terrain object is blocking Daur.
    private boolean checkBlocked(float x, float y) {
        for (float cX = x; cX < x + getWidth(); cX ++)
            for (float cY = y; cY < y + getHeight(); cY ++)
                if (isCellBlocked(cX, cY))
                    return true;
        return false;
    }

    protected boolean overrideCheck() {
        return (state == CASTING || state == DEAD);
    }
    //Overrides the current state if necessary.
    protected boolean priorities(int cState) {
        return (state == DEAD && cState != FALLING && cState != DROWNING) || state == FALLING || state == DROWNING;
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
