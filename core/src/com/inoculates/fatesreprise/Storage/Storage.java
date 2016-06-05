package com.inoculates.fatesreprise.Storage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;

import java.lang.reflect.Constructor;
import java.util.*;

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
    // The respawn X and Y of the respawn point. Used to set Daur's beginning position.
    public float respawnX, respawnY;
    // Boss doors of the respective dungeons (opened or closed).
    public boolean[] bossDoors;
    // Maps of the respective dungeons (obtained or not obtained).
    public boolean[] maps;
    // Compasses of the respective dungeons (obtained or not obtained).
    public boolean[] compasses;
    // Which sages Daur has rescued.
    public boolean[] sages;
    // Which heart pieces Daur has obtained.
    public boolean[] heartPiecesObtained;
    // Which chests have been opened.
    public boolean[] chests;
    // Which locked doors have been opened.
    public boolean[] lockedDoors;
    // Which areas Daur has explored in the Overworld.
    public boolean[][] explored;
    // The hashmap of sounds that are played throughout the game.
    public java.util.Map<String, Sound> sounds;
    // The hashmap of music that is played throughout the game.
    public java.util.Map<String, Music> music;

    // Items Daur has as well as quest items.
    public ArrayList<Item> items = new ArrayList<Item>();
    public ArrayList<Item> questItems = new ArrayList<Item>();
    // Items Daur can use currently. These change often.
    public Item item1, item2, item3;

    public GameScreen mainScreen;
    // The map on which the game is currently in (overworld, underworld, or houses).
    public TiledMap map;
    // The short-term storages.
    public FirstDungeonStorage FDstorage;
    // The preferences that keeps consistent data of the storage variables.
    private Preferences prefs;

    public Storage() {
        // Sets the preferences, if there is a valid one. If there isn't a valid one, creates one anew.
        prefs = Gdx.app.getPreferences("Fate's Reprise");
        // Initializes all variables. These variables will be overwritten if there is game data to be loaded.
        setUninitialized();
    }

    // Gets all the variables stored in the preferences, should they exist.
    public boolean loadVariables() {
        // If the preference does not have storage of the first variable, that means that the preference is not pre-existing.
        // Therefore, it is useless to attempt to load any data.
        if (!prefs.contains("coins"))
            return false;
        // If the storage is being utilized by the beginning input, returns true to avoid going further down in the method.
        // This is to avoid any null pointer exceptions and avoid useless loading (as the game screen will load the
        // game anyway).
        if (mainScreen == null)
            return true;
        // Otherwise sets the variable values to the storage data.
        // Gets all the necessary integer values that tell the state of the game.
        coins = prefs.getInteger("coins");
        dungeon = prefs.getInteger("dungeon");
        health = prefs.getInteger("health");
        maxHealth = prefs.getInteger("maxhealth");
        heartPieces = prefs.getInteger("heartpieces");
        // Integer values that hold the keys for Daur.
        talk = prefs.getInteger("talk");
        moveUp = prefs.getInteger("moveup");
        moveRight = prefs.getInteger("moveright");
        moveLeft = prefs.getInteger("moveleft");
        moveDown = prefs.getInteger("movedown");
        slotOne = prefs.getInteger("slotone");
        slotTwo = prefs.getInteger("slottwo");
        slotThree = prefs.getInteger("slotthree");
        pause = prefs.getInteger("pause");
        secondary = prefs.getInteger("secondary");
        // The two cell values of the game.
        cellX = prefs.getInteger("cellX");
        cellY = prefs.getInteger("cellY");
        // Main Quest stage and the Fairy Queen stage.
        fairyQueenStage = prefs.getInteger("fairyqueenstage");
        mainQuestStage = prefs.getInteger("mainqueststage");
        // How many keys Daur has of each dungeon.
        GHKeys = prefs.getInteger("GHKeys");
        // The villager stages of the game.
        for (int i = 0; i < villagerStages.length; i ++)
            villagerStages[i] = prefs.getInteger("villagerstages" + i);
        // The boolean arrays that determine if triggers launch.
        for (int i = 0; i < bossDoors.length; i ++)
            bossDoors[i] = prefs.getBoolean("bossdoors" + i);
        for (int i = 0; i < sages.length; i ++)
            sages[i] = prefs.getBoolean("sages" + i);
        for (int i = 0; i < heartPiecesObtained.length; i ++)
            heartPiecesObtained[i] = prefs.getBoolean("heartpiecesobtained" + i);
        for (int i = 0; i < chests.length; i ++)
            chests[i] = prefs.getBoolean("chests" + i);
        for (int i = 0; i < lockedDoors.length; i ++)
            lockedDoors[i] = prefs.getBoolean("lockeddoors" + i);
        for (int i = 0; i < maps.length; i ++)
            maps[i] = prefs.getBoolean("maps" + i);
        for (int i = 0; i < compasses.length; i ++)
            compasses[i] = prefs.getBoolean("compasses" + i);
        for (int x = 0; x < 16; x ++)
            for (int y = 0; y < 16; y ++)
                explored[x][y] = prefs.getBoolean("explored" + x + "," + y);
        // Uses the classname as the string to generate the item when loading a game. getItem method details more.
        for (int i = 44; i >= 0; i --)
            items.add(0, getItem(prefs.getString("item" + i)));
        for (int i = 44; i >= 0; i --)
            questItems.add(0, getItem(prefs.getString("questitem" + i)));
        // Gets the items Daur has in the first, second, and third slot.
        item1 = getItem(prefs.getString("itemone"));
        item2 = getItem(prefs.getString("itemtwo"));
        item3 = getItem(prefs.getString("itemthree"));
        // Gets the map for the game.
        switch (prefs.getInteger("map")) {
            case 0:
                map = mainScreen.world1.getMap();
                break;
            case 1:
                map = mainScreen.world2.getMap();
                break;
            case 2:
                map = mainScreen.world3.getMap();
                break;
        }
        respawnX = prefs.getFloat("respawnX");
        respawnY = prefs.getFloat("respawnY");
        return true;
    }

    // Sets all the variables to their unmodified state.
    private void setUninitialized() {
        // Creates all the base keys for movement and such.
        moveRight = Input.Keys.D;
        moveLeft = Input.Keys.A;
        moveUp = Input.Keys.W;
        moveDown = Input.Keys.S;
        talk = Input.Keys.F;
        slotOne = Input.Keys.J;
        slotTwo = Input.Keys.K;
        slotThree = Input.Keys.L;
        pause = Input.Keys.Z;
        secondary = Input.Keys.X;
        // Daur starts with zero coins.
        coins = 0;
        // Daur starts with 6 health.
        health = 6;
        // Daur has a max health of 6 at the beginning of the game.
        maxHealth = 6;
        heartPieces = 0;
        // Initial cell of the game.
        cellX = 0;
        cellY = 15;
        // Initializes all villager stages to zero.
        for (int i = 0; i < villagerStages.length; i++)
            villagerStages[i] = 0;
        // Adds all null items, as Daur does not start with any.
        for (int i = 0; i < 45; i++)
            items.add(null);
        // Adds all null quest items, as Daur does not start with any.
        for (int i = 0; i < 45; i++)
            questItems.add(null);
        // Sets the three item slots to null.
        item1 = null;
        item2 = null;
        item3 = null;
        // Dialogue stages (depending on how far Daur is in the game, sets the dialogue). There are 27 villagers.
        villagerStages = new int[27];
        // How far the Fairy Queen's events have progressed. Starts at zero.
        fairyQueenStage = 0;
        // How far the main quest has progressed. Starts at zero.
        mainQuestStage = 0;
        // How many keys Daur has for the respective dungeons. Starts at zero.
        GHKeys = 0;
        // The respawn X and Y of the respawn point. Used to set Daur's beginning position. Initializes to his starting
        // position.
        respawnX = 1;
        respawnY = 16;
        // Boss doors of the respective dungeons (opened or closed). All are initially closed.
        bossDoors = new boolean[] {false, false, false, false, false, false};
        // Which sages Daur has rescued.
        sages = new boolean[] {false, false, false, false, false, false};
        // Maps and compasses.
        maps = new boolean[] {false, false, false, false, false, false};
        compasses = new boolean[] {false, false, false, false, false, false};
        // Which heart pieces Daur has obtained.
        heartPiecesObtained = new boolean[] {false, false, false, false, false, false, false, false, false, false};
        // Which chests have been opened.
        chests = new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false};
        // Which locked doors have been opened.
        lockedDoors = new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false};
        // Which areas have been explored.
        explored = new boolean[16][16];
        for (int x = 0; x < 16; x ++)
            for (int y = 0; y < 16; y ++)
                explored[x][y] = false;
        // The hashmap of sounds.
        sounds = new HashMap<String, Sound>();
        // Adds all the sounds to the hashmap for easy reference.
        sounds.put("bigboom", Gdx.audio.newSound(Gdx.files.internal("Sounds/bigboom.wav")));
        sounds.put("bigbounce", Gdx.audio.newSound(Gdx.files.internal("Sounds/bigbounce.wav")));
        sounds.put("bigeffect", Gdx.audio.newSound(Gdx.files.internal("Sounds/bigeffect.wav")));
        sounds.put("boom1", Gdx.audio.newSound(Gdx.files.internal("Sounds/boom1.wav")));
        sounds.put("boom2", Gdx.audio.newSound(Gdx.files.internal("Sounds/boom2.wav")));
        sounds.put("boom3", Gdx.audio.newSound(Gdx.files.internal("Sounds/boom3.wav")));
        sounds.put("bossdeath", Gdx.audio.newSound(Gdx.files.internal("Sounds/bossdeath.wav")));
        sounds.put("bosshurt", Gdx.audio.newSound(Gdx.files.internal("Sounds/bosshurt.wav")));
        sounds.put("bossminiexplosion", Gdx.audio.newSound(Gdx.files.internal("Sounds/bossminiexplosion.wav")));
        sounds.put("bounce", Gdx.audio.newSound(Gdx.files.internal("Sounds/bounce.wav")));
        sounds.put("bushcut1", Gdx.audio.newSound(Gdx.files.internal("Sounds/bushcut1.wav")));
        sounds.put("bushcut2", Gdx.audio.newSound(Gdx.files.internal("Sounds/bushcut2.wav")));
        sounds.put("charge", Gdx.audio.newSound(Gdx.files.internal("Sounds/charge.wav")));
        sounds.put("chargeup", Gdx.audio.newSound(Gdx.files.internal("Sounds/chargeup.wav")));
        sounds.put("chestappear", Gdx.audio.newSound(Gdx.files.internal("Sounds/chestappear.wav")));
        sounds.put("chestopen", Gdx.audio.newSound(Gdx.files.internal("Sounds/chestopen.wav")));
        sounds.put("click1", Gdx.audio.newSound(Gdx.files.internal("Sounds/click1.wav")));
        sounds.put("click2", Gdx.audio.newSound(Gdx.files.internal("Sounds/click2.wav")));
        sounds.put("click3", Gdx.audio.newSound(Gdx.files.internal("Sounds/click3.wav")));
        sounds.put("click4", Gdx.audio.newSound(Gdx.files.internal("Sounds/click4.wav")));
        sounds.put("click5", Gdx.audio.newSound(Gdx.files.internal("Sounds/click5.wav")));
        sounds.put("click6", Gdx.audio.newSound(Gdx.files.internal("Sounds/click6.wav")));
        sounds.put("click7", Gdx.audio.newSound(Gdx.files.internal("Sounds/click7.wav")));
        sounds.put("click8", Gdx.audio.newSound(Gdx.files.internal("Sounds/click8.wav")));
        sounds.put("click9", Gdx.audio.newSound(Gdx.files.internal("Sounds/click9.wav")));
        sounds.put("click10", Gdx.audio.newSound(Gdx.files.internal("Sounds/click10.wav")));
        sounds.put("coin1", Gdx.audio.newSound(Gdx.files.internal("Sounds/coin1.wav")));
        sounds.put("coin2", Gdx.audio.newSound(Gdx.files.internal("Sounds/coin2.wav")));
        sounds.put("coin3", Gdx.audio.newSound(Gdx.files.internal("Sounds/coin3.wav")));
        sounds.put("confuse", Gdx.audio.newSound(Gdx.files.internal("Sounds/confuse.wav")));
        sounds.put("daurhurt", Gdx.audio.newSound(Gdx.files.internal("Sounds/daurhurt.wav")));
        sounds.put("death1", Gdx.audio.newSound(Gdx.files.internal("Sounds/death1.wav")));
        sounds.put("death2", Gdx.audio.newSound(Gdx.files.internal("Sounds/death2.wav")));
        sounds.put("defeat1", Gdx.audio.newSound(Gdx.files.internal("Sounds/defeat1.wav")));
        sounds.put("defeat2", Gdx.audio.newSound(Gdx.files.internal("Sounds/defeat2.wav")));
        sounds.put("defeat3", Gdx.audio.newSound(Gdx.files.internal("Sounds/defeat3.wav")));
        sounds.put("dooropen", Gdx.audio.newSound(Gdx.files.internal("Sounds/dooropen.wav")));
        sounds.put("doorslam1", Gdx.audio.newSound(Gdx.files.internal("Sounds/doorslam1.wav")));
        sounds.put("doorslam2", Gdx.audio.newSound(Gdx.files.internal("Sounds/doorslam2.wav")));
        sounds.put("drown", Gdx.audio.newSound(Gdx.files.internal("Sounds/drown.wav")));
        sounds.put("dungeonopen", Gdx.audio.newSound(Gdx.files.internal("Sounds/dungeonopen.wav")));
        sounds.put("effect1", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect1.wav")));
        sounds.put("effect2", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect2.wav")));
        sounds.put("effect3", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect3.wav")));
        sounds.put("effect4", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect4.wav")));
        sounds.put("effect5", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect5.wav")));
        sounds.put("effect6", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect6.wav")));
        sounds.put("effect7", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect7.wav")));
        sounds.put("effect8", Gdx.audio.newSound(Gdx.files.internal("Sounds/effect8.wav")));
        sounds.put("electricity", Gdx.audio.newSound(Gdx.files.internal("Sounds/electricity.wav")));
        sounds.put("enterstairs", Gdx.audio.newSound(Gdx.files.internal("Sounds/enterstairs.wav")));
        sounds.put("error", Gdx.audio.newSound(Gdx.files.internal("Sounds/error.wav")));
        sounds.put("explode1", Gdx.audio.newSound(Gdx.files.internal("Sounds/explode1.wav")));
        sounds.put("explode2", Gdx.audio.newSound(Gdx.files.internal("Sounds/explode2.wav")));
        sounds.put("explode3", Gdx.audio.newSound(Gdx.files.internal("Sounds/explode3.wav")));
        sounds.put("explode4", Gdx.audio.newSound(Gdx.files.internal("Sounds/explode4.wav")));
        sounds.put("explode5", Gdx.audio.newSound(Gdx.files.internal("Sounds/explode5.wav")));
        sounds.put("fairyappear", Gdx.audio.newSound(Gdx.files.internal("Sounds/fairyappear.wav")));
        sounds.put("fall1", Gdx.audio.newSound(Gdx.files.internal("Sounds/fall1.wav")));
        sounds.put("fall2", Gdx.audio.newSound(Gdx.files.internal("Sounds/fall2.wav")));
        sounds.put("fly1", Gdx.audio.newSound(Gdx.files.internal("Sounds/fly1.wav")));
        sounds.put("fly2", Gdx.audio.newSound(Gdx.files.internal("Sounds/fly2.wav")));
        sounds.put("grasswalk", Gdx.audio.newSound(Gdx.files.internal("Sounds/grasswalk.wav")));
        sounds.put("heart", Gdx.audio.newSound(Gdx.files.internal("Sounds/heart.wav")));
        sounds.put("hop", Gdx.audio.newSound(Gdx.files.internal("Sounds/hop.wav")));
        sounds.put("hurt1", Gdx.audio.newSound(Gdx.files.internal("Sounds/hurt1.wav")));
        sounds.put("hurt2", Gdx.audio.newSound(Gdx.files.internal("Sounds/hurt2.wav")));
        sounds.put("jellyfishattack1", Gdx.audio.newSound(Gdx.files.internal("Sounds/jellyfishattack1.wav")));
        sounds.put("jellyfishattack2", Gdx.audio.newSound(Gdx.files.internal("Sounds/jellyfishattack2.wav")));
        sounds.put("jump", Gdx.audio.newSound(Gdx.files.internal("Sounds/jump.wav")));
        sounds.put("landing", Gdx.audio.newSound(Gdx.files.internal("Sounds/landing.wav")));
        sounds.put("launch1", Gdx.audio.newSound(Gdx.files.internal("Sounds/launch1.wav")));
        sounds.put("launch2", Gdx.audio.newSound(Gdx.files.internal("Sounds/launch2.wav")));
        sounds.put("launch3", Gdx.audio.newSound(Gdx.files.internal("Sounds/launch3.wav")));
        sounds.put("launch4", Gdx.audio.newSound(Gdx.files.internal("Sounds/launch4.wav")));
        sounds.put("launch5", Gdx.audio.newSound(Gdx.files.internal("Sounds/launch5.wav")));
        sounds.put("launch6", Gdx.audio.newSound(Gdx.files.internal("Sounds/launch6.wav")));
        sounds.put("lose1", Gdx.audio.newSound(Gdx.files.internal("Sounds/lose1.wav")));
        sounds.put("lose2", Gdx.audio.newSound(Gdx.files.internal("Sounds/lose2.wav")));
        sounds.put("lose3", Gdx.audio.newSound(Gdx.files.internal("Sounds/lose3.wav")));
        sounds.put("lose4", Gdx.audio.newSound(Gdx.files.internal("Sounds/lose4.wav")));
        sounds.put("mysterious", Gdx.audio.newSound(Gdx.files.internal("Sounds/mysterious.wav")));
        sounds.put("powerup1", Gdx.audio.newSound(Gdx.files.internal("Sounds/powerup1.wav")));
        sounds.put("powerup2", Gdx.audio.newSound(Gdx.files.internal("Sounds/powerup2.wav")));
        sounds.put("powerup3", Gdx.audio.newSound(Gdx.files.internal("Sounds/powerup3.wav")));
        sounds.put("powerup4", Gdx.audio.newSound(Gdx.files.internal("Sounds/powerup4.wav")));
        sounds.put("powerup5", Gdx.audio.newSound(Gdx.files.internal("Sounds/powerup5.wav")));
        sounds.put("powerup6", Gdx.audio.newSound(Gdx.files.internal("Sounds/powerup6.wav")));
        sounds.put("pulse", Gdx.audio.newSound(Gdx.files.internal("Sounds/pulse.wav")));
        sounds.put("push", Gdx.audio.newSound(Gdx.files.internal("Sounds/push.wav")));
        sounds.put("refill", Gdx.audio.newSound(Gdx.files.internal("Sounds/refill.wav")));
        sounds.put("shake", Gdx.audio.newSound(Gdx.files.internal("Sounds/shake.wav")));
        sounds.put("sickle", Gdx.audio.newSound(Gdx.files.internal("Sounds/sickle.wav")));
        sounds.put("success1", Gdx.audio.newSound(Gdx.files.internal("Sounds/success1.wav")));
        sounds.put("success2", Gdx.audio.newSound(Gdx.files.internal("Sounds/success2.wav")));
        sounds.put("swim", Gdx.audio.newSound(Gdx.files.internal("Sounds/swim.wav")));
        sounds.put("swordslash1", Gdx.audio.newSound(Gdx.files.internal("Sounds/swordslash1.wav")));
        sounds.put("swordslash2", Gdx.audio.newSound(Gdx.files.internal("Sounds/swordslash2.wav")));
        sounds.put("swordslash3", Gdx.audio.newSound(Gdx.files.internal("Sounds/swordslash3.wav")));
        sounds.put("swordslash4", Gdx.audio.newSound(Gdx.files.internal("Sounds/swordslash4.wav")));
        sounds.put("teleport", Gdx.audio.newSound(Gdx.files.internal("Sounds/teleport.wav")));
        sounds.put("textend", Gdx.audio.newSound(Gdx.files.internal("Sounds/textend.wav")));
        sounds.put("textletter", Gdx.audio.newSound(Gdx.files.internal("Sounds/textletter.wav")));
        sounds.put("throw", Gdx.audio.newSound(Gdx.files.internal("Sounds/throw.wav")));
        sounds.put("tremor1", Gdx.audio.newSound(Gdx.files.internal("Sounds/tremor1.wav")));
        sounds.put("tremor2", Gdx.audio.newSound(Gdx.files.internal("Sounds/tremor2.wav")));
        sounds.put("tremor3", Gdx.audio.newSound(Gdx.files.internal("Sounds/tremor3.wav")));
        sounds.put("unlock", Gdx.audio.newSound(Gdx.files.internal("Sounds/unlock.wav")));
        sounds.put("wade1", Gdx.audio.newSound(Gdx.files.internal("Sounds/wade1.wav")));
        sounds.put("wade2", Gdx.audio.newSound(Gdx.files.internal("Sounds/wade2.wav")));
        sounds.put("watercharge", Gdx.audio.newSound(Gdx.files.internal("Sounds/watercharge.wav")));
        sounds.put("zap1", Gdx.audio.newSound(Gdx.files.internal("Sounds/zap1.wav")));
        sounds.put("zap2", Gdx.audio.newSound(Gdx.files.internal("Sounds/zap2.wav")));
        sounds.put("zap3", Gdx.audio.newSound(Gdx.files.internal("Sounds/zap3.wav")));
        sounds.put("zap4", Gdx.audio.newSound(Gdx.files.internal("Sounds/zap4.wav")));
        // Adds all the music to the music hashmap.
        music = new HashMap<String, Music>();
        // Adds all the music fo easy hashmap reference.
        music.put("bossmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/Boss.mp3")));
        music.put("bossvictorymusic", Gdx.audio.newMusic(Gdx.files.internal("Music/BossVictory.mp3")));
        music.put("carthellvillagemusic", Gdx.audio.newMusic(Gdx.files.internal("Music/CarthellVillage.mp3")));
        music.put("cavemusic", Gdx.audio.newMusic(Gdx.files.internal("Music/Cave.mp3")));
        music.put("creepymusic", Gdx.audio.newMusic(Gdx.files.internal("Music/CreepyMusic.mp3")));
        music.put("fairyfountainmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/FairyFountain.mp3")));
        music.put("forestmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/ForestMusic.mp3")));
        music.put("forestmusicloop", Gdx.audio.newMusic(Gdx.files.internal("Music/ForestMusicLoop.mp3")));
        music.put("gameover", Gdx.audio.newMusic(Gdx.files.internal("Music/GameOver.mp3")));
        music.put("greathollowmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/GreatHollow.mp3")));
        music.put("housemusic", Gdx.audio.newMusic(Gdx.files.internal("Music/HouseMusic.mp3")));
        music.put("minibossmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/Miniboss.mp3")));
        music.put("mysteriousmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/MysteriousMusic.mp3")));
        music.put("mysteriousmusicloop", Gdx.audio.newMusic(Gdx.files.internal("Music/MysteriousMusicLoop.mp3")));
        music.put("overworldtheme", Gdx.audio.newMusic(Gdx.files.internal("Music/OverworldTheme.mp3")));
        music.put("sagemeetingmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/SageMeeting.mp3")));
        music.put("shopmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/Shop.mp3")));
        music.put("strangemusic", Gdx.audio.newMusic(Gdx.files.internal("Music/StrangeMusic.mp3")));
        music.put("titlescreenmusic", Gdx.audio.newMusic(Gdx.files.internal("Music/TitleScreen.mp3")));
        music.put("titlescreenmusicloop", Gdx.audio.newMusic(Gdx.files.internal("Music/TitleScreenLoop.mp3")));
        music.put("trendymusic", Gdx.audio.newMusic(Gdx.files.internal("Music/TrendyMusic.mp3")));
        music.put("victorymusic", Gdx.audio.newMusic(Gdx.files.internal("Music/Victory.mp3")));
    }

    // Reinitializes the preferences and all the variables. This is to create a new, fresh game without any previous
    // variables lurking around.
    public void wipe() {
        // Sets all variables to uninitialized.
        setUninitialized();
    }

    // Stores all variables under string keys. This is to allow for future loading.
    public void store() {
        // Puts all the necessary integer values that tell the state of the game.
        prefs.putInteger("coins", coins);
        prefs.putInteger("dungeon", dungeon);
        prefs.putInteger("health", health);
        prefs.putInteger("maxhealth", maxHealth);
        prefs.putInteger("heartpieces", heartPieces);
        // Integer values that hold the keys for Daur.
        prefs.putInteger("talk", talk);
        prefs.putInteger("moveup", moveUp);
        prefs.putInteger("moveright", moveRight);
        prefs.putInteger("moveleft", moveLeft);
        prefs.putInteger("movedown", moveDown);
        prefs.putInteger("slotone", slotOne);
        prefs.putInteger("slottwo", slotTwo);
        prefs.putInteger("slotthree", slotThree);
        prefs.putInteger("pause", pause);
        prefs.putInteger("secondary", secondary);
        // The two cell values of the game.
        prefs.putInteger("cellX", cellX);
        prefs.putInteger("cellY", cellY);
        // Main Quest stage and the Fairy Queen stage.
        prefs.putInteger("fairyqueenstage", fairyQueenStage);
        prefs.putInteger("mainqueststage", mainQuestStage);
        // How many keys Daur has of each dungeon.
        prefs.putInteger("GHKeys", GHKeys);
        // The villager stages of the game.
        for (int i = 0; i < villagerStages.length; i ++)
            prefs.putInteger("villagerstages" + i, villagerStages[i]);
        // The boolean arrays that determine if triggers launch.
        for (int i = 0; i < bossDoors.length; i ++)
            prefs.putBoolean("bossdoors" + i, bossDoors[i]);
        for (int i = 0; i < sages.length; i ++)
            prefs.putBoolean("sages" + i, sages[i]);
        for (int i = 0; i < heartPiecesObtained.length; i ++)
            prefs.putBoolean("heartpiecesobtained" + i, heartPiecesObtained[i]);
        for (int i = 0; i < chests.length; i ++)
            prefs.putBoolean("chests" + i, chests[i]);
        for (int i = 0; i < lockedDoors.length; i ++)
            prefs.putBoolean("lockeddoors" + i, lockedDoors[i]);
        for (int i = 0; i < maps.length; i ++)
            prefs.putBoolean("maps" + i, maps[i]);
        for (int i = 0; i < compasses.length; i ++)
            prefs.putBoolean("compasses" + i, compasses[i]);
        for (int x = 0; x < 16; x ++)
            for (int y = 0; y < 16; y ++)
                prefs.putBoolean("explored" + x + "," + y, explored[x][y]);
        // Puts all the items in a string list.
        // Puts the classname as the string to generate the item when loading a game. If there is no such item, simply
        // puts a null string.
        for (int i = 0; i < items.size(); i ++) {
            if (items.get(i) != null)
                prefs.putString("item" + i, items.get(i).getClass().toString());
            else
                prefs.putString("item" + i, "null");
        }
        for (int i = 0; i < questItems.size(); i ++) {
            if (questItems.get(i) != null)
                prefs.putString("questitem" + i, questItems.get(i).getClass().toString());
            else
                prefs.putString("questitem" + i, "null");
        }
        // Puts the items Daur has in the first, second, and third slot.
        if (item1 != null)
            prefs.putString("itemone", item1.getClass().toString());
        else
            prefs.putString("itemone", "null");
        if (item2 != null)
            prefs.putString("itemtwo", item2.getClass().toString());
        else
            prefs.putString("itemtwo", "null");
        if (item3 != null)
            prefs.putString("itemthree", item3.getClass().toString());
        else
            prefs.putString("itemthree", "null");
        // Saves the map in the form of 0 (overworld), 1 (underworld), or 2 (houses).
        if (map.equals(mainScreen.world1.getMap()))
            prefs.putInteger("map", 0);
        if (map.equals(mainScreen.world2.getMap()))
            prefs.putInteger("map", 1);
        if (map.equals(mainScreen.world3.getMap()))
            prefs.putInteger("map", 2);
        // Puts the two respawn floats.
        prefs.putFloat("respawnX", respawnX);
        prefs.putFloat("respawnY", respawnY);
        // Saves all the preferences put.
        prefs.flush();
    }

    // Returns an item based on the class name given. This is useful for loading items.
    private Item getItem(String string) {
        // If there is a null string, returns null.
        if (string.equals("null"))
            return null;
        // Basic Sword Item.
        if (string.equals("class com.inoculates.fatesreprise.Items.BasicSwordItem"))
            return new BasicSwordItem(mainScreen.daurAtlases.get(2));
        // Concussive Shot Item.
        if (string.equals("class com.inoculates.fatesreprise.Items.ConcussiveShotItem"))
            return new ConcussiveShotItem(mainScreen.daurAtlases.get(2));
        // Etc.
        if (string.equals("class com.inoculates.fatesreprise.Items.FlameGoutItem"))
            return new FlameGoutItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.GiantsMightItem"))
            return new GiantsMightItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.GreatHollowBossKey"))
            return new GreatHollowBossKey(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.GreatHollowKey"))
            return new GreatHollowKey(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.LiveWireItem"))
            return new LiveWireItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.MinorHealthPotionItem"))
            return new MinorHealthPotionItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.MinorTeleportItem"))
            return new MinorTeleportItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.MirrorImageItem"))
            return new MirrorImageItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.OakStaff"))
            return new OakStaff(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.PlanarShiftItem"))
            return new PlanarShiftItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.PulverizeItem"))
            return new PulverizeItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.ReflectItem"))
            return new ReflectItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.ShieldItem"))
            return new ShieldItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.StoneFistItem"))
            return new StoneFistItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.SwimGear"))
            return new SwimGear(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.WindSickleItem"))
            return new WindSickleItem(mainScreen.daurAtlases.get(2));
        if (string.equals("class com.inoculates.fatesreprise.Items.ZephyrsWispItem"))
            return new ZephyrsWispItem(mainScreen.daurAtlases.get(2));
        return null;
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

    // This increases or decreases the level Daur is currently on.
    public void setLevel(int magnitude) {
        switch (dungeon) {
            default:
                FDstorage.setLevel(magnitude);
                break;
            case 0:
                FDstorage.setLevel(magnitude);
                break;
        }
    }

    // This tells the save data that the corresponding chest has been opened.
    public void setChest(int number) {
        chests[number] = true;
    }

    public void setSage(int i) {
        sages[i] = true;
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
        compasses[dungeon] = true;
    }

    // Same but for map. In this case, all areas in the dungeon are illuminated to the player.
    public void addMap() {
        maps[dungeon] = true;
    }

    // Returns true if Daur has the compass for the dungeon he is currently in.
    public boolean hasCompass() {
        return compasses[dungeon];
    }

    // Returns true if Daur has the map for the dungeon he is currently in.
    public boolean hasMap() {
        return dungeon >= 0 && maps[dungeon];
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

    // Unlocks the boss door (with the same reasoning as above).
    public void setBossUnlocked() {
        bossDoors[dungeon] = true;
    }

    // Informs the game which dungeon Daur is in currently.
    public void setDungeon(int dungeon) {
        this.dungeon = dungeon;
    }

    // Sets the volume of the current soundtrack playing.
    public void setVolume(float volume) {
        // First finds the music that is currently playing.
        // If the Boss Music is currently playing.
        if (music.get("bossmusic").isPlaying())
            music.get("bossmusic").setVolume(volume);
        // If the Boss Victory Music is currently playing.
        if (music.get("bossvictorymusic").isPlaying())
            music.get("bossvictorymusic").setVolume(volume);
        // Etc.
        if (music.get("carthellvillagemusic").isPlaying())
            music.get("carthellvillagemusic").setVolume(volume);
        if (music.get("cavemusic").isPlaying())
            music.get("cavemusic").setVolume(volume);
        if (music.get("bossvictorymusic").isPlaying())
            music.get("bossvictorymusic").setVolume(volume);
        if (music.get("fairyfountainmusic").isPlaying())
            music.get("fairyfountainmusic").setVolume(volume);
        if (music.get("forestmusic").isPlaying())
            music.get("forestmusic").setVolume(volume);
        if (music.get("forestmusicloop").isPlaying())
            music.get("forestmusicloop").setVolume(volume);
        if (music.get("greathollowmusic").isPlaying())
            music.get("greathollowmusic").setVolume(volume);
        if (music.get("housemusic").isPlaying())
            music.get("housemusic").setVolume(volume);
        if (music.get("minibossmusic").isPlaying())
            music.get("minibossmusic").setVolume(volume);
        if (music.get("mysteriousmusic").isPlaying())
            music.get("mysteriousmusic").setVolume(volume);
        if (music.get("mysteriousmusicloop").isPlaying())
            music.get("mysteriousmusicloop").setVolume(volume);
        if (music.get("overworldtheme").isPlaying())
            music.get("overworldtheme").setVolume(volume);
        if (music.get("shopmusic").isPlaying())
            music.get("shopmusic").setVolume(volume);
        if (music.get("strangemusic").isPlaying())
            music.get("strangemusic").setVolume(volume);
        if (music.get("titlescreenmusic").isPlaying())
            music.get("titlescreenmusic").setVolume(volume);
        if (music.get("titlescreenmusicloop").isPlaying())
            music.get("titlescreenmusicloop").setVolume(volume);
        if (music.get("trendymusic").isPlaying())
            music.get("trendymusic").setVolume(volume);
    }

    // Same as the above method, but stops music instead of setting its volume.
    public void stopMusic() {
        // First finds the music that is currently playing.
        // If the Boss Music is currently playing.
        if (music.get("bossmusic").isPlaying())
            music.get("bossmusic").stop();
        // If the Boss Victory Music is currently playing.
        if (music.get("bossvictorymusic").isPlaying())
            music.get("bossvictorymusic").stop();
        // Etc.
        if (music.get("carthellvillagemusic").isPlaying())
            music.get("carthellvillagemusic").stop();
        if (music.get("cavemusic").isPlaying())
            music.get("cavemusic").stop();
        if (music.get("bossvictorymusic").isPlaying())
            music.get("bossvictorymusic").stop();
        if (music.get("fairyfountainmusic").isPlaying())
            music.get("fairyfountainmusic").stop();
        if (music.get("forestmusic").isPlaying())
            music.get("forestmusic").stop();
        if (music.get("forestmusicloop").isPlaying())
            music.get("forestmusicloop").stop();
        if (music.get("greathollowmusic").isPlaying())
            music.get("greathollowmusic").stop();
        if (music.get("housemusic").isPlaying())
            music.get("housemusic").stop();
        if (music.get("minibossmusic").isPlaying())
            music.get("minibossmusic").stop();
        if (music.get("mysteriousmusic").isPlaying())
            music.get("mysteriousmusic").stop();
        if (music.get("mysteriousmusicloop").isPlaying())
            music.get("mysteriousmusicloop").stop();
        if (music.get("overworldtheme").isPlaying())
            music.get("overworldtheme").stop();
        if (music.get("shopmusic").isPlaying())
            music.get("shopmusic").stop();
        if (music.get("strangemusic").isPlaying())
            music.get("strangemusic").stop();
        if (music.get("titlescreenmusic").isPlaying())
            music.get("titlescreenmusic").stop();
        if (music.get("titlescreenmusicloop").isPlaying())
            music.get("titlescreenmusicloop").stop();
        if (music.get("trendymusic").isPlaying())
            music.get("trendymusic").stop();
    }

    // When a new heart piece is obtained increases health and sets heart pieces to zero.
    public void fillHeart() {
        heartPieces = 0;
        maxHealth += 2;
    }

    // Sets the respawn point.
    public void setRespawnPoint(float x, float y) {
        respawnX = x;
        respawnY = y;
    }

    // Sets the cell to be explored.
    public void setExplored(int x, int y) {
        explored[x][y] = true;
    }

    public void incMaxHealth() {
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
