package com.inoculates.fatesreprise.Interactables;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Events.ChestEvent;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

// This is a chest that can be opened by the player to obtain items..
public abstract class Chest extends Interactable {
    // Whether the chest is open or not. Also whether it is spawning. If the chest is spawning the chest cannot open.
    protected boolean open, spawning = false;
    // The contents of the chest, whether it is an item or gold.
    private String contents;
    private int chestNumber;

    public Chest(GameScreen screen, TiledMap map, TextureAtlas atlas, Storage storage, String contents, float spawnX,
                 float spawnY, int chestNumber) {
        super(screen, map, atlas, storage);
        this.screen = screen;
        this.map = map;
        this.atlas = atlas;
        this.storage = storage;
        this.contents = contents;
        this.chestNumber = chestNumber;
        setPosition(spawnX - getWidth() / 2, spawnY - getHeight() / 2);
        layer = (TiledMapTileLayer) map.getLayers().get(2);
        // Uses the storage boolean array to determine whether this chest has been opened already.
        this.open = storage.chests[chestNumber];
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected boolean overrideCheck() {
        return true;
    }

    protected void tryMove() {

    }

    // This creates a spawning animation for the chest.
    public void spawn() {
        spawning = true;
        inverted = true;
        // Inverts the chest several times before allowing it to be opened.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.2f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = true;
            }
        }, 0.4f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
            }
        }, 0.6f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = true;
            }
        }, 0.8f);
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                inverted = false;
                spawning = false;
            }
        }, 1);
    }

    // This creates and gives Daur an item based on what the contents of the chest are.
    public void open() {
        // Chests CANNOT open if they are spawning or open.
        if (spawning || open)
            return;

        open = true;
        ChestEvent event = new ChestEvent(map, screen, this);
        // Informs the game save data that this chest has been opened.
        storage.setChest(chestNumber);
    }

    // Returns the item the chest contains for the chest event.
    public Item getContents() {
        if (contents.equals("Heart Piece"))
            return new HeartPiece(screen.daurAtlases.get(2));
        if (contents.equals("1"))
            return new BronzeItem(screen.daurAtlases.get(2));
        if (contents.equals("5"))
            return new CopperItem(screen.daurAtlases.get(2));
        if (contents.equals("10"))
            return new SilverItem(screen.daurAtlases.get(2));
        if (contents.equals("20"))
            return new GoldItem(screen.daurAtlases.get(2));
        if (contents.equals("50"))
            return new DiamondItem(screen.daurAtlases.get(2));
        if (contents.equals("Oak Staff"))
            return new OakStaff(screen.daurAtlases.get(2));
        if (contents.equals("Great Hollow Dungeon Key"))
            return new GreatHollowSmallKey(screen.daurAtlases.get(2));
        if (contents.equals("Compass"))
            return new Compass(screen.daurAtlases.get(2));
        if (contents.equals("Dungeon Map"))
            return new Map(screen.daurAtlases.get(2));
        if (contents.equals("Zephyr's Wisp"))
            return new ZephyrsWispItem(screen.daurAtlases.get(2));
        return null;
    }

    // Returns the item name for the chest event.
    public String getItemName() {
        if (contents.equals("1"))
            return "one coin. A paltry sum...";
        if (contents.equals("5"))
            return "five coins. Not bad.";
        if (contents.equals("10"))
            return "ten coins. Pretty good.";
        if (contents.equals("20"))
            return "twenty coins. Nice.";
        if (contents.equals("50"))
            return "fifty coins. Great!";
        if (contents.equals("Heart Piece"))
            return "one piece of heart! Collect four to get a brand new heart.";
        if (contents.equals("Oak Staff"))
            return "the Fairy Queen's oak staff. Remember, return this to her at once!";
        if (contents.equals("Great Hollow Dungeon Key"))
            return "a dungeon key for the Great Hollow. Use this to open locked doors.";
        if (contents.equals("Compass"))
            return "a Compass. This object will elucidate the locations of hidden chests, as well as the boss and any " +
                    "mini-bosses.";
        if (contents.equals("Dungeon Map"))
            return "a Dungeon Map. This will show all rooms of the dungeon you are in.";
        if (contents.equals("Zephyr's Wisp"))
            return "Zephyr's Wisp! Said to have been blown away from the cloud of Zephyr himself, this small wisp " +
                    "allows Daur to jump over obstacles in his way.";
        return contents;
    }

    // This is used to determine how much money is given to Daur.
    public int acquireContents() {
        return Integer.parseInt(contents);
    }

    abstract void createAnimations();

    abstract void chooseSprite();
}
