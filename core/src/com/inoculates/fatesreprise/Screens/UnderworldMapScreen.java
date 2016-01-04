
package com.inoculates.fatesreprise.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.fatesreprise.InputProcessor.UnderworldMapInput;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.UI.DaurIcon;

import java.util.ArrayList;

// The screen when the game is set to the map of a dungeon e.g. Great Hollow.
public class UnderworldMapScreen implements Screen {
    SpriteBatch batch;
    Game game;
    GameScreen screen;
    Storage storage;
    Sprite underWorld;
    DaurIcon icon;
    // Font for all the text; these are the levels of the dungeon.
    BitmapFont level1, level2, level3;
    UnderworldMapInput inputProcessor;

    float time = 0;
    // This is the position of the cursor, broken down into its x and y components.
    private int floor = 0;

    // This creates the grid and input processor.
    public UnderworldMapScreen(Game game, Storage storage, GameScreen screen) {
        this.game = game;
        this.storage = storage;
        this.screen = screen;
        inputProcessor = new UnderworldMapInput(screen, storage, this);
        floor = storage.FDstorage.level;
    }

    // Creates the background sprite, and sets the batch.
    @Override
    public void show() {
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 280, 280);
        setImage();

        // Creates the icon of Daur that indicates his position.
        icon = new DaurIcon(screen.miscAtlases.get(2));
        icon.setScale(0.5f);
        // Sets the mask position to zero, as this screen does not lie on the coordinate plane of any tiled map.
        screen.mask.setPosition(0, 0);
        screen.mask.setSize(320, 340);
        Gdx.input.setInputProcessor(inputProcessor);

        // Creates all relevant text.
        createText();
        // Sets numX and numY to the position of Daur on the map. Note that both cells are offset by the fact that the
        // entire map is not shown, only the dungeon relevant portion is. Therefore, one must examine the original tile
        // map to understand the offset.
        int numX = storage.cellX;
        int numY = storage.cellY - 9;
        // Displacement of Daur from within the specific cell based on the floor.
        float displacementX = 0, displacementY = 0;
        switch (floor) {
            case 0:
                displacementX = (screen.daur.getCX() - storage.cellX * 16 * 10) / 160 * 40;
                displacementY = (screen.daur.getCY() - storage.cellY * 16 * 10) / 160 * 40;
                break;
            case 1:
                displacementX = (screen.daur.getCX() - storage.cellX * 16 * 10 - 960) / 160 * 40 ;
                displacementY = (screen.daur.getCY() - storage.cellY * 16 * 10) / 160 * 40;
                break;
            case 2:
                displacementX = (screen.daur.getCX() - storage.cellX * 16 * 10 - 1280) / 160 * 40 ;
                displacementY = (screen.daur.getCY() - storage.cellY * 16 * 10) / 160 * 40;
                break;
        }
        // Sets the icon to the center of this cell.
        icon.setPosition(numX * 40 + displacementX - icon.getWidth() / 2,
                numY * 40 + displacementY - icon.getHeight() / 2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        underWorld.draw(batch);
        // If Daur is on the same floor as the map (e.g. Daur is on floor two and the map is currently viewing floor two)
        // displays where Daur is relative to the map.
        if (floor == storage.FDstorage.level)
            icon.draw(batch);
        // Shader for fonts.
        ShaderProgram fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        batch.setShader(fontShader);
        // Sets scale of fonts, to ensure it is not too large.
        level1.setScale(0.2f);
        level2.setScale(0.2f);
        level3.setScale(0.2f);
        // Draws the text at the bottom of the screen. This is the text that describes the selected item.
        level1.draw(batch, "Floor One", 10, 15);
        level2.draw(batch, "Floor Two", 10, 30);
        level3.draw(batch, "Floor Three", 10, 45);
        batch.setShader(null);
        screen.mask.draw(batch);
        batch.end();
        time += delta;
    }

    @Override
    public void hide() {
        underWorld.getTexture().dispose();
    }

    @Override
    public void resize(int width, int height) {
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(0);
        screen.camera.viewportWidth = layer.getTileWidth() * 10;
        screen.camera.viewportHeight = layer.getTileHeight() * 11;
        screen.camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        underWorld.getTexture().dispose();
    }

    public void changeFloor(int change) {
        floor += change;
        // If the floor is too high or too low, sets it to the lowest and highest (0 and 2 resp)
        if (floor < 0)
            floor = 0;
        if (floor > 2)
            floor = 2;
        // Sets the appropriate png image of the map.
        setImage();
        // Turns the current floor's text green.
        switch (floor) {
            case 0:
                level1.setColor(Color.GREEN);
                level2.setColor(Color.WHITE);
                level3.setColor(Color.WHITE);
                break;
            case 1:
                level2.setColor(Color.GREEN);
                level1.setColor(Color.WHITE);
                level3.setColor(Color.WHITE);
                break;
            case 2:
                level3.setColor(Color.GREEN);
                level2.setColor(Color.WHITE);
                level1.setColor(Color.WHITE);
                break;
        }
    }

    // Creates the bitmapfont that displays the name of the location in the grid.
    private void createText() {
        Texture texture = new Texture(Gdx.files.internal("Text/item.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        level1 = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        level1.setUseIntegerPositions(false);
        level1.setColor(Color.WHITE);
        level2 = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        level2.setUseIntegerPositions(false);
        level2.setColor(Color.WHITE);
        level3 = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        level3.setUseIntegerPositions(false);
        level3.setColor(Color.WHITE);
        // Turns the current floor's text green.
        switch (floor) {
            case 0:
                level1.setColor(Color.GREEN);
                level2.setColor(Color.WHITE);
                level3.setColor(Color.WHITE);
                break;
            case 1:
                level2.setColor(Color.GREEN);
                level1.setColor(Color.WHITE);
                level3.setColor(Color.WHITE);
                break;
            case 2:
                level3.setColor(Color.GREEN);
                level2.setColor(Color.WHITE);
                level1.setColor(Color.WHITE);
                break;
        }
    }

    // Sets the image and the projection matrix based on the floor and dungeon. This is to show Daur different floors.
    private void setImage() {
        switch (storage.dungeon) {
            default:
                break;
            // Great Hollow Dungeon
            case 0:
                switch (floor) {
                    // First floor of the Great Hollow Dungeon.
                    case 0:
                        // If Daur has the compass of the Great Hollow
                        if (storage.compasses[0])
                            underWorld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/LevelOneGreatHollowCompass.png"))));
                        // If he doesn't.
                        else
                            underWorld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/LevelOneGreatHollow.png"))));
                        break;
                    // Second floor of the Great Hollow Dungeon.
                    case 1:
                        // If Daur has the compass of the Great Hollow
                        if (storage.compasses[0])
                            underWorld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/LevelTwoGreatHollowCompass.png"))));
                        // If he doesn't.
                        else
                            underWorld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/LevelTwoGreatHollow.png"))));
                        break;
                    // Third floor of the Great Hollow Dungeon.
                    case 2:
                        // If Daur has the compass of the Great Hollow
                        if (storage.compasses[0])
                            underWorld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/LevelThreeGreatHollowCompass.png"))));
                        // If he doesn't.
                        else
                            underWorld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/LevelThreeGreatHollow.png"))));
                        break;
                }
                break;
        }
        underWorld.setPosition(0, 0);
    }
}
