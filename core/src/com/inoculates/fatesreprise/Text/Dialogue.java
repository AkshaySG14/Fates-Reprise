package com.inoculates.fatesreprise.Text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Events.Event;
import com.inoculates.fatesreprise.Screens.GameScreen;

public class Dialogue {
    private GameScreen screen;
    private Event owner;
    private String textValue;
    private TextBackground background;
    // The two lines of text. The former reers to the top line of text, and the latter the bottom.
    private BitmapFont text1, text2;
    private ShaderProgram fontShader;
    // A line is the TOTAL text being broken up into many different lines. Maxlines is the integer that stores the
    // amount of lines total.
    private int line = 0, maxLines;
    // This is the text list that holds all the lines together. Each string in the array is a line. Displaylist is
    // virtually the same as the textlist, except that it only SLOWLY adds all the lines to itself, and is the string
    // array used to display the text. This creates a stuttering effect.
    private String[] textList = new String[100];
    private String[] displayList = new String[100];

    public Dialogue(GameScreen screen, String text, Event owner) {
        this.screen = screen;
        this.textValue = text;
        this.owner = owner;

        // Initializes both lists.
        for (int i = 0; i < 99; i ++) {
            textList[i] = "";
            displayList[i] = "";
        }
        // Creates the bitmapfont shader, as usual.
        fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));

        // Creates all the fonts and backgrounds.
        createText();
        // Breaks the text up into parts.
        breakText();
    }

    private void createText() {
        AssetManager manager = new AssetManager();
        manager.load("Text/textbackground.pack", TextureAtlas.class);
        manager.finishLoading();

        background = new TextBackground(screen, (TextureAtlas) manager.get("Text/textbackground.pack"));
        Texture texture = new Texture(Gdx.files.internal("Text/dialogue.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        text1 = new BitmapFont(Gdx.files.internal("Text/dialogue.fnt"), region, false);
        text1.setUseIntegerPositions(false);
        text2 = new BitmapFont(Gdx.files.internal("Text/dialogue.fnt"), region, false);
        text2.setUseIntegerPositions(false);
    }

    // This is a recursive method that will constantly call itself to break up the text until it is fully divided into
    // lines.
    private void breakText() {
        String placeHolder = "";
        // If the text when displayed is longer than the dialogue box, proceeds to break it up.
        if (textValue.length() * 3.5f > background.getWidth()) {
            for (int i = textValue.length(); i > 0; i--)
                // Finds the space to truncate the string to so that words are not broken up.
                if (i * 3.5f < background.getWidth() && textValue.charAt(i) == " ".charAt(0)) {
                    // Truncates the string to that point and adds it to the array position, as decided by the line
                    // integer.
                    for (int x = 0; x <= i; x++)
                        textList[line] = textList[line] + textValue.charAt(x);
                    // Increments the line, as the next iteration of the method will need a new array position.
                    line ++;
                    // Sets the text value to the REST of the string by adding it to a placeholder string. The text
                    // value string is then set to the placeholder.
                    for (int y = i + 1; y < textValue.length(); y ++)
                        placeHolder = placeHolder + textValue.charAt(y);
                    textValue = placeHolder;
                    break;
                }
            // Recursive looping until the text value is a small enough length. Exits the method.
            breakText();
            return;
        }
        // Sets the final line of text, as the entire if statement is skipped if the method reaches this far.
        textList[line] = textValue;
        // Gets the total number of lines.
        maxLines = line;
        // Sets the line integer back to zero, as it is later used to draw the text.
        line = 0;
        // Draws the text onto the screen.
        stutterText();
        // Creates the green or red sprite that indicates if the user is at the end of the dialogue (bottom right corner
        // of the box).
        setBackgroundSprite();
    }

    // Draws the current line of text and resets the position of the background.
    public void displayText() {
        background.setPosition(screen.camera.position.x - background.getWidth() / 2, screen.camera.position.y - screen.camera.viewportHeight / 2 + background.getHeight() / 2);
        screen.batch.setShader(fontShader);
        text1.setScale(0.2f);
        // Draws the first line of text, as given by the variable line.
        text1.draw(screen.batch, displayList[line], background.getX() + text1.getSpaceWidth(), background.getY() + background.getHeight() - text1.getCapHeight() * 0.75f);
        text2.setScale(0.2f);
        // Draws the second line of text.
        text2.draw(screen.batch, displayList[line + 1], background.getX() + text2.getSpaceWidth(), background.getY() + text2.getCapHeight() * 2);
        screen.batch.setShader(null);
    }

    // Sets the small indicator at the corner to be a green arrow or red square, depending on whether the user is on
    // the last string of dialogue.
    private void setBackgroundSprite() {
        if (line + 1 >= maxLines)
            background.setState(false);
        else background.setState(true);
    }

    // Moves to the next line.
    public void incrementLine() {
        // If the user is at the end of the dialogue, exits the dialogue and event.
        if (line + 1 >= maxLines)
            owner.proceed();
        // Otherwise increases the line by two. NOTE: this is because line + 1 denotes the line that is drawn to the
        // bottom of the current line.
        else line += 2;
        // Draws the new line in a stutter-like fashion.
        stutterText();
        // Checks if the arrow should be changed to a square.
        setBackgroundSprite();
    }

    // Slowly writes the text out to create a more aesthetically pleasing effect.
    private void stutterText() {
        float deltaTime = 0;
        // From line 1 to line 2.
        for (int i = line; i <= line + 1; i ++)
            // Each character individually.
            for (int o = 0; o < textList[i].length(); o++) {
                // Gets the final version for concurrent threading.
                final int arrayLine = i;
                final int charNumber = o;
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        // Slowly adds the characters to the line of the displaylist.
                        displayList[arrayLine] = displayList[arrayLine] + textList[arrayLine].charAt(charNumber);
                    }
                }, deltaTime);
                timer.start();
                // Increases time.
                deltaTime += 0.025f;
            }
    }

    public TextBackground getBackground() {
        return background;
    }
}
