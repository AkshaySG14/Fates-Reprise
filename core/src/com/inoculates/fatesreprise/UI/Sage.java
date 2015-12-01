package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.fatesreprise.Screens.GameScreen;

// Heart UI that represents the health of Daur.
public class Sage extends UI {
    private GameScreen screen;
    private TextureAtlas atlas;
    private int type = 0;

    // Note the heart integer, which is used to displace the heart UI by a certain amount. This allows for the creation
    // of multiple hearts
    public Sage(GameScreen screen, TextureAtlas atlas, int type) {
        super(atlas.findRegion("Druni1"));
        this.atlas = atlas;
        this.screen = screen;
        this.type = type;
        setAppearance();
        setScale(0.75f);
    }

    // Sets the sprite of the sage depending on the type and whether Daur has rescued the sage. Also sets the positioning.
    private void setAppearance() {
        switch (type) {
            case 0:
                // This sage is supposed to represent Druni.
                // If the sage is rescued.
                if (screen.storage.sages[0])
                    setRegion(atlas.findRegion("Druni2"));
                else
                    // If not.
                    setRegion(atlas.findRegion("Druni1"));
                // Sets position if Druni.
                setPosition(80, 60);
                break;
            case 1:
                // This sage is supposed to represent Khalin.
                if (screen.storage.sages[1])
                    setRegion(atlas.findRegion("Khalin2"));
                else
                    setRegion(atlas.findRegion("Khalin1"));
                setPosition(95, 70);
                break;
            case 2:
                // This sage is supposed to represent Laylia.
                if (screen.storage.sages[2])
                    setRegion(atlas.findRegion("Laylia2"));
                else
                    setRegion(atlas.findRegion("Laylia1"));
                setPosition(115, 70);
                break;
            case 3:
                // This sage is supposed to represent Ragnor.
                if (screen.storage.sages[3])
                    setRegion(atlas.findRegion("Ragnor2"));
                else
                    setRegion(atlas.findRegion("Ragnor1"));
                setPosition(130, 60);
                break;
            case 4:
                // This sage is supposed to represent Voorhe.
                if (screen.storage.sages[4])
                    setRegion(atlas.findRegion("Voorhe2"));
                else
                    setRegion(atlas.findRegion("Voorhe1"));
                setPosition(115, 50);
                break;
            case 5:
                // This sage is supposed to represent Xalo.
                if (screen.storage.sages[5])
                    setRegion(atlas.findRegion("Xalo2"));
                else
                    setRegion(atlas.findRegion("Xalo1"));
                setPosition(95, 50);
                break;
        }
    }

    public int getType() {
        return type;
    }

    public void renewPosition() {
    }

}
