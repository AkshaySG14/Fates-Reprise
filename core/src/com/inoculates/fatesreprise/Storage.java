package com.inoculates.fatesreprise;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Items.Item;
import com.inoculates.fatesreprise.Screens.GameScreen;

import java.util.ArrayList;

// This is the storage class that carries persistent data throughout games.
public class Storage {
    // Value that tells the storage class something is uninitialized.
    public static final int UNINITIALIZED = -1;

    public int soundM = UNINITIALIZED;
    public int soundE = UNINITIALIZED;
    public int coins = UNINITIALIZED;
    public int health = UNINITIALIZED;
    public int mana = UNINITIALIZED;
    public int talk = UNINITIALIZED;

    public int moveRight = UNINITIALIZED;
    public int moveLeft = UNINITIALIZED;
    public int moveUp = UNINITIALIZED;
    public int moveDown = UNINITIALIZED;
    public int slotOne = UNINITIALIZED;
    public int slotTwo = UNINITIALIZED;
    public int slotThree = UNINITIALIZED;
    public int pause = UNINITIALIZED;

    public int cellX = UNINITIALIZED;
    public int cellY = UNINITIALIZED;

    public int[] villagerStages = new int[27];

    public ArrayList<Item> items = new ArrayList<Item>();
    public Item item1, item2, item3;

    public GameScreen mainScreen;
    public TiledMap map;

    public Storage() {
        // This class constructor initializes all unitialized values to their based values.
        // Creates all the base keys for movement and such.
        if (moveRight == UNINITIALIZED)
            moveRight = Input.Keys.D;
        if (moveLeft == UNINITIALIZED)
            moveLeft = Input.Keys.A;
        if (moveUp == UNINITIALIZED)
            moveUp = Input.Keys.W;
        if (moveDown == UNINITIALIZED)
            moveDown = Input.Keys.S;
        if (talk == UNINITIALIZED)
            talk = Input.Keys.F;
        if (slotOne == UNINITIALIZED)
            slotOne = Input.Keys.J;
        if (slotTwo == UNINITIALIZED)
            slotTwo = Input.Keys.K;
        if (slotThree == UNINITIALIZED)
            slotThree = Input.Keys.L;
        if (pause == UNINITIALIZED)
            pause = Input.Keys.ESCAPE;
        // Daur starts with zero coins.
        if (coins == UNINITIALIZED)
            coins = 000;
        // Daur starts with 6 health.
        if (health == UNINITIALIZED)
            health = 6;
        // Daur starts with 8 mana.
        if (mana == UNINITIALIZED)
            mana = 8;
        // Initial cell of the game.
        if (cellX == UNINITIALIZED)
            cellX = 1;
        if (cellY == UNINITIALIZED)
            cellY = 16;
        // Initializes all villager stages to zero.
        if (villagerStages[0] == 0)
            for (int i = 0; i < villagerStages.length; i ++)
                villagerStages[i] = 0;
        // Adds all null items, as Daur does not start with any.
        if (items.size() == 0)
            for (int i = 0; i < 45; i ++)
                items.add(null);
    }

    public void setMainScreen(GameScreen screen) {
        mainScreen = screen;
    }

    // Sets the cell so that the game can persistently know which cell Daur is in.
    public void setCells(int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
    }

    // Increments the village state, and saves it, so that Daur will always initiate a different dialogue from then on.
    public void setVillagerStage(int villager) {
        for (int i = 0; i < villagerStages.length; i ++)
            if (i == villager)
                villagerStages[i] ++;
    }

    // This sets the village state to a specific value. Useful for regression.
    public void setVillagerStageSp(int villager, int stage) {
        for (int i = 0; i < villagerStages.length; i ++)
            if (i == villager)
                villagerStages[i] = stage;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMana(int mana) { this.mana = mana; }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setItem1(Item item) {
        item1 = item;
    }

    public void setItem2(Item item) {
        item2 = item;
    }

    public void setItem3(Item item) {
        item3 = item;
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }


}
