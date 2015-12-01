package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.Text.Dialogue;

/*
 * Created by akshaysubramaniam on 11/9/15.
 */

public class GreatHollowDialogue extends Event {
    private String message;

    public GreatHollowDialogue(TiledMap map, GameScreen screen, Storage storage) {
        super(screen, map);
        startEvent();
        storage.FDstorage.beginGreatHollowDialogue();
        // Sets the dungeon so that the game knows which dungeon Daur is in.
        storage.setDungeon(0);
    }

    protected void startEvent() {
        message();
    }

    protected void message() {
        Dialogue dialogue;
        // Begins dialogue.
        switch (stage) {
            case 0:
                dialogue = new Dialogue(screen, "Level One - The Great Hollow", this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                // Freezes Daur.
                screen.daur.freeze();
                screen.pauseScreen();
                break;
            // Breaks dialogue.
            case 1:
                screen.setText(null, null);
                screen.daur.unStun();
                screen.unPauseScreen();
                break;
        }
    }

}
