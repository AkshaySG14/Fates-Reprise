package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Note that this is the actual UI that houses all the different UI elements. The prior UI was the bar sprite itself.
public class BlueBarUI extends UI {
    GameScreen screen;
    // Different slots. These are the one-lettered indicators of the key the user has used to bound the separate slots.
    // This class is basically the three sets of brackets and the coin as a sprite.
    BitmapFont firstSlot, secondSlot, thirdSlot, money, keys;
    ShaderProgram fontShader;
    public BlueBar bar;

    public BlueBarUI(GameScreen screen, TextureAtlas atlas) {
        super(atlas.findRegion("blueui"));
        this.screen = screen;
        fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        // Creates the red part of the UI bar.
        bar = new BlueBar(screen, atlas, this);
        screen.UIS.add(bar);
        createText();
    }

    // Draws all the different texts.
    public void draw(Batch batch) {
        super.draw(batch);
        batch.setShader(fontShader);
        // Text telling user how much money he has.
        money.setScale(0.125f);
        money.draw(batch, Integer.toString(screen.storage.coins), getX() + 93, getY() + money.getCapHeight() + 2);
        // Text of the first slot, second slot, and third.
        firstSlot.setScale(0.25f);
        firstSlot.draw(batch, convertText(screen.storage.slotOne), getX() + 3, getY() + firstSlot.getCapHeight() + 6);
        secondSlot.setScale(0.25f);
        secondSlot.draw(batch, convertText(screen.storage.slotTwo), getX() + 28, getY() + secondSlot.getCapHeight() + 6);
        thirdSlot.setScale(0.25f);
        thirdSlot.draw(batch, convertText(screen.storage.slotThree), getX() + 53, getY() + thirdSlot.getCapHeight() + 6);

        // Draws the number of keys only if inside a dungeon.
        keys.setScale(0.15f);
        switch (screen.storage.dungeon) {
            case 0:
                thirdSlot.draw(batch, Integer.toString(screen.storage.GHKeys), getX() + 111, getY() + thirdSlot.getCapHeight() + 2);
                break;
        }
        batch.setShader(null);
        // Sets the position of the items themselves and draws, provided they exist. These are the icons of the items.
        if (screen.storage.item1 != null) {
            screen.storage.item1.setPosition(getX() + 11.5f, getY() + 2);
            screen.storage.item1.draw(batch);
        }
        if (screen.storage.item2 != null) {
            screen.storage.item2.setPosition(getX() + 36.5f, getY() + 2);
            screen.storage.item2.draw(batch);
        }
        if (screen.storage.item3 != null) {
            screen.storage.item3.setPosition(getX() + 61.5f, getY() + 2);
            screen.storage.item3.draw(batch);
        }
    }

    public void renewPosition() {
        setPosition(screen.camera.position.x - screen.camera.viewportWidth / 2, screen.camera.position.y +
                screen.camera.viewportHeight / 2 - getHeight());
    }

    // Creates fonts and other text-related objects.
    private void createText() {
        Texture texture = new Texture(Gdx.files.internal("Text/coins.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        money = new BitmapFont(Gdx.files.internal("Text/coins.fnt"), region, false);
        money.setUseIntegerPositions(false);
        Texture texture2 = new Texture(Gdx.files.internal("Text/slots.png"));
        texture2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region2 = new TextureRegion(texture2);
        firstSlot = new BitmapFont(Gdx.files.internal("Text/slots.fnt"), region2, false);
        firstSlot.setUseIntegerPositions(false);
        secondSlot = new BitmapFont(Gdx.files.internal("Text/slots.fnt"), region2, false);
        secondSlot.setUseIntegerPositions(false);
        thirdSlot = new BitmapFont(Gdx.files.internal("Text/slots.fnt"), region2, false);
        thirdSlot.setUseIntegerPositions(false);
        keys = new BitmapFont(Gdx.files.internal("Text/coins.fnt"), region, false);
        keys.setUseIntegerPositions(false);

        money.setColor(Color.BLACK);
        firstSlot.setColor(Color.BLACK);
        secondSlot.setColor(Color.BLACK);
        thirdSlot.setColor(Color.BLACK);
        keys.setColor(Color.BLACK);
    }

    // This converts every unconvertable key into proper text. (For example mouse button 1, enter, etc).
    private String convertText(final int input) {
        if (Input.Keys.toString(input).length() > 1)
            switch (input) {
                case 0:
                    return "M";
                case 1:
                    return "M";
                case 2:
                    return "M";
                case 19:
                    return "U";
                case 20:
                    return "D";
                case 21:
                    return "L";
                case 22:
                    return "R";
                case 59:
                    return "S";
                case 62:
                    return "B";
                case 129:
                    return "C";
            }
        else return Input.Keys.toString(input);
        return "UNKNOWN";
    }

}
