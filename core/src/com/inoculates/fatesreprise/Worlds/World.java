package com.inoculates.fatesreprise.Worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Events.*;
import com.inoculates.fatesreprise.Interactables.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.UI.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// This is the superclass for all worlds, and contains valuable methods.
public abstract class World {

    protected Storage storage;
    protected OrthographicCamera camera;
    protected TiledMap map;
    protected GameScreen screen;
    protected TiledMapTileLayer layer;
    protected ArrayList<RectangleMapObject> triggers = new ArrayList<RectangleMapObject>();

    public int cellX = 0, cellY = 15;

    // Rectangular areas that causes a black fog of varying intensity to display.
    protected ArrayList<RectangleMapObject> fogIn = new ArrayList<RectangleMapObject>();
    protected ArrayList<RectangleMapObject> fogOut = new ArrayList<RectangleMapObject>();
    // Cells of the map, as well as the cells that specifically cause another shader to be used. These are both expressed
    // as hashmaps.
    protected Map<String, ArrayList<TiledMapTileLayer.Cell>> cells = new HashMap<String, ArrayList<TiledMapTileLayer.Cell>>();
    protected Map<String, Vector2> shaderCells = new HashMap<String, Vector2>();

    // These are the events that are responsible for the triggers.
    private FairyMeeting fMeeting;

    public World(Storage storage, OrthographicCamera camera, TiledMap map, GameScreen screen) {
        this.storage = storage;
        this.camera = camera;
        this.map = map;
        this.screen = screen;
        layer = (TiledMapTileLayer) map.getLayers().get(0);

        setFog();
        setShaderTransitions();
        setTriggers();
        createCellMap();
    }

    // Main method that creates all aspects of the map, including the characters, interactables, and tile objects (like
    // bushes).
    public void createRenewables() {
        createCharRenewables();
        createInteractableRenewables();
        createTileRenewables();
        createTeleporters();
    }

    // Creates all characters.
    private void createCharRenewables() {
        // Clears all local memory events so that multiple, identical local array elements are not added.
        screen.clearEvents();
        // Looks at all rectangular map objects that are in the layer Spawns.
        for (MapObject object : map.getLayers().get("Spawns").getObjects())
            if (object instanceof RectangleMapObject) {
                // Casts the rectangular object into a normal rectangle.
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                // Gets the middle of the spawn rectangle.
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;
                ArrayList<Character> characterList;
                // The character rendering list is added to depending on the type of world this instance is.
                if (this instanceof UpperWorld)
                    characterList = screen.characters1;
                else if (this instanceof UnderWorld)
                    characterList = screen.characters2;
                else
                    characterList = screen.characters3;
                // Generic sprite that is used for methods.
                Enemy enemy = null;
                // Creates the character based on the spawn.
                if (object.getProperties().containsKey("beetlespawn")) {
                    enemy = new Beetle(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("dummyspawn")) {
                    enemy = new Dummy(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("dollspawn")) {
                    enemy = new Doll(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("slimespawn")) {
                    enemy = new Slime(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("wizardspawn")) {
                    enemy = new Wizard(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("pantomimespawn")) {
                    enemy = new Pantomime(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("woodenstatuespawn")) {
                    enemy = new WoodenStatue(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - 8, y - 8);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("batspawn")) {
                    enemy = new Bat(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("lurkerspawn")) {
                    enemy = new Lurker(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("plantstalkerspawn")) {
                    enemy = new PlantStalker(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("slimejumperspawn")) {
                    enemy = new SlimeJumper(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("ghostspawn")) {
                    enemy = new Ghost(screen, map, screen.characterAtlases.get(2),
                            Integer.parseInt(rectObject.getProperties().get("ghostspawn").toString()));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                if (object.getProperties().containsKey("jfishspawn")) {
                    enemy = new JellyFish(screen, map, screen.characterAtlases.get(2));
                    enemy.setSpawn(x - enemy.getWidth() / 2, y - enemy.getHeight() / 2);
                    characterList.add(enemy);
                }
                // Checks if the added character is part of any event.
                checkEvents(object, enemy);
            }
    }

    // Same but for interactables.
    private void createInteractableRenewables() {
        for (MapObject object : map.getLayers().get("Interactables").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;
                // General interactable for methods.
                Interactable interactable = null;

                // Note that IF the chest does have a trigger, incorporates it. Otherwise, it shall not be considered.
                if (object.getProperties().containsKey("greenblock")) {
                    if (!(rectObject.getProperties().get("limited").toString()).equals(""))
                        interactable = new GreenBlock(screen, map, screen.miscAtlases.get(0), storage,
                                rectObject.getProperties().get("greenblock").toString(),
                                object.getProperties().containsKey("limited"),
                                Integer.parseInt(rectObject.getProperties().get("limited").toString()));
                    else
                        interactable = new GreenBlock(screen, map, screen.miscAtlases.get(0), storage,
                                rectObject.getProperties().get("greenblock").toString(),
                                object.getProperties().containsKey("limited"));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 2);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("greenchest")) {
                    interactable = new GreenChest(screen, map, screen.miscAtlases.get(0), storage,
                            rectObject.getProperties().get("greenchest").toString(), x, y,
                            Integer.parseInt(rectObject.getProperties().get("chest").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 2);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("woodblock")) {
                    if (!(rectObject.getProperties().get("limited").toString()).equals(""))
                        interactable = new WoodBlock(screen, map, screen.miscAtlases.get(0), storage,
                                rectObject.getProperties().get("woodblock").toString(),
                                object.getProperties().containsKey("limited"),
                                Integer.parseInt(rectObject.getProperties().get("limited").toString()));
                    else
                        interactable = new WoodBlock(screen, map, screen.miscAtlases.get(0), storage,
                                rectObject.getProperties().get("woodblock").toString(),
                                object.getProperties().containsKey("limited"));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 2);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("GHMS") && storage.mainQuestStage > 2) {
                    interactable = new WoodChest(screen, map, screen.miscAtlases.get(0), storage,
                            rectObject.getProperties().get("GHMS").toString(), x, y,
                            Integer.parseInt(rectObject.getProperties().get("chest").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 2);
                    screen.interactables.add(interactable);
                }
                // For the Zephyr's Wisp chest.
                if (object.getProperties().containsKey("woodchest")) {
                    interactable = new WoodChest(screen, map, screen.miscAtlases.get(0), storage,
                            rectObject.getProperties().get("woodchest").toString(), x, y,
                            Integer.parseInt(rectObject.getProperties().get("chest").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 2);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("ghlockedh") &&
                        !storage.lockedDoors[Integer.parseInt(object.getProperties().get("locked").toString())]) {
                    interactable = new WoodLockedDoorHorizontal(screen, map, screen.miscAtlases.get(0), storage,
                            x, y, Integer.parseInt(object.getProperties().get("locked").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 4, y - interactable.getHeight() / 2);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("ghlockedv") &&
                        !storage.lockedDoors[Integer.parseInt(object.getProperties().get("locked").toString())]) {
                    interactable = new WoodLockedDoorVertical(screen, map, screen.miscAtlases.get(0), storage,
                            x, y, Integer.parseInt(object.getProperties().get("locked").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 4);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("ghclosedv")) {
                    interactable = new WoodClosedDoorVertical(screen, map, screen.miscAtlases.get(0), storage,
                            x, y, Integer.parseInt(object.getProperties().get("closed").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 4);
                }
                if (object.getProperties().containsKey("ghclosedh")) {
                    interactable = new WoodClosedDoorHorizontal(screen, map, screen.miscAtlases.get(0), storage,
                            x, y, Integer.parseInt(object.getProperties().get("closed").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 4, y - interactable.getHeight() / 2);
                }
                if (object.getProperties().containsKey("platformspawn")) {
                    interactable = new Platform(screen, map, screen.miscAtlases.get(0), storage,
                            Float.parseFloat(object.getProperties().get("velocityX").toString()),
                            Float.parseFloat(object.getProperties().get("velocityY").toString()));
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 2);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("bossh") &&
                        !storage.bossDoors[Integer.parseInt(object.getProperties().get("bossdoor").toString())]) {
                    interactable = new BossDoorHorizontal(screen, map, screen.miscAtlases.get(0), storage);
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 4);
                    screen.interactables.add(interactable);
                }
                if (object.getProperties().containsKey("bossv") &&
                        !storage.bossDoors[Integer.parseInt(object.getProperties().get("bossdoor").toString())]) {
                    interactable = new BossDoorVertical(screen, map, screen.miscAtlases.get(0), storage);
                    interactable.setSpawn(x - interactable.getWidth() / 2, y - interactable.getHeight() / 4);
                    screen.interactables.add(interactable);
                }
                checkEvents(object, interactable);
            }
    }

    // Same but for teleporters.
    private void createTeleporters() {
        // If not Under World simply return.
        if (!(this instanceof UnderWorld))
            return;
        for (MapObject object : map.getLayers().get("Teleporters").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();
                float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + layer.getTileWidth() / 2;
                float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight() + layer.getTileHeight() / 2;
                Teleporter tp;
                if (object.getProperties().containsKey("TP1Spawn") && storage.mainQuestStage > 2) {
                    tp = new Teleporter(screen, screen.map, screen.miscAtlases.get(0), storage, this);
                    tp.setSpawn(x - tp.getWidth() / 2, y - tp.getHeight() / 2);
                    screen.interactables.add(tp);
                    checkEvents(object, tp);
                }
                if (object.getProperties().containsKey("TP2Spawn") && storage.mainQuestStage > 2) {
                    tp = new Teleporter(screen, screen.map, screen.miscAtlases.get(0), storage, this);
                    tp.setSpawn(x - tp.getWidth() / 2, y - tp.getHeight() / 2);
                    screen.interactables.add(tp);
                    checkEvents(object, tp);
                }
            }
    }

    // Same but for tile objects.
    private void createTileRenewables() {
        for (TiledMapTileLayer.Cell cell : cells.get("Spring Bush"))
            cell.setTile(screen.springBushTile);
        cells.get("Spring Bush").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Summer Bush"))
            cell.setTile(screen.summerBushTile);
        cells.get("Summer Bush").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Fall Bush"))
            cell.setTile(screen.fallBushTile);
        cells.get("Fall Bush").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Winter Bush"))
            cell.setTile(screen.winterBushTile);
        cells.get("Winter Bush").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Bramble"))
            cell.setTile(screen.brambleTile);
        cells.get("Bramble").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Spring Grass"))
            cell.setTile(screen.springGrassTile);
        cells.get("Spring Grass").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Summer Grass"))
            cell.setTile(screen.summerGrassTile);
        cells.get("Summer Grass").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Fall Grass"))
            cell.setTile(screen.fallGrassTile);
        cells.get("Fall Grass").clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Winter Grass"))
            cell.setTile(screen.winterGrassTile);
        cells.get("Winter Grass").clear();
    }

    // This removes all renewables, depending on certain conditions. Note that this method is launched when the player
    // enters/leaves a world. Meaning if a character enters a dungeon from the overworld, all renewables in the overworld
    // shall be erased. This is so that when the renewables are recreated, they are not created over existing renewables.
    public void cleanRenewables() {
        ArrayList<Character> characterList = new ArrayList<Character>();
        // Adds to the separate list to create a temporary copy of the corresponding character arraylist.
        if (this instanceof UpperWorld)
            for (Character character : screen.characters1)
                characterList.add(character);
        else if (this instanceof UnderWorld)
            for (Character character : screen.characters2)
                characterList.add(character);
        else
            for (Character character : screen.characters3)
                characterList.add(character);
        // If the character is an enemy, deletes it from the corresponding character list.
        for (Character character : characterList)
            if (character instanceof Enemy) {
                if (this instanceof UpperWorld)
                    screen.characters1.remove(character);
                else if (this instanceof UnderWorld)
                    screen.characters2.remove(character);
                else
                    screen.characters3.remove(character);
            }
        // Same for all interactables.
        screen.interactables.clear();
        // Same for all projectiles.
        screen.projectiles.clear();
        // Same for all consumables.
        screen.consumables.clear();
        // Clears global timer.
        screen.globalTimer.clear();
    }

    // This method checks if the cell has a bush tag. If so, creates the bush over the tile and deletes the tag.
    private void checkCell(TiledMapTileLayer.Cell cell) {
        for (TiledMapTileLayer.Cell checkCell : cells.get("Spring Bush")) {
            if (cell.equals(checkCell)) {
                cell.setTile(screen.springBushTile);
                cells.get("Spring Bush").remove(cell);
            }
        }
        for (TiledMapTileLayer.Cell checkCell : cells.get("Summer Bush"))
            if (cell.equals(checkCell)) {
                cell.setTile(screen.summerBushTile);
                cells.get("Summer Bush").remove(cell);
            }
        for (TiledMapTileLayer.Cell checkCell : cells.get("Fall Bush"))
            if (cell.equals(checkCell)) {
                cell.setTile(screen.fallBushTile);
                cells.get("Fall Bush").remove(cell);
            }
        for (TiledMapTileLayer.Cell checkCell : cells.get("Winter Bush"))
            if (cell.equals(checkCell)) {
                cell.setTile(screen.winterBushTile);
                cells.get("Winter Bush").remove(cell);
            }

    }

    // This triggers an event depending on the trigger given.
    public void trigger(RectangleMapObject trigger) {
        Event event;
        // Player has triggered the fairy meeting start. If the fairy meeting event has not launched yet, launches one.
        if (trigger.getProperties().containsKey("FS"))
            if (fMeeting == null)
                fMeeting = new FairyMeeting(screen.map, screen);
        // Player has triggered the fairy meeting end. If the fairy meeting has launched, ends it.
        if (trigger.getProperties().containsKey("FE"))
            if (fMeeting != null) {
                fMeeting.proceed();
                fMeeting = null;
            }
        // Player has triggered the bush ambush. Launches the corresponding event.
        if (trigger.getProperties().containsKey("MAS") && storage.mainQuestStage == 0 && !storage.FDstorage.ambush)
            event = new BushAmbush(screen.map, screen, storage);
        // Player has triggered the heart piece bush enemy event. Launches the corresponding event.
        if (trigger.getProperties().containsKey("BHS") && !storage.heartPiecesObtained[0] && !storage.FDstorage.heartPieceEvent)
            event = new HeartPieceBushEvent(screen.map, screen, storage);
        // If the player has entered the Great Hollow Dungeon, initiates the informing dialogue, and sets the dungeon
        // integer to 0 (which indicates Daur is in the Great Hollow Dungeon).
        if (trigger.getProperties().containsKey(("GHD")) && storage.dungeon != 0)
            event = new GreatHollowDialogue(screen.map, screen, storage);
        // If the player has exited the Great Hollow Dungeon, sets the dungeon integer to read -1 (no dungeon).
        if (trigger.getProperties().containsKey(("GHE")))
            storage.setDungeon(-1);
        // If the player has stepped on the first great hollow trigger, closes the trigger door.
        if (trigger.getProperties().containsKey("GHT1") && !storage.FDstorage.GHT1)
            storage.FDstorage.closeDoor1();
        // If the player has stepped on the second great hollow trigger, closes the trigger door.
        if (trigger.getProperties().containsKey("GHT2") && !storage.FDstorage.GHT2)
            storage.FDstorage.closeDoor2();
        // If the player has stepped on the Great Hollow miniboss trigger, closes the trigger door and creates the
        // miniboss: the Slime King. Also resets Daur's spawn point.
        if (trigger.getProperties().containsKey("GHMT") && storage.mainQuestStage < 3 && !storage.FDstorage.GHMT) {
            storage.FDstorage.closeDoor3();
            storage.FDstorage.createMiniboss();
            // Sets Daur's new spawn point.
            screen.daur.setSpawnPoint(trigger.getRectangle().getX() + trigger.getRectangle().getWidth() / 2
                    - screen.daur.getWidth() / 2, trigger.getRectangle().getY() + trigger.getRectangle().getHeight() / 2
                    - screen.daur.getHeight() / 2);
        }
        if (trigger.getProperties().containsKey("GHBT") && storage.mainQuestStage < 4 && !storage.FDstorage.GHBT) {
            storage.FDstorage.closeDoor4();
            storage.FDstorage.startBossFight();
            screen.daur.setSpawnPoint(trigger.getRectangle().getX() + trigger.getRectangle().getWidth() / 2
                    - screen.daur.getWidth() / 2, trigger.getRectangle().getY() + trigger.getRectangle().getHeight() / 2
                    - screen.daur.getHeight() / 2);
        }
    }

    // Creates four new array lists, one for each type of bush.
    private void createCellMap() {
        cells.put("Spring Bush", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Summer Bush", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Fall Bush", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Winter Bush", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Bramble", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Spring Grass", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Summer Grass", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Fall Grass", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Winter Grass", new ArrayList<TiledMapTileLayer.Cell>());
    }

    // If the player is beyond a certain point (the boundaries of a cell), the camera will pan over to the new cell.
    public void checkCameraChange() {
        // Does NOT work if Daur is flying for whatever reason.
        if (!screen.daur.isGrounded())
            return;
        if (screen.daur.getX() + screen.daur.getWidth() / 2 > layer.getTileWidth() * 10 * (cellX + 1)) {
            cellX++;
            setCameraPosition(true, true);
        } else if (screen.daur.getX() + screen.daur.getWidth() / 2 < layer.getTileWidth() * 10 * cellX) {
            cellX--;
            setCameraPosition(true, false);
        }
        if (screen.daur.getY() + screen.daur.getHeight() / 2 > layer.getTileWidth() * 10 * (cellY + 1)) {
            cellY++;
            setCameraPosition(false, true);
        } else if (screen.daur.getY() + screen.daur.getHeight() / 2 < layer.getTileWidth() * 10 * cellY) {
            cellY--;
            setCameraPosition(false, false);
        }
        // If this is the overworld, sets the cell to be explored.
        if (this instanceof UpperWorld)
            storage.setExplored(cellX, cellY);
    }

    // This causes the panning effect when the camera moves a cell.
    protected void setCameraPosition(boolean onXAxis, boolean plus) {
        float deltaTime = 0;
        // Freezes the screen temporarily.
        screen.freeze();
        screen.daur.freeze();
        screen.daur.setTransitioning(true);
        // After 0.16 seconds sets the camera to a static point. This is to avoid any
        // different camera positions from occurring.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                camera.position.set(cellX * 10 * layer.getTileWidth() + camera.viewportWidth / 2,
                        cellY * 10 * layer.getTileHeight() + camera.viewportHeight / 2, 0);
                screen.daur.setTransitioning(false);
            }
        }, 0.16f);

        if (onXAxis) {
            if (plus)
                // Using a delayed for loop, pans the camera to the right.
                for (float x = camera.position.x; x <= cellX * 10 * layer.getTileWidth() + camera.viewportWidth / 2; x++) {
                    final float newX = x;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(newX, camera.position.y, 0);
                            // Ensures that the UI pans with the camera, rather than jumps.
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    deltaTime += 0.001f;
                }
            else
                for (float x = camera.position.x; x >= cellX * 10 * layer.getTileWidth() + camera.viewportWidth / 2; x--) {
                    final float newX = x;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(newX, camera.position.y, 0);
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    deltaTime += 0.001f;
                }
        } else {
            if (plus)
                for (float y = camera.position.y; y <= cellY * 10 * layer.getTileHeight() + camera.viewportHeight / 2; y++) {
                    final float newY = y;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(camera.position.x, newY, 0);
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    deltaTime += 0.001f;
                }
            else {
                for (float y = camera.position.y; y >= cellY * 10 * layer.getTileHeight() + camera.viewportHeight / 2; y--) {
                    final float newY = y;
                    screen.globalTimer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(camera.position.x, newY, 0);
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    deltaTime += 0.001f;
                }
            }
        }
        // Resets the position of the mask, informs the program which cell the player resides in, checks if a shader
        // transition is needed, and sets Daur's new spawn point (if the player should fall down a hole).
        screen.mask.setPosition(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2);
        storage.setCells(cellX, cellY);
        checkShaderTransition();
        setSpawnPoint(onXAxis, plus);

        // After 0.1 seconds, unfreezes the screen to allow for movement again.
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.unFreeze();
            }
        }, 0.1f);
    }

    // Sets the spawn point. This is if the player is erased from the map for any reason, including falling down a hole.
    protected void setSpawnPoint(boolean xAxis, boolean plus) {
        if (xAxis) {
            if (plus)
                screen.daur.setSpawnPoint(screen.daur.getX() + screen.daur.getWidth(), screen.daur.getY());
            else
                screen.daur.setSpawnPoint(screen.daur.getX() - screen.daur.getWidth(), screen.daur.getY());
        } else {
            if (plus)
                screen.daur.setSpawnPoint(screen.daur.getX(), screen.daur.getY() + screen.daur.getHeight());
            else
                screen.daur.setSpawnPoint(screen.daur.getX(), screen.daur.getY() - screen.daur.getHeight());
        }
    }

    // Gets the fog of the cell. If the cell is meant to be a fog in or fog out transition.
    public Rectangle getFog(int p, boolean in) {
        if (in)
            return fogIn.get(p).getRectangle();
        else
            return fogOut.get(p).getRectangle();
    }

    // Checks whether a shader should be used to draw the map and the renewables. This is based on the dictionary shader
    // cells. If the shader cell indicates a certain transition should be made, it will be made.
    protected void checkShaderTransition() {
        Vector2 cells = new Vector2(cellX, cellY);
        if (shaderCells.get("fwin1").equals(cells) || shaderCells.get("fwin2").equals(cells) ||
                shaderCells.get("fwin3").equals(cells) || shaderCells.get("fwin4").equals(cells) ||
                shaderCells.get("fwin5").equals(cells) || shaderCells.get("fwin6").equals(cells) ||
                shaderCells.get("fwin7").equals(cells))
            screen.setCurrentMapShader(new ShaderProgram(Gdx.files.internal("Shaders/faron.vert"), Gdx.files.internal("Shaders/faron.frag")));
        else if (shaderCells.get("fwout1").equals(cells) || shaderCells.get("fwout2").equals(cells))
            screen.setCurrentMapShader(null);
    }

    // Gets all of the triggers in the world.
    protected void setTriggers() {
        for (MapObject object : map.getLayers().get("Triggers").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                triggers.add(rectObject);
            }
    }

    // Adds the fog to the fog array list.
    protected void setFog() {
        for (MapObject object : map.getLayers().get("Fogs").getObjects())
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;

                if (object.getProperties().containsKey("FI"))
                    fogIn.add(rectObject);
                if (object.getProperties().containsKey("FO"))
                    fogOut.add(rectObject);
            }
    }

    // Gets the intensity of the fog (alpha of the mask).
    public float getFogAmount(int p) {
        return Float.parseFloat(fogIn.get(p).getProperties().get("FI").toString());
    }

    // Returns the size of the fog arrays.
    public int getFogSize(boolean in) {
        if (in)
            return fogIn.size();
        else return fogOut.size();
    }

    // Returns the amount of triggers currently inside the world.
    public int getTriggerSize() {
        return triggers.size();
    }

    public RectangleMapObject getTrigger(int t) {
        return triggers.get(t);
    }

    public TiledMap getMap() {
        return map;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void setCellX(int x) {
        cellX = x;
    }

    public void setCellY(int y) {
        cellY = y;
    }

    // This sets the camera instantly to a cell, rather than panning to it. This is useful if the player has just left or
    // entered a world.
    public void setCameraInstantly() {
        for (int i = 0; i < 16; i++)
            if (screen.daur.getX() + screen.daur.getWidth() / 2 > layer.getTileWidth() * 10 * i)
                cellX = i;
        for (int i = 0; i < 16; i++)
            if (screen.daur.getY() + screen.daur.getHeight() / 2 > layer.getTileWidth() * 10 * i)
                cellY = i;
        camera.position.set(cellX * 10 * layer.getTileWidth() + camera.viewportWidth / 2,
                cellY * 10 * layer.getTileHeight() + camera.viewportHeight / 2, 0);
        screen.mask.setPosition(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2);
        // Resets the position of the mask, informs the program which cell the player resides in, checks if a shader
        // transition is needed, and sets Daur's new spawn point (if the player should fall down a hole).
        storage.setCells(cellX, cellY);
        screen.daur.setSpawnPoint(screen.daur.getX(), screen.daur.getY());
        // If this is the overworld, sets the cell to be explored.
        if (this instanceof UpperWorld)
            storage.setExplored(cellX, cellY);
        checkShaderTransition();
    }

    // This method checks if any triggering events should be fired upon the creation of a character.
    private void checkEvents(MapObject object, Sprite sprite) {
        if (sprite == null)
            return;
        if (object.getProperties().containsKey("bushheartpiece"))
            storage.FDstorage.addHeartPieceEnemy((Enemy) sprite);
        if (object.getProperties().containsKey("greathollowtrigger1"))
            storage.FDstorage.addGreatHollowTrigger1Sprites(sprite);
        if (object.getProperties().containsKey("greathollowtrigger2"))
            storage.FDstorage.addGreatHollowTrigger2Sprites(sprite);
        if (object.getProperties().containsKey("greathollowtrigger3"))
            storage.FDstorage.addGreatHollowTrigger3Sprites(sprite);
        if (object.getProperties().containsKey("greathollowtrigger4"))
            storage.FDstorage.addGreatHollowTrigger4Sprites(sprite);
        if (object.getProperties().containsKey("greathollowtrigger5"))
            storage.FDstorage.addGreatHollowTrigger5Sprites(sprite);
        if (object.getProperties().containsKey("greathollowtrigger6"))
            storage.FDstorage.addGreatHollowTrigger6Sprites(sprite);
        if (object.getProperties().containsKey("greathollowminiboss"))
            storage.FDstorage.addGreatHollowMinibossDoor((ClosedDoor) sprite);
        if (object.getProperties().containsKey("greathollowboss"))
            storage.FDstorage.addGreatHollowBossDoor((ClosedDoor) sprite);
        if (object.getProperties().containsKey("greathollowboss2"))
            storage.FDstorage.addGreatHollowBossDoor2((ClosedDoor) sprite);
    }

    public void addCell(String key, TiledMapTileLayer.Cell value) {
        cells.get(key).add(value);
    }

    abstract void createCharacters();

    abstract void setShaderTransitions();

    abstract void setQuestEvents();
}
