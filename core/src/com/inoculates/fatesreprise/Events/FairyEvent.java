package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Items.GreatHollowKey;
import com.inoculates.fatesreprise.Items.Item;
import com.inoculates.fatesreprise.Items.OakStaff;
import com.inoculates.fatesreprise.Items.WindSickleItem;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.Text.Dialogue;

// These are the events that are fired when Daur wishes to talk to the Fairy Queen.
public class FairyEvent extends Event {
    Storage storage;

    String message = null;
    boolean shop = false;

    public FairyEvent(TiledMap map, GameScreen screen, Storage storage) {
        super(screen, map);
        this.storage = storage;
        startEvent();
    }

    protected void startEvent() {
        message();
    }

    protected void message() {
        Dialogue dialogue;
        ItemAcquisitionEvents aqItems;

        switch (storage.fairyQueenStage) {
            // First meeting.
            case 0:
                switch (stage) {
                case 0:
                    dialogue = new Dialogue(screen, "Greetings wanderer. It seems I am in a bit of a predicament, and seeing as you are the only one " +
                            "capable of assisting me, I must count on you to get me out of it. Unfortunately, my staff has " +
                            "recently been taken from me. Stolen in fact. Naturally, the perpetrator, whom I can only assume " +
                            "is some middling fool, has hid it in one of the many caves here in Faron Wood. Seeing as I am " +
                            "incapable of using my powers without my staff, I implore you to recover it for me. Of course, I " +
                            "shall reward you handsomely. I know you require something in the Great Hollow. Whatever it is, you " +
                            "need the key to enter it first. As I am the only trustee of this key, fulfilling my task is the " +
                            "only manner through which you may achieve your goals. Now, I shall bequeath to you a spell that will " +
                            "assist in your task.",
                            this);
                    screen.setText(dialogue, dialogue.getBackground());
                    screen.daur.stun();
                    screen.pauseScreen();
                    break;
                case 1:
                    // Gives Daur wind scythe spell.
                    aqItems = new ItemAcquisitionEvents(map, screen, WindSickleItem.class, this);
                    break;
                case 2:
                    dialogue = new Dialogue(screen, "Very well. I gave you the tool for the job. Now get to it.",
                            this);
                    screen.setText(dialogue, dialogue.getBackground());
                    screen.daur.stun();
                    break;
                case 3:
                    screen.setText(null, null);
                    screen.daur.unStun();
                    screen.unPauseScreen();
                    // Note that this INCREMENTS the fairy queen stage so that she does not say the same words over and
                    // over again.
                    storage.setFairyQueenStage();
                    break;
            }
                break;
            // Second meeting but no staff in inventory.
            case 1:
                switch (stage) {
                    case 0:
                        dialogue = new Dialogue(screen, "Well? Where is my staff. If you do not have it, I would request " +
                                "you stop wasting both my time and yours and go find it.",
                                this);
                        screen.setText(dialogue, dialogue.getBackground());
                        screen.daur.stun();
                        screen.pauseScreen();
                        break;
                    case 1:
                        screen.setText(null, null);
                        screen.daur.unStun();
                        screen.unPauseScreen();
                        // Note no increment of fairy queen stage.
                        break;
                }
                break;
            // Subsequent meetings with staff in inventory.
            case 2:
                switch (stage) {
                    case 0:
                        dialogue = new Dialogue(screen, "Hmph. I see my staff has been reacquired. Your actions are " +
                                "most... commendable. I suppose I must keep my end of the bargain. Well, here it is.",
                                this);
                        screen.setText(dialogue, dialogue.getBackground());
                        screen.daur.stun();
                        screen.pauseScreen();
                        break;
                    case 1:
                        // Gives Daur the key to the Great Hollow.
                        aqItems = new ItemAcquisitionEvents(map, screen, GreatHollowKey.class, this);
                        // Removes staff from inventory.
                        removeStaff();
                        break;
                    case 2:
                        dialogue = new Dialogue(screen, "I believe that fulfills my part. Now go, and do what you must. " +
                                "I wish you luck.",
                                this);
                        screen.setText(dialogue, dialogue.getBackground());
                        screen.daur.stun();
                        break;
                    case 3:
                        screen.setText(null, null);
                        screen.daur.unStun();
                        screen.unPauseScreen();
                        // Note that this INCREMENTS the fairy queen stage so that she does not say the same words over and
                        // over again.
                        storage.setFairyQueenStage();
                        break;
                }
                break;
            // After bequeathing the key, the Fairy Queen will say the same thing over and over again.
            case 3:
                switch (stage) {
                    case 0:
                        dialogue = new Dialogue(screen, "I do not believe we have anything more to discuss.",
                                this);
                        screen.setText(dialogue, dialogue.getBackground());
                        screen.daur.stun();
                        screen.pauseScreen();
                        break;
                    case 1:
                        screen.setText(null, null);
                        screen.daur.unStun();
                        screen.unPauseScreen();
                        // Note no increment of fairy queen stage.
                        break;
                }
                break;
        }
    }

    // Removes the oak staff from Daur's inventory.
    private void removeStaff() {
        Item staff = null;
        for (Item item : screen.storage.questItems)
            if (item instanceof OakStaff)
                staff = item;
        screen.storage.questItems.remove(staff);
    }
}
