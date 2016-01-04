package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is a teleporter that teleports Daur to its corresponding teleporter.
public class Platform extends Interactable {
    private boolean cooldown;

    public Platform(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, float velX, float velY) {
        super(screen, map, atlas, storage);
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.storage = storage;
        vel.x = velX;
        vel.y = velY;
        layer = (TiledMapTileLayer) map.getLayers().get(2);
    }

    // Checks if the platform has gone past a marker, and reverses its direction if so.
    private void checkBounds() {
        // Cannot reverse direction during cooldown.
        if (cooldown)
            return;
        for (MapObject object : map.getLayers().get("Markers").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                Rectangle marker = new Rectangle(x, y, layer.getTileWidth(), layer.getTileHeight());
                // Object is a platform marker, and it contains the platform, reverses it.
                if (object.getProperties().containsKey("platformmarker")) {
                    // Moving right and up, checks accordingly.
                    if (vel.x > 0 && vel.y > 0 && marker.contains(getRX(), getRY())) {
                        // Reverses the direction if so.
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                    // Moving right and down.
                    else if (vel.x > 0 && vel.y < 0 && marker.contains(getRX(), getY())) {
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                    // Moving right only.
                    else if (vel.x > 0 && vel.y == 0 && marker.contains(getRX(), getCY())) {
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                    // Moving left and up.
                    else if (vel.x < 0 && vel.y > 0 && marker.contains(getX(), getRY())) {
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                    // Moving left and down.
                    else if (vel.x < 0 && vel.y < 0 && marker.contains(getX(), getY())) {
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                    // Moving left only.
                    else if (vel.x < 0 && vel.y == 0 && marker.contains(getX(), getCY())) {
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                    // Moving up only.
                    else if (vel.x == 0 && vel.y > 0 && marker.contains(getCX(), getRY())) {
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                    // Moving down only.
                    else if (vel.x == 0 && vel.y < 0 && marker.contains(getCX(), getY())) {
                        vel.x = -vel.x;
                        vel.y = -vel.y;
                        cooldown();
                    }
                }
            }
    }

    // After 0.1 seconds the platform can reverse direction again.
    private void cooldown() {
        cooldown = true;
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cooldown = false;
            }
        }, 0.1f);
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected boolean overrideCheck() {
        return true;
    }

    protected void tryMove() {
        checkCollisions();
        detectConditions();
        checkBounds();
    }

    protected void update(float time) {
        animationTime += time;
    }

    protected void createAnimations() {

    }

    protected void chooseSprite() {
        setRegion(atlas.findRegion("platform"));
        setSize(getRegionWidth(), getRegionHeight());
    }

    public float getVelX() {
        return vel.x;
    }

    public float getVelY() {
        return vel.y;
    }
}
