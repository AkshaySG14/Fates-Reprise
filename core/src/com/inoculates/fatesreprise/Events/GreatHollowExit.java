package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

/*
 * Created by akshaysubramaniam on 11/9/15.
 */

public class GreatHollowExit extends Event {

    public GreatHollowExit(TiledMap map, GameScreen screen, Storage storage) {
        super(screen, map);
        startEvent();
    }

    protected void startEvent() {
        // Sets the dungeon to none so that the game knows Daur is not in a dungeon.
        screen.storage.setDungeon(-1);
    }

    protected void message() {

    }

}
