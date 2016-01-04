package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

public class SignEvents extends Event {
    int sign;
    String message = null;

    // This is the event that causes the dialogue for signs.
    public SignEvents(TiledMap map, GameScreen screen, int sign) {
        super(screen, map);
        this.sign = sign;
        startEvent();
    }

    protected void startEvent() {
        setMessage();
    }

    // Sets the message depending on the value of the sign.
    private void setMessage() {
        switch (sign) {
            case 0:
                message = "Head east to Carthell Square, north east to the Marketplace, south to Aragoth's Fountain, and " +
                        "far north east to Carthell Library.";
                break;
            case 1:
                message = "South to Faron Woods.";
                break;
            case 2:
                message = "Follow the road south to Aniga Ruins.";
                break;
            case 3:
                message = "Caution! This road leads to Geliga Swamp.";
                break;
            case 4:
                message = "Road to Mt. Morrow. Passage currently blocked.";
                break;
            case 5:
                message = "Here lie the primordial stones of the north, south, east, and west.";
                break;
            case 6:
                message = "Lairon looks to the North.";
                break;
            case 7:
                message = "Corsa watches the East.";
                break;
            case 8:
                message = "Gori gazes upon the South.";
                break;
            case 9:
                message = "Marka sees the West.";
                break;
        }
        message();
    }

    protected void message() {
        switch (stage) {
            case 0:
                Dialogue dialogue = new Dialogue(screen, message, this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.forceState(0);
                screen.daur.freeze();
                screen.daur.stun();
                break;
            case 1:
                screen.setText(null, null);
                screen.daur.unStun();
        }
    }
}
