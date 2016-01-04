package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.InputProcessor.ShopInput;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.UI.Shop;

// This is the event that creates the shop window, for when Daur wishes to buy items.
public class ShopEvent extends Event {
    private int type = 0;
    private Villager villager;
    private Shop shop;

    public ShopEvent(TiledMap map, GameScreen screen, Character character) {
        super(screen, map);
        villager = (Villager) character;
        setType();
        startEvent();
    }

    // Sets the type of the shop, as each shop keeper has a different assortment of items.
    private void setType() {
        if (villager.getVillager() == 20)
            type = 0;
        if (villager.getVillager() == 21)
            type = 1;
        if (villager.getVillager() == 22)
            type = 2;
    }

    protected void startEvent() {
        message();
    }

    protected void message() {
        switch (stage) {
            case 0:
                // Creates the shop dialogue and then stuns Daur, as well as setting his animation to idle.
                createShop();
                screen.daur.forceState(0);
                screen.daur.freeze();
                screen.daur.stun();
                break;
            // After the player presses exit in the shop window.
            case 1:
                // Unstuns daur and sets the input processor back to the normal one.
                screen.daur.unStun();
                screen.resetProcessor();
                VillagerEvents events = new VillagerEvents(map, screen, screen.storage, villager);
                break;
        }
    }

    // Creates the shop screen window and sets the input processor to the shop one.
    private void createShop() {
        // Creates the fonts, texts, and items for the shop.
        Array<BitmapFont> fonts = createFonts();
        Array<String> texts = createText();
        Array<Item> items = createItems();
        // Creates the shop screen itself.
        shop = new Shop(screen, screen.daurAtlases.get(1), this, fonts, texts, items);
        // Adds the shop window to the rendering list.
        screen.UIS.add(shop);
        // Sets the input processor to thes hop input, so that the player can properly interact with the shop.
        ShopInput input = new ShopInput(screen, screen.storage, shop);
        Gdx.input.setInputProcessor(input);
    }

    // Destroys the shop after the user exits.
    public void destroyShop() {
        stage ++;
        message();
        screen.UIS.removeValue(shop, false);
    }

    // Destroys the shopUI only.
    public void endShop() {
        screen.UIS.removeValue(shop, false);
    }

    // Creates the bitmap fonts, which is later used for the text.
    private Array<BitmapFont> createFonts() {
        Array<BitmapFont> fonts = new Array<BitmapFont>();
        // Creates the shop bitmap font.
        Texture texture = new Texture(Gdx.files.internal("Text/shop.png"));
        // Sets the filter.
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        switch (type) {
            case 0:
                // Magic shop.
                // Creates the actual bitmap font itself. Note that 10 separate fonts are created for all 10 items.
                for (int i = 0; i < 10; i++) {
                    BitmapFont font = new BitmapFont(Gdx.files.internal("Text/shop.fnt"), region, false);
                    font.setUseIntegerPositions(false);
                    fonts.add(font);
                }
                return fonts;
            case 1:
                // Potion shop.
                // For the 4 items.
                for (int i = 0; i < 4; i++) {
                    BitmapFont font = new BitmapFont(Gdx.files.internal("Text/shop.fnt"), region, false);
                    font.setUseIntegerPositions(false);
                    fonts.add(font);
                }
                return fonts;
            case 2:
                // Misc shop.
                // For the 5 items.
                for (int i = 0; i < 5; i++) {
                    BitmapFont font = new BitmapFont(Gdx.files.internal("Text/shop.fnt"), region, false);
                    font.setUseIntegerPositions(false);
                    fonts.add(font);
                }
                return fonts;
            default:
                return null;
        }
    }

    // Creates the strings for the bitmap fonts to draw. This is used for the drawing of text in the shop UI.
    private Array<String> createText() {
        Array<String> texts = new Array<String>();

        switch (type) {
            // Spell shop.
            case 0:
                texts.add("Shield" + getSpaces("Shield") + " 20g");
                texts.add("Flame Gout" + getSpaces("Flame Gout") + "120g");
                texts.add("Giant's Might" + getSpaces("Giant's Might") + "180g");
                texts.add("Minor Teleport" + getSpaces("Minor Teleport") + "220g");
                texts.add("Mirror Image" + getSpaces("Mirror Image") + "255g");
                texts.add("Planar Shift" + getSpaces("Planar Shift") + "340g");
                texts.add("Live Wire" + getSpaces("Live Wire") + "410g");
                texts.add("Stone Fist" + getSpaces("Stone Fist") + "485g");
                texts.add("Pulverize" + getSpaces("Pulverize") + "510g");
                texts.add("Reflect" + getSpaces("Reflect") + "605g");
                return texts;
            case 1:
                // Potion shop.
                texts.add("Minor Health" + getSpaces("Minor Health") + " 15g");
                texts.add("Minor Haste" + getSpaces("Minor Haste") + "130g");
                texts.add("Lesser Rejuv" + getSpaces("Lesser Rejuv") + "210g");
                texts.add("Steelskin" + getSpaces("Steelskin") + "255g");
                return texts;
            case 2:
                // Misc shop.
                texts.add("Oil Canister" + getSpaces("Oil Canister") + " 20g");
                texts.add("Bottled Water" + getSpaces("Bottled Water") + " 25g");
                texts.add("Tube" + getSpaces("Tube") + " 10g");
                texts.add("Rusted Iron" + getSpaces("Rusted Iron") + " 35g");
                texts.add("Broken Bulb" + getSpaces("Broken Bulb") + " 40g");
                return texts;
            default:
                return null;
        }
    }

    // Creates the items themselves. This is the actual item given to Daur that he purchases.
    // Note most of the items here are just copies of the shield item to serve as place holders.
    private Array<Item> createItems() {
        Array<Item> items = new Array<Item>();
        switch (type) {
            // Magic Shop.
            case 0:
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                return items;
            // Potion Shop.
            case 1:
                items.add(new MinorHealthPotionItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                return items;
            // Misc Shop.
            case 2:
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                items.add(new ShieldItem(screen.daurAtlases.get(2)));
                return items;
            default:
                return null;
        }
    }

    // This gets the amount of spaces between the
    private String getSpaces(String base) {
        String spaces = "";
        int amount = 16 - base.length();
        for (int i = 0; i < amount; i ++)
            spaces += " ";
        return spaces;
    }
}
