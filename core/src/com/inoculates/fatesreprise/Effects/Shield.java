package com.inoculates.fatesreprise.Effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Projectiles.MagicSphere;
import com.inoculates.fatesreprise.Projectiles.Projectile;
import com.inoculates.fatesreprise.Projectiles.Splinter;
import com.inoculates.fatesreprise.Screens.GameScreen;

public class Shield extends Effect {
    Animation anim;
    Character owner;

    // Shield effect for Daur.
    public Shield(GameScreen screen, TiledMap map, TextureAtlas atlas, Character owner) {
        super(screen, map, atlas, true);
        this.owner = owner;
        chooseSprite();
        reposition();
    }

    protected void update(float deltaTime) {
        chooseSprite();
        reposition();
        updateTime(deltaTime);
        repelObjects();
    }

    protected void chooseSprite() {
        createAnimations();
        setRegion(anim.getKeyFrame(animationTime, true));
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 3 / 4, anim.getKeyFrame(animationTime, true).getRegionHeight());
    }

    private void reposition() {
        setPosition(owner.getX() + owner.getWidth() / 2 - getWidth() / 2, owner.getY() + owner.getHeight() / 2
                - getHeight() / 2);
    }

    // Repels projectiles and enemies alike.
    private void repelObjects() {
        final float RADIUS = 15;
        final float cX = getX() + getWidth() / 2;
        final float cY = getY() + getHeight() / 2;

        // Checks if an enemy is inside the shield. If so, deflects the enemy away from Daur.
        for (Character character : screen.charIterator)
            if (character instanceof Enemy) {
                // Gets all points of the enemy.
                for (float x = character.getX(); x < character.getX() + character.getWidth(); x ++)
                    for (float y = character.getY(); y < character.getY() + character.getHeight(); y ++) {
                        // Uses the distance formula to determine whether the enemy is inside the shield (is less than
                        // one radius away from the center of the shield).
                        if (Math.sqrt(Math.pow(cX - x, 2) + Math.pow(cY - y, 2)) < RADIUS)
                            ((Enemy) character).stunCollision(this, 0.1f);
                    }
            }

        // Same but for projectiles.
        for (Projectile projectile : screen.projectiles)
            for (float x = projectile.getX(); x < projectile.getX() + projectile.getWidth(); x ++)
                for (float y = projectile.getY(); y < projectile.getY() + projectile.getHeight(); y ++)
                    // If projectile within range of shield, explodes the projectile.
                    if (Math.sqrt(Math.pow(cX - x, 2) + Math.pow(cY - y, 2)) < RADIUS && isIncluded(projectile))
                        projectile.explode();

    }

    // Checks if the projectile is excluded from being blocked (cannot be shielded)
    private boolean isIncluded(Projectile proj) {
        return !(proj instanceof MagicSphere || proj instanceof Splinter);
    }

    // Shield animation
    protected void createAnimations() {
        anim = new Animation(0.333333f, atlas.findRegion("shield1"), atlas.findRegion("shield2"));
    }

    public void setAnimationTime(float time) {
        animationTime = time;
    }

}
