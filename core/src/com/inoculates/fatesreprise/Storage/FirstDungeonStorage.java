package com.inoculates.fatesreprise.Storage;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.Enemy;
import com.inoculates.fatesreprise.Characters.MasterWizard;
import com.inoculates.fatesreprise.Characters.SlimeKing;
import com.inoculates.fatesreprise.Events.GreatHollowBossFight;
import com.inoculates.fatesreprise.Interactables.*;
import com.inoculates.fatesreprise.Screens.GameScreen;

import java.awt.*;
import java.util.ArrayList;

// This is the storage class that carries NON-persistent data throughout games but only for the events leading up till the
// first dungeon.
public class FirstDungeonStorage {
    private GameScreen screen;
    private Storage storage;

    // The bush blocks that cordon the area off.
    private ArrayList<BushBlock> ambushBushBlocks = new ArrayList<BushBlock>();
    // Enemies that Daur encounters in the ambush.
    private ArrayList<Enemy> ambushEnemies = new ArrayList<Enemy>();
    // Enemies Daur needs to kill to obtain the heart piece in the bush.
    private ArrayList<Enemy> heartPieceBushEnemies = new ArrayList<Enemy>();
    // The sprites (block and enemies) that need to be tinkered with to activate the great hollow trigger one.
    private ArrayList<Sprite> greatHollowTrigger1Sprites = new ArrayList<Sprite>();
    // The sprites (enemies) that need to be tinkered with to activate the great hollow trigger two.
    private ArrayList<Sprite> greatHollowTrigger2Sprites = new ArrayList<Sprite>();
    // And so on.
    private ArrayList<Sprite> greatHollowTrigger3Sprites = new ArrayList<Sprite>();
    private ArrayList<Sprite> greatHollowTrigger4Sprites = new ArrayList<Sprite>();
    private ArrayList<Sprite> greatHollowTrigger5Sprites = new ArrayList<Sprite>();
    private ArrayList<Sprite> greatHollowTrigger6Sprites = new ArrayList<Sprite>();
    private ClosedDoor greatHollowMinibossTriggerDoor;
    private ClosedDoor greatHollowBossTriggerDoor;
    private ClosedDoor greatHollowBossTriggerDoor2;
    // The two teleporters. The first refers to the entrance and the second the miniboss room.
    Teleporter tp1, tp2;

    public boolean ambush = false, heartPieceEvent = false, greatHollowDialogue = false, GHT1 = false, GHT2 = false,
    GHMT = false, GHBT = false;

    // THe level Daur is on.
    public int level = 0;

    public FirstDungeonStorage(GameScreen screen, Storage storage) {
        this.screen = screen;
        this.storage = storage;
    }

    public void addAmbushBushBlocks(BushBlock bushBlock) {
        ambushBushBlocks.add(bushBlock);
    }

    public void addAmbushEnemy(Enemy enemy) {
        ambushEnemies.add(enemy);
    }

    public void addHeartPieceEnemy(Enemy enemy) {
        heartPieceBushEnemies.add(enemy);
    }

    public void addGreatHollowTrigger1Sprites (Sprite sprite) {
        greatHollowTrigger1Sprites.add(sprite);
    }

    public void addGreatHollowTrigger2Sprites (Sprite sprite) {
        greatHollowTrigger2Sprites.add(sprite);
    }

    public void addGreatHollowTrigger3Sprites (Sprite sprite) {
        greatHollowTrigger3Sprites.add(sprite);
    }

    public void addGreatHollowTrigger4Sprites (Sprite sprite) {
        greatHollowTrigger4Sprites.add(sprite);
    }

    public void addGreatHollowTrigger5Sprites (Sprite sprite) {
        greatHollowTrigger5Sprites.add(sprite);
    }

    public void addGreatHollowTrigger6Sprites (Sprite sprite) {
        greatHollowTrigger6Sprites.add(sprite);
    }

    public void addGreatHollowMinibossDoor (ClosedDoor door) {
        greatHollowMinibossTriggerDoor = door;
    }

    public void addGreatHollowBossDoor (ClosedDoor door) {
        greatHollowBossTriggerDoor = door;
    }

    public void addGreatHollowBossDoor2 (ClosedDoor door) {
        greatHollowBossTriggerDoor2 = door;
    }

    public void beginAmbush() {
        ambush = true;
    }

    public void beginHeartPieceEvent() {
        heartPieceEvent = true;
    }

    public void beginGreatHollowDialogue() {
        greatHollowDialogue = true;
    }

    public void checkClear(Sprite sprite) {
        checkClearAmbush(sprite);
        checkClearBushHeartPiece(sprite);
        checkClearGreatHollowTrigger1(sprite);
        checkClearGreatHollowTrigger2(sprite);
        checkClearGreatHollowTrigger3(sprite);
        checkClearGreatHollowTrigger4(sprite);
        checkClearGreatHollowTrigger5(sprite);
        checkClearGreatHollowTrigger6(sprite);
        checkClearMiniboss(sprite);
        checkClearBoss(sprite);
    }

    // If all ambush enemies are dead, clears away the bushes. The object in the constructor is used to determine whether
    // this method will actually fire.
    public void checkClearAmbush(Sprite sprite) {
        // If the array DOES NOT contain the enemy.
        if (!ambushEnemies.contains(sprite))
            return;
        // If any of the enemies are not dead, does NOT clear the bushes.
        for (Enemy aEnemy : ambushEnemies)
            if (!aEnemy.isDead())
                return;
        // Otherwise clears the bushes.
        for (BushBlock block : ambushBushBlocks)
            block.fade();
        // Sets the main quest forward to 1.
        storage.setMainQuestStage();
        // Clears array.
        ambushEnemies.clear();
        // Plays the victory sound.
        storage.sounds.get("success1").play(2.0f);
        // Stops miniboss music and starts normal cave music.
        storage.stopMusic();
        screen.storage.music.get("cavemusic").play();
        screen.storage.music.get("cavemusic").setVolume(0.75f);
        screen.storage.music.get("cavemusic").setLooping(true);
    }

    // Similar to above.
    public void checkClearBushHeartPiece(Sprite sprite) {
        // If the array DOES NOT contain the enemy.
        if (!heartPieceBushEnemies.contains(sprite))
            return;
        // If any of the enemies are not dead, does NOT give the heart piece.
        for (Enemy hEnemy : heartPieceBushEnemies)
            if (!hEnemy.isDead())
                return;
        // Otherwise gives the heart piece.
        screen.storage.setHeartPiece(0);
        // Creates the chest.
        spawnChest("greenchestkillspawn");
        // Plays the victory sound.
        storage.sounds.get("success2").play(2.0f);
    }

    public void checkClearGreatHollowTrigger1(Sprite sprite) {
        // If the array DOES NOT contain the sprite.
        if (!greatHollowTrigger1Sprites.contains(sprite))
            return;
        // If any of the enemies are not dead, or the block is not moved, does NOT give the key.
        for (Sprite component : greatHollowTrigger1Sprites) {
            if (component instanceof Enemy)
                if (!((Enemy) component).isDead())
                    return;
            if (component instanceof WoodBlock)
                if (!(((Block) component).isTriggered()))
                    return;
        }
        // Otherwise gives the key chest.
        spawnChest("greathollowtrigger1spawn");
        // Plays the victory sound.
        storage.sounds.get("success2").play(2.0f);
    }

    public void checkClearGreatHollowTrigger2(Sprite sprite) {
        // If the array DOES NOT contain the sprite.
        if (!greatHollowTrigger2Sprites.contains(sprite))
            return;
        // If any of the enemies are not dead, does NOT open the door.
        for (Sprite component : greatHollowTrigger2Sprites) {
            if (component instanceof Enemy)
                if (!((Enemy) component).isDead())
                    return;
            // If the for loop has gone on far enough to reach the closed door, opens it.
            if (component instanceof WoodClosedDoorVertical) {
                ((WoodClosedDoorVertical) component).open(2);
                // Plays the victory sound.
                storage.sounds.get("success2").play(2.0f);
            }
        }
    }

    public void checkClearGreatHollowTrigger3(Sprite sprite) {
        // If the array DOES NOT contain the sprite.
        if (!greatHollowTrigger3Sprites.contains(sprite))
            return;
        // If the block is not pushed, does not spawn the chest containing the compass.
        for (Sprite component : greatHollowTrigger3Sprites) {
            if (component instanceof WoodBlock)
                if (!(((Block) component).isTriggered()))
                    return;
                else {
                    spawnChest("greathollowtrigger3spawn");
                    // Plays the victory sound.
                    storage.sounds.get("success2").play(2.0f);
                }
        }
    }

    public void checkClearGreatHollowTrigger4(Sprite sprite) {
        // If the array DOES NOT contain the sprite.
        if (!greatHollowTrigger4Sprites.contains(sprite))
            return;
        // If any of the enemies are not dead, does NOT open the door.
        for (Sprite component : greatHollowTrigger4Sprites) {
            if (component instanceof WoodBlock)
                if (!(((Block) component).isTriggered()))
                    return;
            // If the for loop has gone on far enough to reach the closed door, opens it.
            if (component instanceof WoodClosedDoorHorizontal) {
                ((WoodClosedDoorHorizontal) component).open(1);
                // Plays the victory sound.
                storage.sounds.get("success2").play(2.0f);
            }
        }
    }

    public void checkClearGreatHollowTrigger5(Sprite sprite) {
        // If the array DOES NOT contain the sprite.
        if (!greatHollowTrigger5Sprites.contains(sprite))
            return;
        for (Sprite component : greatHollowTrigger5Sprites)
            if (component instanceof WoodBlock)
                if (!(((Block) component).isTriggered()))
                    return;
        spawnChest("greathollowtrigger5spawn");
        // Plays the victory sound.
        storage.sounds.get("success2").play(2.0f);
    }

    public void checkClearGreatHollowTrigger6(Sprite sprite) {
        // If the array DOES NOT contain the sprite.
        if (!greatHollowTrigger6Sprites.contains(sprite))
            return;
        for (Sprite component : greatHollowTrigger6Sprites)
            if (component instanceof WoodBlock)
                if (!(((Block) component).isTriggered()))
                    return;
        spawnChest("greathollowtrigger6spawn");
        // Plays the victory sound.
        storage.sounds.get("success2").play(2.0f);
    }

    // If the sprite is the King Slime miniboss, spawns the chest containing Zephyr's Wisp and the teleporter for the
    // dungeon. Also removes the closed door.
    private void checkClearMiniboss(Sprite sprite) {
        if (sprite instanceof SlimeKing) {
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    // Increments the main quest to 3, for trigger-related purposes.
                    screen.storage.setMainQuestStage();
                    spawnChest("GHMS");
                    createTeleporters();
                    greatHollowMinibossTriggerDoor.open(-2);
                    // Plays the victory sound.
                    storage.sounds.get("success1").play(2.0f);
                    // Stops miniboss music and starts normal Great Hollow music.
                    storage.stopMusic();
                    storage.music.get("greathollowmusic").play();
                    storage.music.get("greathollowmusic").setVolume(0.75f);
                    storage.music.get("greathollowmusic").setLooping(true);
                }
            }, 0.5f);
        }
    }

    // If the Master Wizard has died, opens the door to the first sage.
    private void checkClearBoss(Sprite sprite) {
        if (sprite instanceof MasterWizard) {
            // Increments the main quest to 4, for trigger-related purposes.
            storage.setMainQuestStage();
            // Opens the sage door after 2 seconds.
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    greatHollowBossTriggerDoor2.open(2);
                }
            }, 2);
            // Stops boss music and starts victory music.
            storage.stopMusic();
            // Plays victory sound (the short burst of sound).
            screen.storage.music.get("victorymusic").play();
            screen.storage.music.get("victorymusic").setVolume(2.0f);
            screen.globalTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    // Plays victory music (the ambient soundtrack) after 2 seconds.
                    screen.storage.music.get("bossvictorymusic").play();
                    screen.storage.music.get("bossvictorymusic").setVolume(0.75f);
                    screen.storage.music.get("bossvictorymusic").setLooping(true);
                }
            }, 2);
        }
    }

    // Creates the corresponding chest.
    private void spawnChest(String trigger) {
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(0);

        // Creates the chest.
        for (MapObject object : screen.map.getLayers().get("Interactables").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;

                if (object.getProperties().containsKey(trigger) && object.getProperties().containsKey("green")) {
                    GreenChest chest = new GreenChest(screen, screen.map, screen.miscAtlases.get(0), storage,
                            rectObject.getProperties().get(trigger).toString(), x, y,
                            Integer.parseInt(rectObject.getProperties().get("chest").toString()));
                    chest.spawn();
                    screen.interactables.add(chest);
                    // Plays the chest appear sound, if Daur is close enough after 1 second.
                    if ((int) (x / layer.getTileWidth()) / 10 == storage.cellX &&
                            (int) (y / layer.getTileHeight()) / 10 == storage.cellY)
                        screen.globalTimer.scheduleTask(new Timer.Task() {
                            @Override
                            public void run() {
                                storage.sounds.get("chestappear").play(1.0f);
                            }
                        }, 1);
                    }
                if (object.getProperties().containsKey(trigger) && object.getProperties().containsKey("wood")) {
                    WoodChest chest = new WoodChest(screen, screen.map, screen.miscAtlases.get(0), storage,
                            rectObject.getProperties().get(trigger).toString(), x, y,
                            Integer.parseInt(rectObject.getProperties().get("chest").toString()));
                    chest.spawn();
                    screen.interactables.add(chest);
                    // Plays the chest appear sound, if Daur is close enough after 1 second.
                    if ((int) (x / layer.getTileWidth()) / 10 == storage.cellX &&
                            (int) (y / layer.getTileHeight()) / 10 == storage.cellY)
                        screen.globalTimer.scheduleTask(new Timer.Task() {
                            @Override
                            public void run() {
                                storage.sounds.get("chestappear").play(1.0f);
                            }
                        }, 1);
                }
            }
    }

    // Tells the game that all these events can fire again.
    public void resetEvents() {
        ambush = false;
        heartPieceEvent = false;
        greatHollowDialogue = false;
        GHT1 = false;
        GHT2 = false;
        GHMT = false;
        GHBT = false;
    }

    public void clearEvents() {
        ambushEnemies.clear();
        heartPieceBushEnemies.clear();
        greatHollowTrigger1Sprites.clear();
        greatHollowTrigger2Sprites.clear();
        greatHollowTrigger3Sprites.clear();
        greatHollowTrigger4Sprites.clear();
        greatHollowTrigger5Sprites.clear();
        greatHollowTrigger6Sprites.clear();
    }

    public void closeDoor1() {
        GHT1 = true;
        for (Sprite sprite : greatHollowTrigger2Sprites)
            if ((sprite instanceof WoodClosedDoorVertical))
                ((WoodClosedDoorVertical) sprite).close(-2);
    }

    public void closeDoor2() {
        GHT2 = true;
        for (Sprite sprite : greatHollowTrigger4Sprites)
            if ((sprite instanceof WoodClosedDoorHorizontal))
                ((WoodClosedDoorHorizontal) sprite).close(-1);
    }

    public void closeDoor3() {
        GHMT = true;
        greatHollowMinibossTriggerDoor.close(2);
    }

    public void closeDoor4() {
        GHBT = true;
        greatHollowBossTriggerDoor.close(2);
        greatHollowBossTriggerDoor2.close(-2);
    }

    // Creates the miniboss: the King Slime.
    public void createMiniboss() {
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(0);
        // Looks at all rectangular map objects that are in the layer Spawns.
        for (MapObject object : screen.map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                // Casts the rectangular object into a normal rectangle.
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                // Gets the bottom left of the rectangle.
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                if (object.getProperties().containsKey("slimekingspawn")) {
                    SlimeKing sKing = new SlimeKing(screen, screen.map, screen.characterAtlases.get(2));
                    sKing.setSpawn(x + layer.getTileWidth() - sKing.getWidth() / 2, y + layer.getTileHeight()
                            - sKing.getHeight() / 2);
                    // Elevates the King Slime far above the arena, to properly create his entrance.
                    sKing.setY(sKing.getY() + 100);
                    // Adds the King Slime to the rendering list.
                    screen.characters2.add(sKing);
                    // Stops the dungeon music. NOTE: Only starts the miniboss music once the King Slime reaches the
                    // floor.
                    screen.storage.stopMusic();
                }
            }
    }

    // Begins the boss fight (begins dialogue and then initiates boss fight).
    public void startBossFight() {
        GreatHollowBossFight bossFight = new GreatHollowBossFight(screen.map, screen);
    }

    // This method is fired off after the death of the King Slime. Creates the teleporters at the beginning of the
    // level and at the miniboss location.
    public void createTeleporters() {
        TiledMapTileLayer layer = (TiledMapTileLayer) screen.map.getLayers().get(0);
        for (MapObject object : screen.map.getLayers().get("Teleporters").getObjects())
            if (object instanceof RectangleMapObject) {
                // Casts the rectangular object into a normal rectangle.
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                // Gets the bottom left of the rectangle.
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                if (object.getProperties().containsKey("TP1Spawn")) {
                    tp1 = new Teleporter(screen, screen.map, screen.miscAtlases.get(0), storage, this);
                    tp1.setSpawn(x + layer.getTileWidth() / 2 - tp1.getWidth() / 2,
                            y + layer.getTileHeight() / 2 - tp1.getHeight() / 2);
                    screen.interactables.add(tp1);
                }
                if (object.getProperties().containsKey("TP2Spawn")) {
                    tp2 = new Teleporter(screen, screen.map, screen.miscAtlases.get(0), storage, this);
                    tp2.setSpawn(x + layer.getTileWidth() / 2 - tp2.getWidth() / 2,
                            y + layer.getTileHeight() / 2 - tp2.getHeight() / 2);
                    screen.interactables.add(tp2);
                }
            }
    }

    // Sets the level Daur is currently on.
    public void setLevel(int magnitude) {
        level += magnitude;
    }
}
