package com.inoculates.fatesreprise.Worlds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Interactables.GreenBlock;
import com.inoculates.fatesreprise.Interactables.Interactable;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage;
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

    public int cellX = 1, cellY = 16;

    // Rectangular areas that causes a black fog of varying intensity to display.
    protected ArrayList<RectangleMapObject> fogIn = new ArrayList<RectangleMapObject>();
    protected ArrayList<RectangleMapObject> fogOut = new ArrayList<RectangleMapObject>();
    // Cells of the map, as well as the cells that specifically cause another shader to be used. These are both expressed
    // as hashmaps.
    protected Map<String, ArrayList<TiledMapTileLayer.Cell>> cells = new HashMap<String, ArrayList<TiledMapTileLayer.Cell>>();
    protected Map<String, Vector2> shaderCells = new HashMap<String, Vector2>();

    public World(Storage storage, OrthographicCamera camera, TiledMap map, GameScreen screen) {
        this.storage = storage;
        this.camera = camera;
        this.map = map;
        this.screen = screen;
        layer = (TiledMapTileLayer) map.getLayers().get(0);

        setFog();
        setShaderTransitions();
        createCellMap();
    }
    // Main method that creates all aspects of the map, including the characters, interactables, and tile objects (like
    // bushes).
    public void createRenewables() {
        createCharRenewables();
        createInteractableRenewables();
        createTileRenewables();
    }

    // Creates all characters.
    private void createCharRenewables() {
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
                // Creates the character based on the spawn.
                if (object.getProperties().containsKey("beetlespawn")) {
                    Beetle beetle = new Beetle(screen, map, screen.characterAtlases.get(2));
                    beetle.setSpawn(x - beetle.getWidth() / 2, y - beetle.getHeight() / 2);
                    characterList.add(beetle);
                }
                if (object.getProperties().containsKey("dummyspawn")) {
                    Dummy dummy = new Dummy(screen, map, screen.characterAtlases.get(2));
                    dummy.setSpawn(x - dummy.getWidth() / 2, y - dummy.getHeight() / 2);
                    characterList.add(dummy);
                }
                if (object.getProperties().containsKey("dollspawn")) {
                    Doll doll = new Doll(screen, map, screen.characterAtlases.get(2));
                    doll.setSpawn(x - doll.getWidth() / 2, y - doll.getHeight() / 2);
                    characterList.add(doll);
                }
                if (object.getProperties().containsKey("slimespawn")) {
                    Slime slime = new Slime(screen, map, screen.characterAtlases.get(2));
                    slime.setSpawn(x - slime.getWidth() / 2, y - slime.getHeight() / 2);
                    characterList.add(slime);
                }
                if (object.getProperties().containsKey("wizardspawn")) {
                    Wizard wizard = new Wizard(screen, map, screen.characterAtlases.get(2));
                    wizard.setSpawn(x - wizard.getWidth() / 2, y - wizard.getHeight() / 2);
                    characterList.add(wizard);
                }
                if (object.getProperties().containsKey("pantomimespawn")) {
                    Pantomime pantomime = new Pantomime(screen, map, screen.characterAtlases.get(2));
                    pantomime.setSpawn(x - pantomime.getWidth() / 2, y - pantomime.getHeight() / 2);
                    characterList.add(pantomime);
                }
                if (object.getProperties().containsKey("woodenstatuespawn")) {
                    WoodenStatue woodenStatue = new WoodenStatue(screen, map, screen.characterAtlases.get(2));
                    woodenStatue.setSpawn(x - woodenStatue.getWidth() / 2, y - woodenStatue.getHeight() / 2);
                    characterList.add(woodenStatue);
                }
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

                if (object.getProperties().containsKey("greenblock")) {
                    GreenBlock block = new GreenBlock(screen, map, screen.miscAtlases.get(0), storage,
                            rectObject.getProperties().get("greenblock").toString(),
                            object.getProperties().containsKey("limited"));
                    block.setSpawn(x - block.getWidth() / 2, y - block.getHeight() / 2);
                    screen.interactables.add(block);
                }
            }
    }

    // Same but for tile objects.
    private void createTileRenewables() {
        ArrayList<TiledMapTileLayer.Cell> tempCells = new ArrayList<TiledMapTileLayer.Cell>();

        for (TiledMapTileLayer.Cell cell : cells.get("Spring Bush")) {
            cell.setTile(screen.springBushTile);
            tempCells.add(cell);
        }

        for (TiledMapTileLayer.Cell cell : tempCells)
            cells.get("Spring Bush").remove(cell);

        tempCells.clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Summer Bush")) {
            cell.setTile(screen.springBushTile);
            tempCells.add(cell);
        }

        for (TiledMapTileLayer.Cell cell : tempCells)
            cells.get("Summer Bush").remove(cell);

        tempCells.clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Fall Bush")) {
            cell.setTile(screen.springBushTile);
            tempCells.add(cell);
        }

        for (TiledMapTileLayer.Cell cell : tempCells)
            cells.get("Fall Bush").remove(cell);

        tempCells.clear();

        for (TiledMapTileLayer.Cell cell : cells.get("Winter Bush")) {
            cell.setTile(screen.springBushTile);
            tempCells.add(cell);
        }

        for (TiledMapTileLayer.Cell cell : tempCells)
            cells.get("Winter Bush").remove(cell);

        tempCells.clear();
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
        // If the character is an enemy, deletes it rom the corresponding character list.
        for (Character character : characterList)
            if (character instanceof Enemy) {
                if (this instanceof UpperWorld)
                    screen.characters1.remove(character);
                else if (this instanceof UnderWorld)
                    screen.characters2.remove(character);
                else
                    screen.characters3.remove(character);
            }
        // Same but for all interactables.
        ArrayList<Interactable> interactableList = new ArrayList<Interactable>();

        for (Interactable interactable : screen.interactables)
            interactableList.add(interactable);

        for (Interactable interactable : interactableList)
            screen.interactables.remove(interactable);
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
            if (cell.equals(checkCell)){
                cell.setTile(screen.fallBushTile);
                cells.get("Fall Bush").remove(cell);
            }
        for (TiledMapTileLayer.Cell checkCell : cells.get("Winter Bush"))
            if (cell.equals(checkCell)) {
                cell.setTile(screen.winterBushTile);
                cells.get("Winter Bush").remove(cell);
            }

    }

    // Creates four new array lists, one for each type of bush.
    private void createCellMap() {
        cells.put("Spring Bush", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Summer Bush", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Fall Bush", new ArrayList<TiledMapTileLayer.Cell>());
        cells.put("Winter Bush", new ArrayList<TiledMapTileLayer.Cell>());
    }

    // If the player is beyond a certain point (the boundaries of a cell), the camera will pan over to the new cell.
    public void checkCameraChange() {
        if (screen.daur.getX() + screen.daur.getWidth() / 2 > layer.getTileWidth() * 10 * cellX) {
            cellX++;
            setCameraPosition(true, true);
        } else if (screen.daur.getX() + screen.daur.getWidth() / 2 < layer.getTileWidth() * 10 * (cellX - 1)) {
            cellX--;
            setCameraPosition(true, false);
        }
        if (screen.daur.getY() + screen.daur.getHeight() / 2 > layer.getTileWidth() * 10 * cellY) {
            cellY++;
            setCameraPosition(false, true);
        } else if (screen.daur.getY() + screen.daur.getHeight() / 2 < layer.getTileWidth() * 10 * (cellY - 1)) {
            cellY--;
            setCameraPosition(false, false);
        }
    }

    // This causes the panning effect when the camera moves a cell.
    protected void setCameraPosition(boolean onXAxis, boolean plus) {
        float deltaTime = 0;

        if (onXAxis) {
            if (plus)
                // Using a delayed for loop, pans the camera to the right.
                for (float x = camera.position.x; x <= cellX * 10 * layer.getTileWidth() - camera.viewportWidth / 2; x++) {
                    final float newX = x;
                    Timer timer = new Timer();
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(newX, camera.position.y, 0);
                            // Ensures that the UI pans with the camera, rather than jumps.
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    timer.start();
                    deltaTime += 0.001f;
                }
            else
                for (float x = camera.position.x; x >= cellX * 10 * layer.getTileWidth() - camera.viewportWidth / 2; x--) {
                    final float newX = x;
                    Timer timer = new Timer();
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(newX, camera.position.y, 0);
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    timer.start();
                    deltaTime += 0.001f;
                }
        } else {
            if (plus)
                for (float y = camera.position.y; y <= cellY * 10 * layer.getTileHeight() - camera.viewportHeight / 2 + 16; y++) {
                    final float newY = y;
                    Timer timer = new Timer();
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(camera.position.x, newY, 0);
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    timer.start();
                    deltaTime += 0.001f;
                }
            else {
                for (float y = camera.position.y; y >= cellY * 10 * layer.getTileHeight() - camera.viewportHeight / 2 + 16; y--) {
                    final float newY = y;
                    Timer timer = new Timer();
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            camera.position.set(camera.position.x, newY, 0);
                            for (UI ui : screen.UIS)
                                ui.renewPosition();
                        }
                    }, deltaTime);
                    timer.start();
                    deltaTime += 0.001f;
                }
            }
        }
        // Resets the position of the max, informs the program which cell the player resides in, checks if a shader
        // transition is needed, and sets Daur's new spawn point (if the player should fall down a hole).
        screen.mask.setPosition(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2);
        storage.setCells(cellX, cellY);
        checkShaderTransition();
        setSpawnPoint(onXAxis, plus);
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
        if (shaderCells.get("fwin1").equals(cells) || shaderCells.get("fwin2").equals(cells))
            screen.setCurrentMapShader(new ShaderProgram(Gdx.files.internal("Shaders/faron.vert"), Gdx.files.internal("Shaders/faron.frag")));
        else if (shaderCells.get("fwout1").equals(cells) || shaderCells.get("fwout2").equals(cells))
            screen.setCurrentMapShader(null);
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
                cellX = i + 1;
        for (int i = 0; i < 16; i++)
            if (screen.daur.getY() + screen.daur.getHeight() / 2 > layer.getTileWidth() * 10 * i)
                cellY = i + 1;

        camera.position.set(cellX * 10 * layer.getTileWidth() - camera.viewportWidth / 2, cellY * 10 * layer.getTileHeight() - camera.viewportHeight / 2 + 16, 0);
        screen.mask.setPosition(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2);
    }

    public void addCell(String key, TiledMapTileLayer.Cell value) {
        cells.get(key).add(value);
    }

    abstract void createCharacters();

    abstract void setShaderTransitions();
}
