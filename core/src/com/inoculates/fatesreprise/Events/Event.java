package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Super class for all the events, to streamline class calling.
public abstract class Event {
    GameScreen screen;
    TiledMap map;
    TiledMapTileLayer layer;

    int stage = 0;

    public Event(GameScreen screen, TiledMap map) {
        this.map = map;
        this.screen = screen;
        layer = (TiledMapTileLayer) map.getLayers().get(0);
    }

    abstract void startEvent();

    abstract void message();

    // This is when the player presses the F button to advance the dialogue.
    public void proceed() {
        stage++;
        message();
    }
}
