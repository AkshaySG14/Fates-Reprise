package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

// This is the event responsible for the dialogue created when Daur wants to read a book.
public class BookEvents extends Event {
    int sign;
    String message = null;

    public BookEvents(TiledMap map, GameScreen screen, int sign) {
        super(screen, map);
        this.sign = sign;
        startEvent();
    }

    protected void startEvent() {
        setMessage();
    }

    private void setMessage() {
        switch (sign) {
            case 0:
                // If the book is of type one.
                message = "You grab a book from the shelf and begin to read... " +
                        "Unfortunately, the text is too advanced to comprehend.";
                break;
            case 1:
                // If the book is of type two.
                message = "You grab a book from the shelf and begin to read... " +
                        "The book contains largely useless information that your mind quickly discards.";
                break;
            case 2:
                // Type three.
                message = "You grab a book from the shelf and begin to read... " +
                        "The book's information, while interesting, is irrelevant for now.";
                break;
            case 3:
                // Unfinished messages.
                message = "Gilyard Swamp lies east. Tread lightly. Monsters and bog abound.";
                break;
            case 4:
                message = "Trail to Mt. Rudin. Path blocked.";
                break;
        }
        message();
    }

    // Creates the dialogue itself.
    protected void message() {
        switch (stage) {
            case 0:
                // Launches the dialogue.
                Dialogue dialogue = new Dialogue(screen, message, this);
                // Sets the text of the dialogue.
                screen.setText(dialogue, dialogue.getBackground());
                // Stuns Daur and forces an idle state.
                screen.daur.stun();
                screen.daur.freeze();
                screen.daur.forceState(0);
                break;
            case 1:
                // Once Daur presses F, erases the dialogue.
                screen.setText(null, null);
                screen.daur.unStun();
        }
    }
}
