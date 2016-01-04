
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
import com.inoculates.fatesreprise.InputProcessor.DefeatInput;
import com.inoculates.fatesreprise.Storage.Storage;

// The screen when the player is defeated (Daur's life is zero).
public class DefeatScreen implements Screen {
    SpriteBatch batch;
    Game game;
    GameScreen screen;
    Storage storage;
    Sprite defeat;
    // Font for all the text.
    BitmapFont mainDisplay, display1, display2, display3;
    DefeatInput inputProcessor;

    float time = 0;
    // The position of the cursor (whether it's on continue, continue and save, or save and quit. This determines which
    // text is highlighted.
    private int cursorPos = 0;

    // This creates the input processor.
    public DefeatScreen(Game game, Storage storage, GameScreen screen) {
		this.game = game;
        this.storage = storage;
        this.screen = screen;
        inputProcessor = new DefeatInput(screen, storage, this);
    }

    // Creates the background sprite, and sets the batch.
	@Override
	public void show () {
        defeat = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/losescreen.png"))));
        defeat.setPosition(0, 0);
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 160, 176);

        // Sets the mask position to zero, as this screen does not lie on the coordinate plane of any tiled map.
        screen.mask.setPosition(0, 0);
        Gdx.input.setInputProcessor(inputProcessor);

        // Creates all relevant text.
        createText();
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        defeat.draw(batch);

        // Shader for fonts.
        ShaderProgram fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        batch.setShader(fontShader);
        // Sets scale of fonts, to ensure it is not too large.
        mainDisplay.setScale(0.3f);
        display1.setScale(0.25f);
        display2.setScale(0.25f);
        display3.setScale(0.25f);
        // Draws the continue game text.
        mainDisplay.draw(batch, "You Have Fallen", 17.5f, 160);
        display1.draw(batch, "Continue", 52.5f, 117.5f);
        display2.draw(batch, "Save & Continue", 27.5f, 72.5f);
        display3.draw(batch, "Save & Quit", 42.5f, 30);
        // Sets the color of the text corresponding with te position of the cursor.
        setTextColor();
        batch.setShader(null);
        screen.mask.draw(batch);
        batch.end();
        time += delta;
    }

    // Sets the color of the text which the cursor is currently on (tells the player what happens when F is clicked).
    private void setTextColor() {
        // Sets all displays to black first.
        display1.setColor(Color.BLACK);
        display2.setColor(Color.BLACK);
        display3.setColor(Color.BLACK);
        // Depending on the cursor position, sets the color of the text.
        switch (cursorPos) {
            // First position, continue game.
            case 0:
                display1.setColor(Color.WHITE);
                break;
            // Second position, save and continue game.
            case 1:
                display2.setColor(Color.WHITE);
                break;
            // Third position, save and quit game.
            case 2:
                display3.setColor(Color.WHITE);
                break;
        }
    }

	@Override
	public void hide () {
        defeat.getTexture().dispose();
	}

    @Override
    public void resize (int width, int height) {
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(0);
        screen.camera.viewportWidth = layer.getTileWidth() * 10;
        screen.camera.viewportHeight = layer.getTileHeight() * 10;
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
        defeat.getTexture().dispose();
    }

    // Creates the bitmapfont that displays the options available to Daur.
    private void createText() {
        Texture texture = new Texture(Gdx.files.internal("Text/item.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        mainDisplay = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        mainDisplay.setUseIntegerPositions(false);
        mainDisplay.setColor(Color.BLACK);
        display1 = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        display1.setUseIntegerPositions(false);
        display1.setColor(Color.BLACK);
        display2 = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        display2.setUseIntegerPositions(false);
        display2.setColor(Color.BLACK);
        display3 = new BitmapFont(Gdx.files.internal("Text/item.fnt"), region, false);
        display3.setUseIntegerPositions(false);
        display3.setColor(Color.BLACK);
    }

    // Moves the cursor up by one, assuming it is not at the top already.
    public void moveUp() {
        // Moves the cursor's position to the bottom if at the top, else moves up normally.
        if (cursorPos == 0)
            cursorPos = 2;
        else
            cursorPos --;
    }

    // Moves the cursor down by one, assuming it is not at the bottom already.
    public void moveDown() {
        // Moves the cursor's position to the top if at the bottom, else moves down normally.
        if (cursorPos == 2)
            cursorPos = 0;
        else
            cursorPos ++;
    }

    // Executes the decision based on the cursor position.
    public void executeDecision() {
        switch (cursorPos) {
            case 0:
                continueGame();
                break;
            case 1:
                saveAndContinue();
                break;
            case 2:
                saveAndQuit();
                break;
        }
    }

    // Continues the game WITHOUT saving.
    private void continueGame() {
        // Unpauses game, resets screen, and then respawns Daur.
        screen.unPauseGame();
        game.setScreen(screen);
        screen.daur.respawn();
    }

    // Saves the game then continues it.
    private void saveAndContinue() {
        // Unpauses game, resets screen, and then respawns Daur.
        screen.unPauseGame();
        game.setScreen(screen);
        screen.daur.respawn();
        // Saves game after Daur respawn to prevent health being saved as zero.
        storage.store();
    }

    // Saves game and then quits it.
    private void saveAndQuit() {
        // Sets health to 6 before leaving, so as to avoid a zero health bug.
        storage.setHealth(6);
        // Saves game.
        storage.store();
        // Quits game.
        Gdx.app.exit();
    }
}
