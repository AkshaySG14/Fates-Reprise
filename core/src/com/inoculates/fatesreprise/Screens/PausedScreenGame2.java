
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
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.InputProcessor.InventoryInput2;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.UI.*;

import java.util.ArrayList;

// This is the secondary pause game screen, which shows quest items only.
public class PausedScreenGame2 implements Screen {
    SpriteBatch batch;
    Game game;
    GameScreen screen;
    Storage storage;
    Sprite paused;
    Cursor cursor;
    // Font for all the text.
    BitmapFont display;
    BitmapFont saveGame;
    BitmapFont quitGame;
    // Item currently selected.
    Item currentItem;
    InventoryInput2 inputProcessor;
    // Bigheart that represents the amount of max health Daur has.
    BigHeart bHeart;

    float time = 0;
    // If the player has pressed down enough times, the cursor will go outside of the item grid, allowing the player
    // to save or quit the game.
    boolean outOfGrid = false;
    // The position of the cursor when it is outside the grid.
    int position;
    // The item grid.
    private float[] gridX = new float[9], gridY = new float[3];
    // The list of items Daur has in total.
    private ArrayList<Item> items = new ArrayList<Item>();
    // The sages, depicted as rescued or not.
    private ArrayList<UI> sages = new ArrayList<UI>();
    // This is the position of the cursor, broken down into its x and y components.
    private int numX = 0, numY = 2;

    // This creates the grid and input processor.
    public PausedScreenGame2(Game game, Storage storage, GameScreen screen) {
		this.game = game;
        this.storage = storage;
        this.screen = screen;
        items = storage.questItems;
        inputProcessor = new InventoryInput2(screen, storage, this);
        createSages();
        createGrid();
    }

    // Creates the background sprite, and sets the batch.
	@Override
	public void show () {
        paused = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/pausescreen2.png"))));
        paused.setPosition(0, 0);
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 160, 176);

        // Creates the cursor and sets it to the position of the first grid item.
        cursor = new Cursor(screen.daurAtlases.get(1));
        // Sets the mask position to zero, as this screen does not lie on the coordinate plane of any tiled map.
        screen.mask.setPosition(0, 0);
        Gdx.input.setInputProcessor(inputProcessor);

        // Creates the big heart.
        bHeart = new BigHeart(storage, screen.daurAtlases.get(1));
        bHeart.setPosition(33, 52.5f);

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
        // If inside the grid, draws normally, otherwise draws on the different UI on the bottom of the screen.
        if (!outOfGrid) {
            cursor.setScale(1);
            cursor.setPosition(gridX[numX], gridY[numY]);
        }
        else {
            Sprite target;
            switch (position) {
                // On big heart.
                case 1:
                    target = bHeart;
                    cursor.setPosition(target.getX() + target.getWidth() / 2 - cursor.getWidth() / 2,
                            target.getY() + target.getHeight() / 2 - cursor.getHeight() / 2);
                    cursor.setScale(2);
                    break;
                // On sage on the far left (Druni).
                case 2:
                    target = sages.get(0);
                    cursor.setScale(1.2f, 0.75f);
                    cursor.setPosition(target.getX() + target.getWidth() / 2 - cursor.getWidth() / 2,
                            target.getY() + target.getHeight() / 2 - cursor.getHeight() / 1.25f);
                    break;
                // On sage on the top left (Khalin).
                case 3:
                    target = sages.get(1);
                    cursor.setScale(1.2f, 0.75f);
                    cursor.setPosition(target.getX() + target.getWidth() / 2 - cursor.getWidth() / 2,
                            target.getY() + target.getHeight() / 2 - cursor.getHeight() / 1.25f);
                    break;
                // On sage on the top right (Laylia).
                case 4:
                    target = sages.get(2);
                    cursor.setScale(1.2f, 0.75f);
                    cursor.setPosition(target.getX() + target.getWidth() / 2 - cursor.getWidth() / 2,
                            target.getY() + target.getHeight() / 2 - cursor.getHeight() / 1.25f);
                    break;
                // On sage on the far right (Ragnor).
                case 5:
                    target = sages.get(3);
                    cursor.setScale(1.2f, 0.75f);
                    cursor.setPosition(target.getX() + target.getWidth() / 2 - cursor.getWidth() / 2,
                            target.getY() + target.getHeight() / 2 - cursor.getHeight() / 1.25f);
                    break;
                // On sage on the bottom right (Voorhe).
                case 6:
                    target = sages.get(4);
                    cursor.setScale(1.2f, 0.75f);
                    cursor.setPosition(target.getX() + target.getWidth() / 2 - cursor.getWidth() / 2,
                            target.getY() + target.getHeight() / 2 - cursor.getHeight() / 1.25f);
                    break;
                // On sage on the bottom left (Xalo).
                case 7:
                    target = sages.get(5);
                    cursor.setScale(1.2f, 0.75f);
                    cursor.setPosition(target.getX() + target.getWidth() / 2 - cursor.getWidth() / 2,
                            target.getY() + target.getHeight() / 2 - cursor.getHeight() / 1.25f);
                    break;
                // On save game button.
                case 8:
                    cursor.setPosition(23.5f + 14, 36.125f - 11.5f);
                    cursor.setScale(4.5f, 1.7f);
                    break;
                // On end game button
                case 9:
                    cursor.setPosition(93.75f + 12.5f, 36.125f - 11.5f);
                    cursor.setScale(4.5f, 1.7f);
                    break;
            }
        }
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
        // Draws the big heart.
        bHeart.draw(batch);
        // Draws all the sages.
        for (UI ui : sages)
            ui.draw(batch);
        // Shader for fonts.
        ShaderProgram fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        batch.setShader(fontShader);
        // Sets scale of fonts, to ensure it is not too large.
        display.setScale(0.18f);
        saveGame.setScale(0.25f);
        quitGame.setScale(0.25f);
        // Draws the text at the bottom of the screen. This is the text that describes the selected item.
        display.draw(batch, displayText(), 42.5f, 14);
        // Draws te two button fonts.
        saveGame.draw(batch, "Save Game", 23.5f, 36.125f);
        quitGame.draw(batch, "Quit Game", 93.75f, 36.125f);
        batch.setShader(null);
        screen.mask.draw(batch);
        batch.end();
        time += delta;
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

    // Creates the UI list for the sage UIs
    private void createSages() {
        UI ui;
        // Creates each of the sages in proceeding order.
        for (int i = 0; i < 6; i ++) {
            ui = new Sage(screen, screen.daurAtlases.get(1), i);
            sages.add(ui);
        }
    }

    // Creates the grid that is used to place the items.
    private void createGrid() {
        // Positions of the first and 3rd element of the x-grid and y-grid respectively.
        gridX[0] = 8;
        gridY[2] = 138;

        // Fills in the positions of the other elements, starting from 1. Distance of 16 between each element.
        for (int i = 1; i < gridX.length; i ++)
            gridX[i] = gridX[i - 1] + 16;

        // Fills in the positions of other elements, starting from 3. Distance of 29 between each element.
        for (int i = gridY.length - 2; i >= 0; i --)
            gridY[i] = gridY[i + 1] - 22;
    }

    // Increments or decrements the x or y position of the cursor on the grid. Sign is either -1 or 1.
    public void changeGrid(int sign, boolean up) {
        // Cursor is out of the grid, therefore this method is useless.
        if (outOfGrid) {
            moveOutOfGrid(sign, up);
            return;
        }
        // If vertical, applies change to the numY. Else to the numX.
        if (up)
            numY += sign;
        else numX += sign;

        // Ensures that the num values do not point to a non-existent element of the grid.
        if (numX < 0)
            numX = 0;

        if (numX > 8)
            numX = 8;

        // If the user is at the bottom and presses down once more, sets the cursor to be OUTSIDE the quest item grid.
        if (numY < 0) {
            outOfGrid = true;
            // Moves the cursor to the sage number if on the right side of the screen.
            // Moves to top left sage.
            if (numX == 4 || numX == 5 || numX == 6)
                position = 3;
            // Moves to top right sage.
            else if (numX == 7 || numX == 8)
                position = 4;
            else
            // Else moves the cursor to the big heart.
                position = 1;
        }
        if (numY > 2)
            numY = 2;
        // Sets the new current item, as the position of the cursor has moved.
        setCurrentItem();
    }

    private void moveOutOfGrid(int sign, boolean up) {
        // Moving the cursor up from the out of bounds position.
        if (up && sign == 1) {
            switch (position) {
                // Big heart is the focus. Move back into the grid.
                case 1:
                    outOfGrid = false;
                    position = 0;
                    numX = 2;
                    numY = 0;
                    break;
                // Sage on the far left is the focus. Move up to the top-left sage.
                case 2:
                    position = 3;
                    break;
                // Sage on the top-left is the focus. Move back into the grid. Note that numX is set to the right.
                case 3:
                    outOfGrid = false;
                    position = 0;
                    numX = 5;
                    numY = 0;
                    break;
                // Sage on the top-right is the focus. Move back into the grid. Note that numX is set to the extreme right.
                case 4:
                    outOfGrid = false;
                    position = 0;
                    numX = 7;
                    numY = 0;
                    break;
                // Sage on the far right is the focus. Move up to the top-right sage.
                case 5:
                    position = 4;
                    break;
                // Sage on the bottom right is the focus. Move up to the rightmost sage.
                case 6:
                    position = 5;
                    break;
                // Sage on the bottom left is the focus. Move up to the leftmost sage.
                case 7:
                    position = 2;
                    break;
                // Save Game button is the focus. Move up to the big heart.
                case 8:
                    position = 1;
                    break;
                // End Game button is the focus. Move up to the bottom left sage.
                case 9:
                    position = 7;
                    break;
            }
        }
        // Moves down.
        if (up && sign == -1) {
            switch (position) {
                // Big heart is the focus. Move down to save game button.
                case 1:
                    position = 8;
                    break;
                // Sage on the far left is the focus. Move down to the bottom-left sage.
                case 2:
                    position = 7;
                    break;
                // Sage on the top-left is the focus. Move down to the leftmost sage.
                case 3:
                    position = 2;
                    break;
                // Sage on the top-right is the focus. Move down to the rightmost sage.
                case 4:
                    position = 5;
                    break;
                // Sage on the far right is the focus. Move down to the bottom-right sage.
                case 5:
                    position = 6;
                    break;
                // Sage on the bottom right is the focus. Move down to the end game button.
                case 6:
                    position = 9;
                    break;
                // Sage on the bottom left is the focus. Move down to the end game button.
                case 7:
                    position = 9;
                    break;
                // Save Game button is the focus. Do nothing.
                case 8:
                    break;
                // End Game button is the focus. Do nothing.
                case 9:
                    break;
            }
        }
        // Moves right.
        if (!up && sign == 1)
            switch (position) {
                // Big heart is the focus. Move right to the sage on the far left.
                case 1:
                    position = 2;
                    break;
                // Sage on the far left is the focus. Move right to the top-left sage.
                case 2:
                    position = 3;
                    break;
                // Sage on the top-left is the focus. Move right to the top-right sage.
                case 3:
                    position = 4;
                    break;
                // Sage on the top-right is the focus. Move right to the rightmost sage.
                case 4:
                    position = 5;
                    break;
                // Sage on the far right is the focus. Do nothing.
                case 5:
                    break;
                // Sage on the bottom right is the focus. Move right to the rightmost sage.
                case 6:
                    position = 5;
                    break;
                // Sage on the bottom left is the focus. Move right to the bottom right sage.
                case 7:
                    position = 6;
                    break;
                // Save Game button is the focus. Move right to the end game button.
                case 8:
                    position = 9;
                    break;
                // End Game button is the focus. Do nothing.
                case 9:
                    break;
            }
        // Moves left.
        if (!up && sign == -1)
            switch (position) {
                // Big heart is the focus. Do nothing.
                case 1:
                    break;
                // Sage on the far left is the focus. Move left to the big heart.
                case 2:
                    position = 1;
                    break;
                // Sage on the top-left is the focus. Move left to the leftmost sage.
                case 3:
                    position = 2;
                    break;
                // Sage on the top-right is the focus. Move left to the top-left sage.
                case 4:
                    position = 3;
                    break;
                // Sage on the far right is the focus. Move left to the bottom-right sage.
                case 5:
                    position = 6;
                    break;
                // Sage on the bottom right is the focus. Move left to the bottom-left sage.
                case 6:
                    position = 7;
                    break;
                // Sage on the bottom left is the focus. Move left to the leftmost sage.
                case 7:
                    position = 2;
                    break;
                // Save Game button is the focus. Do nothing.
                case 8:
                    break;
                // End Game button is the focus. Move left to the save game button.
                case 9:
                    position = 8;
                    break;
            }
    }

    // Creates the bitmapfont that displays the item name.
    private void createText() {
        Texture texture = new Texture(Gdx.files.internal("Text/item.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        Texture texture2 = new Texture(Gdx.files.internal("Text/slots.png"));
        texture2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region2 = new TextureRegion(texture2);
        display = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        display.setUseIntegerPositions(false);
        display.setColor(Color.BLACK);
        saveGame = new BitmapFont(Gdx.files.internal("Text/slots.fnt"), region2, false);
        saveGame.setUseIntegerPositions(false);
        saveGame.setColor(Color.BLACK);
        quitGame = new BitmapFont(Gdx.files.internal("Text/slots.fnt"), region2, false);
        quitGame.setUseIntegerPositions(false);
        quitGame.setColor(Color.BLACK);
    }

    // Gets the name of the item based on the current item selected by the cursor.
    private String displayText() {
        // If inside of grid, displays the quest items accordingly.
        if (!outOfGrid) {
            if (currentItem == null)
                return "";
            else if (currentItem instanceof OakStaff)
                return "     Oak Staff";
            else if (currentItem instanceof GreatHollowKey)
                return "Great Hollow Key";
            else if (currentItem instanceof GreatHollowSmallKey)
                return "Great Hollow Dungeon Key";
            else if (currentItem instanceof GreatHollowBossKey)
                return "    Boss Key";

            return "";
        }
        else {
            switch (position) {
                default:
                    return "";
                case 1:
                    // Returns the number of pieces of heart.
                    return "Heart Pieces: " + storage.heartPieces + " / 4";
                case 2:
                    // If Druni is free, returns Druni's name and title, if not returns only his color and his title.
                    if (storage.sages[position - 2])
                        return "    Sage Druni";
                    else
                        return "  The Red Sage";
                case 3:
                    if (storage.sages[position - 2])
                        return "Sage Khalin";
                    else
                        return "The Green Sage";
                case 4:
                    if (storage.sages[position - 2])
                        return "Sage Laylia";
                    else
                        return "  The Blue Sage";
                case 5:
                    if (storage.sages[position - 2])
                        return "Sage Ragnor";
                    else
                        return "The Orange Sage";
                case 6:
                    if (storage.sages[position - 2])
                        return "Sage Voorhe";
                    else
                        return " The Cyan Sage";
                case 7:
                    if (storage.sages[position - 2])
                        return "Sage Xalo";
                    else
                        return "  The Pink Sage";
                // Save game button.
                case 8:
                    return "Saves the game";
                // End game button
                case 9:
                    return "Quits the game";
            }
        }
    }

    // Gets row of the current item.
    private int getRow(int index) {
        if (index < 9)
            return 2;
        if (index < 18)
            return 1;
        if (index < 27)
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
        int position = numX + (2 - numY) * 9;
        if (position >= items.size())
            currentItem = null;
        else
            currentItem = items.get(position);
    }

    // Checks if the quit button or save game button is hovered over while the talk button is pressed. If so, quits or
    // saves the game.
    public void checkButtonPressed() {
        // Button is save game, saves the game and informs the user by turning the text green for 0.4 seconds.
        if (position == 8) {
            storage.store();
            saveGame.setColor(Color.GREEN);
            // Freezes screen for 0.4 seconds.
            screen.freeze();
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    saveGame.setColor(Color.BLACK);
                    screen.unFreeze();
                }
            }, 0.4f);
        }
        // Button is quit game. Simply quits the game,
        if (position == 9) {
            quitGame.setColor(Color.GREEN);
            // Freezes screen until exit.
            screen.freeze();
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.exit();
                }
            }, 0.4f);
        }
    }

    // Changes the screen to the game screen.
    public void changeScreen() {
        game.setScreen(new PausedScreenGame(game, storage, screen));
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public int getCurrentPosition() {
        return numX + (4 - numY) * 9;
    }
}
