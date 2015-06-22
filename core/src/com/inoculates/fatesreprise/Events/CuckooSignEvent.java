package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

// Same as the book event but for the cuckoo sign.
public class CuckooSignEvent extends Event {
    String message = null;

    public CuckooSignEvent(TiledMap map, GameScreen screen) {
        super(screen, map);
        startEvent();
    }

    protected void startEvent() {
        setMessage();
    }

    private void setMessage() {
        // Sets the message for the cuckoo sign.
        message = "The Wayward Cuckoo.";
        message();
    }

    protected void message() {
        switch (stage) {
            case 0:
                Dialogue dialogue = new Dialogue(screen, message, this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                screen.daur.forceState(0);
                break;
            case 1:
                screen.setText(null, null);
                screen.daur.unStun();
        }
    }
}
