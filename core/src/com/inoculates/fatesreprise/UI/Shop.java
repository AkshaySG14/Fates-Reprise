package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.inoculates.fatesreprise.Events.ShopEvent;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;

// This is the shop UI, which houses multiple texts and items.
public class Shop extends UI {
    private GameScreen screen;
    private ShopEvent event;
    private Array<BitmapFont> fonts;
    private Array<Item> items = new Array<Item>();
    private Array<String> texts = new Array<String>();
    private ShaderProgram fontShader;
    private Indicator indicator;

    // Dictate the position of the text UIs on screen.
    private float[] positions;
    // Dictate the costs.
    private int[] costs;
    // The identity of the current item selected.
    private int itemNumber = 0;

    public Shop(GameScreen screen, TextureAtlas atlas, ShopEvent event, Array<BitmapFont> fonts, Array<String> texts, Array<Item> items) {
        super(atlas.findRegion("shopicon"));
        this.screen = screen;
        this.event = event;
        this.fonts = fonts;
        this.texts = texts;
        this.items = items;
        // Creates indicator.
        indicator = new Indicator(screen.daurAtlases.get(1));
        fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        for (BitmapFont font : fonts)
            font.setScale(0.235f);
        setPosition(screen.camera.position.x + screen.camera.viewportWidth / 2 - getWidth(),
                screen.camera.position.y + screen.camera.viewportHeight / 2 - getHeight() - 16);
        createPositions();
        initializeItems();
    }

    public void draw(Batch batch) {
        super.draw(batch);
        batch.setShader(fontShader);
        for (BitmapFont font : fonts) {
            // Gets the string the fonts draw from the text array.
            int index = fonts.indexOf(font, false);
            font.draw(batch, texts.get(index), getX() + 8, positions[index]);
        }
        indicator.draw(batch);
        batch.setShader(null);
    }
    // Checks if Daur has any items that were originally bought from the store, and removes them if so.
    private void initializeItems() {
        for (Item item : items)
            for (Item item2 : screen.storage.items)
                if (item.equals(item2)) {
                    // Removes the font, text, and item.
                    fonts.removeIndex(items.indexOf(item, false));
                    texts.removeIndex(items.indexOf(item, false));
                    items.removeValue(item, false);
                }
        // Creates the cost array.
        costs = new int[items.size];
        // Gets the item cost from each of the items.
        for (Item item : items)
            costs[items.indexOf(item, false)] = item.getCost();
    }

    public void renewPosition() {
        setPosition(screen.camera.position.x - screen.camera.viewportWidth / 2, screen.camera.position.y +
                screen.camera.viewportHeight / 2 - getHeight());
    }

    // Creates the position of each item.
    private void createPositions() {
        // Position set nearly at the top of the shop window.
        float position = getY() + getHeight() - 4;
        float height = 9;

        positions = new float[fonts.size];

        // Sets the position of every text bit. Subtracts each position from another by a set height.
        for (int i = 0; i < fonts.size; i ++) {
            positions[i] = position;
            position -= height;
        }
        // Sets the initial position of the indicator.
        indicator.setX(getX() + 3);
        indicator.setY(positions[itemNumber] - fonts.get(0).getCapHeight());
    }

    // Moves the indicator.
    public void movePosition(int direction) {
        // Sets the new current item by subtracting the given integer.
        itemNumber -= direction;
        // Ensures no overflow.
        if (itemNumber == items.size)
            itemNumber = 0;
        if (itemNumber < 0)
            itemNumber = items.size - 1;

        indicator.setY(positions[itemNumber] - fonts.get(0).getCapHeight());
    }

    public Item getItem() {
        return items.get(itemNumber);
    }

    public int getCost() {
        return costs[itemNumber];
    }

    public void update() {
        initializeItems();
    }

    public void destroy() {
        event.destroyShop();
    }

    public void end() {
        event.endShop();
    }

    public ShopEvent getShopEvent() {
        return event;
    }
}
