package com.inoculates.fatesreprise.Storage;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Items.GreatHollowSmallKey;
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
    public int dungeon = UNINITIALIZED;
    public int health = UNINITIALIZED;
    public int maxHealth = UNINITIALIZED;
    public int heartPieces = UNINITIALIZED;
    public int talk = UNINITIALIZED;

    public int moveRight = UNINITIALIZED;
    public int moveLeft = UNINITIALIZED;
    public int moveUp = UNINITIALIZED;
    public int moveDown = UNINITIALIZED;
    public int slotOne = UNINITIALIZED;
    public int slotTwo = UNINITIALIZED;
    public int slotThree = UNINITIALIZED;
    public int pause = UNINITIALIZED;
    public int secondary = UNINITIALIZED;

    public int cellX = UNINITIALIZED;
    public int cellY = UNINITIALIZED;

    // Dialogue stages (depending on how far Daur is in the game, sets the dialogue).
    public int[] villagerStages = new int[27];
    public int fairyQueenStage = 0;
    // How far the main quest has progressed.
    public int mainQuestStage = 0;
    // How many keys Daur has for the respective dungeons.
    public int GHKeys = 0;
    // Which sages Daur has rescued.
    public boolean[] sages = {false, false, false, false, false, false};
    // Which minibosses Daur has defeated.
    public boolean[] minibosses = {false, false, false, false, false, false};
    // Which heart pieces Daur has obtained.
    public boolean[] heartPiecesObtained = {false, false, false, false, false, false, false, false, false, false};
    // Which chests have been opened.
    public boolean[] chests = {false, false, false, false, false, false, false, false, false, false, false, false, false,
    false, false, false, false, false, false, false, false, false, false};
    // Which locked doors have been opened.
    public boolean[] lockedDoors = {false, false, false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false};

    // Items Daur has as well as quest items.
    public ArrayList<Item> items = new ArrayList<Item>();
    public ArrayList<Item> questItems = new ArrayList<Item>();
    // Items Daur can use currently. These change often.
    public Item item1, item2, item3;

    public GameScreen mainScreen;
    public TiledMap map;
    // The short-term storages.
    public FirstDungeonStorage FDstorage;

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
        if (secondary == UNINITIALIZED)
            secondary = Input.Keys.X;
        // Daur starts with zero coins.
        if (coins == UNINITIALIZED)
            coins = 0;
        // Daur starts with 6 health.
        if (health == UNINITIALIZED)
            health = 6;
        // Daur has a max health of 6 at the beginning of the game.
        if (maxHealth == UNINITIALIZED)
            maxHealth = 6;
        if (heartPieces == UNINITIALIZED)
            heartPieces = 0;
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
        // Adds all null quest items, as Daur does not start with any.
        if (questItems.size() == 0)
            for (int i = 0; i < 45; i ++)
                questItems.add(null);
    }

    public void setMainScreen(GameScreen screen) {
        mainScreen = screen;
        // Initializes the short-term storages.
        FDstorage = new FirstDungeonStorage(mainScreen, this);
    }

    // Sets the cell so that the game can persistently know which cell Daur is in.
    public void setCells(int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
    }

    // Increments the fairy queen stage, for dialogue and story related purposes.
    public void setFairyQueenStage() {
        fairyQueenStage ++;
    }

    // Increments the main quest stage, for dialogue and story related purposes.
    public void setMainQuestStage() {
        mainQuestStage ++;
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

    // This tells the save data that the corresponding chest has been opened.
    public void setChest(int number) {
        chests[number] = true;
    }

    public void setSage(int i) {
        sages[i] = true;
    }

    public void setMinibosses(int i) {
        minibosses[i] = true;
    }

    public void setHeartPiece(int i) {
        heartPiecesObtained[i] = true;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void incHeartPiece() {
        heartPieces ++;
    }

    // Increments the key based on the item type given.
    public void addKey(Item key) {
        if (key instanceof GreatHollowSmallKey)
            GHKeys ++;
    }

    // Adds the compass to the proper dungeon so that when Daur uses his map he can see all the chests that could spawn,
    // as well as where the boss/mini-boss is located.
    public void addCompass() {
        // The Great Hollow Dungeon.
        switch (dungeon) {
            case 0:
                FDstorage.addCompass();
                break;
        }
    }

    // Same but for map. In this case, all areas in the dungeon are illuminated to the player.
    public void addMap() {
        // The Great Hollow Dungeon.
        switch (dungeon) {
            case 0:
                FDstorage.addMap();
                break;
        }
    }

    // Returns true if Daur has the compass for the dungeon he is currently in.
    public boolean hasCompass() {
        switch (dungeon) {
            default:
                return false;
            case 0:
                return FDstorage.compass;
        }
    }

    // Returns true if Daur has the map for the dungeon he is currently in.
    public boolean hasMap() {
        switch (dungeon) {
            default:
                return false;
            case 0:
                return FDstorage.map;
        }
    }

    // Removes the key based on the dungeon Daur is in currently.
    public void removeKey() {
        switch (dungeon) {
            case 0:
                GHKeys --;
                break;
        }
    }

    // Unlocks the door (sets the door boolean value to false).
    public void setUnlocked(int dNum) {
        lockedDoors[dNum] = true;
    }

    // Informs the game which dungeon Daur is in currently.
    public void setDungeon(int dungeon) {
        this.dungeon = dungeon;
    }

    public void fillHeart() {
        heartPieces = 0;
        maxHealth += 2;
    }

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
