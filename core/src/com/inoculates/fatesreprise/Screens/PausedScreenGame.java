
package com.inoculates.fatesreprise.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.fatesreprise.InputProcessor.InventoryInput;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.UI.BlueBar;
import com.inoculates.fatesreprise.UI.BlueBarUI;
import com.inoculates.fatesreprise.UI.Cursor;
import com.inoculates.fatesreprise.UI.UI;

import java.util.ArrayList;

// The screen when the game is paused.
public class PausedScreenGame implements Screen {
    SpriteBatch batch;
    Game game;
    GameScreen screen;
    Storage storage;
    Sprite paused;
    Cursor cursor;
    // Font for all the text.
    BitmapFont display;
    // Item currently selected.
    Item currentItem;
    InventoryInput inputProcessor;

    float time = 0;
    // The item grid.
    private float[] gridX = new float[9], gridY = new float[5];
    // The list of items Daur has in total.
    private ArrayList<Item> items = new ArrayList<Item>();
    // This is the position of the cursor, broken down into its x and y components.
    private int numX = 0, numY = 4;

    // This creates the grid and input processor.
    public PausedScreenGame(Game game, Storage storage, GameScreen screen) {
		this.game = game;
        this.storage = storage;
        this.screen = screen;
        items = storage.items;
        inputProcessor = new InventoryInput(screen, storage, this);
        createGrid();
    }

    // Creates the background sprite, and sets the batch.
	@Override
	public void show () {
        paused = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/pausescreen.png"))));
        paused.setPosition(0, 0);
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 160, 176);

        // Creates the cursor and sets it to the position of the first grid item.
        cursor = new Cursor(screen.daurAtlases.get(1));
        // Sets the mask position to zero, as this screen does not lie on the coordinate plane of any tiled map.
        screen.mask.setPosition(0, 0);
        Gdx.input.setInputProcessor(inputProcessor);

        // Creates all relevant text and sets the current item.
        createText();
        setCurrentItem();
        cursor.setPosition(gridX[numX], gridY[numY]);
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        paused.draw(batch);
        // Draws cursor and repositions it.
        cursor.draw(batch);
        cursor.setPosition(gridX[numX], gridY[numY]);
        // Draws all UI.
        for (UI ui : screen.UIS) {
            // Note that the blue bar does not need to be drawn, as there exits a space for the UI already.
            if (!(ui instanceof BlueBar)) {
                ui.draw(batch);
                ui.renewPosition();
            }
            // Sets position of the UI on the blue bar.
            if (ui instanceof BlueBarUI)
                // Sets position of the blue bar.
                ui.setPosition(0, 176 - ui.getHeight());
        }
        // Sets the position of each item, depending on its position in the array list.
        for (Item item : items)
            if (item != null) {
                item.setPosition(gridX[getColumn(items.indexOf(item))] + 2, gridY[getRow(items.indexOf(item))] + 2);
                item.draw(batch);
            }
        // Shader for fonts.
        ShaderProgram fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        batch.setShader(fontShader);
        // Sets scale of fonts, to ensure it is not too large.
        display.setScale(0.18f);
        // Draws the text at the bottom of the screen. This is the text that describes the selected item.
        display.draw(batch, displayText(), 42.5f, 14);
        batch.setShader(null);
        screen.mask.draw(batch);
        batch.end();
        time += delta;
    }

    public void changeScreen() {
        game.setScreen(new PausedScreenGame2(game, storage, screen));
    }

	@Override
	public void hide () {
        paused.getTexture().dispose();
	}

    @Override
    public void resize (int width, int height) {
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(0);
        screen.camera.viewportWidth = layer.getTileWidth() * 10;
        screen.camera.viewportHeight = layer.getTileHeight() * 11;
        screen.camera.update();
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        paused.getTexture().dispose();
    }

    // Creates the grid that is used to place the items.
    private void createGrid() {
        // Positions of the first and 3rd element of the x-grid and y-grid respectively.
        gridX[0] = 8;
        gridY[4] = 137;

        // Fills in the positions of the other elements, starting from 1. Distance of 16 between each element.
        for (int i = 1; i < gridX.length; i ++)
            gridX[i] = gridX[i - 1] + 16;

        // Fills in the positions of other elements, starting from 3. Distance of 29 between each element.
        for (int i = gridY.length - 2; i >= 0; i --)
            gridY[i] = gridY[i + 1] - 29;
    }

    // Increments or decrements the x or y position of the cursor on the grid. Sign is either -1 or 1.
    public void changeGrid(int sign, boolean up) {
        // If vertical, applies change to the numY. Else to the numX.
        if (up)
            numY += sign;
        else numX += sign;

        // Ensures that the num values do not point to a non-existent element of the grid.
        if (numX < 0)
            numX = 0;

        if (numX > 8)
            numX = 8;

        if (numY < 0)
            numY = 0;

        if (numY > 4)
            numY = 4;
        // Sets the new current item, as the position of the cursor has moved.
        setCurrentItem();
        // Plays the sound that indicates the player moved the cursor.
        storage.sounds.get("click1").play(1.0f);
    }

    // Creates the bitmapfont that displays the item name.
    private void createText() {
        Texture texture = new Texture(Gdx.files.internal("Text/item.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        display = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        display.setUseIntegerPositions(false);
        display.setColor(Color.BLACK);
    }

    // Gets the name of the item based on the current item selected by the cursor.
    private String displayText() {
        if (currentItem == null)
            return "";
        else if (currentItem instanceof BasicSwordItem)
            return "Level One Sword";
        else if (currentItem instanceof ConcussiveShotItem)
            return "Concussive Shot";
        else if (currentItem instanceof ShieldItem)
            return "       Shield";
        else if (currentItem instanceof WindSickleItem)
            return "   Wind Sickles";
        else if (currentItem instanceof ZephyrsWispItem)
            return "   Zephyr's Wisp";
        else if (currentItem instanceof MinorHealthPotionItem)
            return "  Minor HP Pot";

        return "";
    }

    // Gets row of the current item.
    private int getRow(int index) {
        if (index < 9)
            return 4;
        if (index < 18)
            return 3;
        if (index < 27)
            return 2;
        if (index < 36)
            return 1;
        if (index < 45)
            return 0;
        return 0;
    }

    // Gets column of the current item.
    private int getColumn(int index) {
        if (index < 9)
            return index;
        if (index < 18)
            return index - 9;
        if (index < 27)
            return index - 18;
        if (index < 36)
            return index - 27;
        if (index < 45)
            return index - 36;
        return 0;
    }

    // Sets the current item depending on numX and numY, which tell the program where the cursor is.
    public void setCurrentItem() {
        int position = numX + (4 - numY) * 9;
        if (position >= items.size())
            currentItem = null;
        else
            currentItem = items.get(position);
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public int getCurrentPosition() {
        return numX + (4 - numY) * 9;
    }
}
