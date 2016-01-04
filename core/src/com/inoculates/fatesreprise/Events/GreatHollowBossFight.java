package com.inoculates.fatesreprise.Events;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.fatesreprise.Characters.MasterWizard;
import com.inoculates.fatesreprise.Characters.Messenger;
import com.inoculates.fatesreprise.Items.*;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Text.Dialogue;

public class GreatHollowBossFight extends Event {
    MasterWizard MW;

    // This is the event that fires off before the boss fight begins proper.
    public GreatHollowBossFight(TiledMap map, GameScreen screen) {
        super(screen, map);
        startEvent();
    }

    protected void startEvent() {
        // Sets the direciton of Daur to up and freezes the screen.
        screen.daur.setDirection(2);
        screen.daur.stun();
        screen.daur.freeze();
        screen.freeze();
        screen.globalTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                message();
            }
        }, 0.1f);
    }

    protected void message() {
        // Starts the series of events. Similar to the messenger events.
        switch (stage) {
            case 0:
                for (MapObject object : map.getLayers().get("Spawns").getObjects())
                    if (object instanceof RectangleMapObject) {
                        RectangleMapObject rectObject = (RectangleMapObject) object;
                        Rectangle rect = rectObject.getRectangle();
                        float x = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
                        float y = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
                        // Spawns the Master Wizard.
                        if (object.getProperties().containsKey("MWspawn")) {
                            MW = new MasterWizard(screen, map, screen.characterAtlases.get(2));
                            screen.characters2.add(MW);
                            MW.setSpawn(x + layer.getTileWidth() - MW.getWidth() / 2,
                                    y + layer.getTileHeight() - MW.getHeight() / 2);
                            // Stuns and then phases the Master Wizard.
                            MW.stun();
                            MW.phase(true);
                        }
                        // After one second creates the dialogue.
                        screen.globalTimer.scheduleTask(new Timer.Task() {
                            @Override
                            public void run() {
                                createDialogue();
                            }
                        }, 1);
                    }
                break;
            case 1:
                // Phases the Master Wizard Out.
                MW.phase(false);
                // Ends the event by unfreezing the screen and then spawning the boss.
                screen.setText(null, null);
                screen.globalTimer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        screen.daur.unStun();
                        MW.unStun();
                        screen.unFreeze();
                        screen.unPauseGame();
                    }
                }, 1);
        }
    }

    private void createDialogue() {
        Dialogue dialogue = new Dialogue(screen, "Hah. So you are the one who seeks to free the sages from their " +
                "imprisonment? I am the guardian of the first sage. You must get past me to free him. Ready " +
                "yourself. My face is the last one you'll ever see.", this);
        screen.setText(dialogue, dialogue.getBackground());
    }
}
