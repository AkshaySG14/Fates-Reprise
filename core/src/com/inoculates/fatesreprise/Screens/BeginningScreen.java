
package com.inoculates.fatesreprise.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.InputProcessor.BeginningInput;
import com.inoculates.fatesreprise.Storage.Storage;

// The screen when the game has just begun.
public class BeginningScreen implements Screen {
    SpriteBatch batch;
    Game game;
    Storage storage;
    Sprite beginning;
    // Font for all the text.
    BitmapFont mainDisplay, display1, display2, display3;
    BeginningInput inputProcessor;

    float time = 0;
    boolean transitioning = false;
    // The position of the cursor (whether it's on continue, continue and save, or save and quit. This determines which
    // text is highlighted.
    private int cursorPos = 0;

    // This creates the input processor.
    public BeginningScreen(Game game) {
		this.game = game;
        storage = new Storage();
        inputProcessor = new BeginningInput(storage, this);
        transition(true);
        // Sets an oncompletionlistener, so that the title screen music plays the looping version once played.
        storage.music.get("titlescreenmusic").setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                storage.music.get("titlescreenmusicloop").play();
                storage.music.get("titlescreenmusicloop").setVolume(0.75f);
                storage.music.get("titlescreenmusicloop").setLooping(true);
            }
        });
        // Plays the titlescreen music.
        storage.music.get("titlescreenmusic").play();
        storage.music.get("titlescreenmusic").setVolume(0.75f);
    }

    // Slowly fades the screen/text in/out.
    private void transition(boolean in) {
        Timer timer = new Timer();
        transitioning = true;
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                transitioning = false;
            }
        }, 2);
        // Fades in the screen and text.
        if (in)
            for (float time = 0; time <= 2; time += 0.05f) {
                final float alpha = time / 2;
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        beginning.setAlpha(alpha);
                        mainDisplay.setColor(alpha, alpha, alpha, alpha);
                        display1.setColor(display1.getColor().r, display1.getColor().g,
                                display1.getColor().b, alpha);
                        display2.setColor(display2.getColor().r, display2.getColor().g,
                                display2.getColor().b, alpha);
                        display3.setColor(display3.getColor().r, display3.getColor().g,
                                display3.getColor().b, alpha);
                    }
                }, time);
            }
        // Fades out the screen and text.
        else
            for (float time = 0; time <= 1; time += 0.05f) {
                final float alpha = 1 - time;
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        beginning.setAlpha(alpha);
                        mainDisplay.setColor(alpha, alpha, alpha, alpha);
                        display1.setColor(display1.getColor().r, display1.getColor().g,
                                display1.getColor().b, alpha);
                        display2.setColor(display2.getColor().r, display2.getColor().g,
                                display2.getColor().b, alpha);
                        display3.setColor(display3.getColor().r, display3.getColor().g,
                                display3.getColor().b, alpha);
                    }
                }, time);
            }
    }

    // Creates the background sprite, and sets the batch.
	@Override
	public void show () {
        beginning = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("Screens/beginningscreen.png"))));
        beginning.setPosition(0, 0);
        batch = new SpriteBatch();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, 160, 176);

        Gdx.input.setInputProcessor(inputProcessor);

        // Creates all relevant text.
        createText();
        // Sets all images to transparent first (background, texts).
        beginning.setAlpha(0);
        mainDisplay.setColor(0, 0, 0, 0);
        display1.setColor(display1.getColor().r, display1.getColor().g,
                display1.getColor().b, 0);
        display2.setColor(display2.getColor().r, display2.getColor().g,
                display2.getColor().b, 0);
        display3.setColor(display3.getColor().r, display3.getColor().g,
                display3.getColor().b, 0);
    }

	@Override
	public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        batch.begin();
        beginning.draw(batch);

        // Shader for fonts.
        ShaderProgram fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        batch.setShader(fontShader);
        // Sets scale of fonts, to ensure it is not too large.
        mainDisplay.setScale(0.5f);
        display1.setScale(0.25f);
        display2.setScale(0.25f);
        display3.setScale(0.25f);
        // Draws the continue game text.
        mainDisplay.draw(batch, "Fate's Reprise", 20, 160);
        display1.draw(batch, "New Game", 50, 117.5f);
        display2.draw(batch, "Continue Game", 31.5f, 72.5f);
        display3.draw(batch, "Quit Game", 48.5f, 30);
        // Sets the color of the text corresponding with the position of the cursor.
        setTextColor();
        batch.setShader(null);
        batch.end();
        time += delta;
    }

    // Sets the color of the text which the cursor is currently on (tells the player what happens when F is clicked).
    private void setTextColor() {
        // If transitioning, returns.
        if (transitioning)
            return;
        // Sets all displays to black first.
        display1.setColor(Color.BLACK);
        display2.setColor(Color.BLACK);
        display3.setColor(Color.BLACK);
        // Depending on the cursor position, sets the color of the text.
        switch (cursorPos) {
            // First position, new game.
            case 0:
                display1.setColor(Color.GREEN);
                break;
            // Second position, continue game.
            case 1:
                display2.setColor(Color.GREEN);
                break;
            // Third position, quit game.
            case 2:
                display3.setColor(Color.GREEN);
                break;
        }
    }

	@Override
	public void hide () {
        beginning.getTexture().dispose();
	}

    @Override
    public void resize (int width, int height) {

    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
        beginning.getTexture().dispose();
    }

    // Creates the bitmapfont that displays the options available to Daur.
    private void createText() {
        Texture texture = new Texture(Gdx.files.internal("Text/item.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        Texture texture2 = new Texture(Gdx.files.internal("Text/dialogue.png"));
        texture2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region2 = new TextureRegion(texture2);
        mainDisplay = new BitmapFont(Gdx.files.internal("Text/dialogue.fnt"), region2, false);
        mainDisplay.setUseIntegerPositions(false);
        mainDisplay.setColor(Color.WHITE);
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
        // Plays the sound that indicates the player moved the cursor.
        storage.sounds.get("click1").play(1.0f);
    }

    // Moves the cursor down by one, assuming it is not at the bottom already.
    public void moveDown() {
        // Moves the cursor's position to the top if at the bottom, else moves down normally.
        if (cursorPos == 2)
            cursorPos = 0;
        else
            cursorPos ++;
        // Plays the sound that indicates the player moved the cursor.
        storage.sounds.get("click1").play(1.0f);
    }

    // Executes the decision based on the cursor position.
    public void executeDecision() {
        // Plays the sound that indicates the player pressed a button.
        storage.sounds.get("click3").play(1.0f);
        switch (cursorPos) {
            case 0:
                newGame();
                break;
            case 1:
                loadGame();
                break;
            case 2:
                quitGame();
                break;
        }
        // Stops the title screen music.
        storage.music.get("titlescreenmusic").stop();
        storage.music.get("titlescreenmusicloop").stop();
    }

    // Creates a fresh, new game.
    private void newGame() {
        // Transitions first.
        transition(false);
        // After one second, creates a new game.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                final GameScreen screen = new GameScreen(game, storage);
                game.setScreen(screen);
                screen.newGame();
            }
        }, 1);
    }

    // Loads a pre-existing save.
    private void loadGame() {
        // If there is nothing to load, returns.
        if (!storage.loadVariables())
            return;
        // Transitions first.
        transition(false);
        // After one second, loads game.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                GameScreen screen = new GameScreen(game,storage);
                screen.loadGame();
                game.setScreen(screen);
            }
        }, 1);
    }

    // Simply exits the game.
    private void quitGame() {
        // Transitions first.
        transition(false);
        // After one second, quits game.
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        }, 1);
    }

    // Tells the input processor whether the screen is transitioning or not.
    public boolean getTransitioning() {
        return transitioning;
    }
}
