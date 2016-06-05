
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
import com.inoculates.fatesreprise.InputProcessor.OverworldMapInput;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.UI.*;

import java.util.ArrayList;

// The screen when the game is set to the overworld map.
public class OverworldMapScreen implements Screen {
    SpriteBatch batch;
    Game game;
    GameScreen screen;
    Storage storage;
    Sprite overWorld;
    MapIndicator indicator;
    DaurIcon icon;
    // Font for all the text.
    BitmapFont display;
    OverworldMapInput inputProcessor;
    // The array of gray squares used to block out the map.
    private ArrayList<GraySquare> squares = new ArrayList<GraySquare>();

    float time = 0;
    // This is the position of the cursor, broken down into its x and y components.
    private int numX = 0, numY = 0;

    // This creates the grid and input processor.
    public OverworldMapScreen(Game game, Storage storage, GameScreen screen) {
		this.game = game;
        this.storage = storage;
        this.screen = screen;
        inputProcessor = new OverworldMapInput(screen, storage, this);
    }

    // Creates the background sprite, and sets the batch.
	@Override
	public void show () {
        overWorld = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/overworldscreen.png"))));
        overWorld.setPosition(0, 0);
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 320, 340);

        // Creates the indicator and sets it to the position of the first grid item.
        indicator = new MapIndicator(screen.miscAtlases.get(2));
        // Creates the icon of Daur that indicates his position.
        icon = new DaurIcon(screen.miscAtlases.get(2));
        // Sets the mask position to zero, as this screen does not lie on the coordinate plane of any tiled map.
        screen.mask.setPosition(0, 0);
        screen.mask.setSize(320, 340);
        Gdx.input.setInputProcessor(inputProcessor);

        // Creates all relevant text.
        createText();
        // Sets numX and numY to the position of Daur on the map.
        numX = storage.cellX;
        numY = storage.cellY;
        indicator.setPosition(numX * 20, numY * 20 + 19);
        // Sets the icon to the center of this cell.
        icon.setPosition(numX * 20 + 10 - icon.getWidth() / 2, numY * 20 + 29 - icon.getHeight() / 2);
        // Creates all the gray squares necessary.
        createGraySquares();
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        overWorld.draw(batch);
        // Draws the gray squares.
        for (GraySquare square : squares)
                square.draw(batch);
        // Draws cursor and repositions it.
        indicator.setPosition(numX * 20, numY * 20 + 19);
        indicator.draw(batch);
        icon.draw(batch);
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

	@Override
	public void hide () {
        overWorld.getTexture().dispose();
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
        overWorld.getTexture().dispose();
    }

    // Creates all the gray squares, which obscure areas that have not been explored.
    private void createGraySquares() {
        for (int x = 0; x < 16; x ++)
            for (int y = 0; y < 16; y ++)
                // If the area is not explored, creates a gray square at that point.
                if (!storage.explored[x][y]) {
                    GraySquare square = new GraySquare(screen.miscAtlases.get(2));
                    // Uses the x and y integers to set the corresponding position in the grid.
                    square.setPosition(x * 20, y * 20 + 19);
                    // Adds to the rendering list.
                    squares.add(square);
                }
    }

    // Increments or decrements the x or y position of the indicator on the grid. Sign is either -1 or 1.
    public void changeGrid(int sign, boolean up) {
        // If vertical, applies change to the numY. Else to the numX.
        if (up)
            numY += sign;
        else
            numX += sign;

        // Ensures that the num values do not point to a non-existent element of the grid.
        if (numX < 0)
            numX = 0;

        if (numX > 15)
            numX = 15;

        if (numY < 0)
            numY = 0;

        if (numY > 15)
            numY = 15;

        // Plays the sound that indicates the player moved the cursor.
        storage.sounds.get("click1").play(1.0f);
    }

    // Creates the bitmapfont that displays the name of the location in the grid.
    private void createText() {
        Texture texture = new Texture(Gdx.files.internal("Text/item.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        display = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        display.setUseIntegerPositions(false);
        display.setColor(Color.BLACK);
    }

    // Gets the name of the location based on the current grid position.
    private String displayText() {
        // Location is unknown, tell the user as such.
        if (!storage.explored[numX][numY])
            return "                         Unknown Location";
        // Uses the specific cell to generate the name.
        if (numX == 8 && numY == 4)
            return "                       Great Hollow Entrance";
        if ((numY == 3 || numY == 2 || numY == 1) && (numX == 7 || numX == 8 || numX == 9))
            return "                               Faron Woods";
        if (numY == 0 && (numX == 7 || numX == 8))
            return "                               Faron Woods";
        if (numY == 4 && (numX == 7 || numX == 9))
            return "                               Faron Woods";
        if (numY == 5 && (numX == 7 || numX == 9))
            return "                      Faron Woods Entrance";
        if (numY == 5 && numX == 8)
            return "                  Carthell Village Outskirts";
        if ((numY == 7 || numY == 8) && numX == 7)
            return "                  Carthell Village Outskirts";
        if (numY == 6 && numX == 8)
            return "                       Aragoth's Fountain";
        if (numY == 6 && numX == 7)
            return "                         Unoccupied House";
        if (numY == 9 && numX == 11)
            return "                         Carthell Library";
        if (numY == 8 && numX == 10)
            return "                       Carthell Marketplace";
        if (numY == 7 && numX == 11)
            return "                         Four Statue Square";
        if (numY == 6 && (numX == 9 || numX == 10 || numX == 11 || numX == 12))
            return "                          Carthell Village";
        if (numY == 7 && (numX == 8 || numX == 9 || numX == 10 || numX == 12))
            return "                          Carthell Village";
        if (numY == 8 && (numX == 8 || numX == 9 || numX == 11 || numX == 12))
            return "                          Carthell Village";
        if (numY == 9 && (numX == 8 || numX == 9 || numX == 10 || numX == 12))
            return "                          Carthell Village";
        if (numX == 10 && numY == 3)
            return "                       Fairy Queen's Fountain";
        return "";
    }
}
