package com.inoculates.fatesreprise.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.inoculates.fatesreprise.Storage.Storage;

// Heart UI that represents the health of Daur.
public class BigHeart extends UI {
    private TextureAtlas atlas;
    private int state = 0;

    // Note the heart integer, which is used to displace the heart UI by a certain amount. This allows for the creation
    // of multiple hearts
    public BigHeart(Storage storage, TextureAtlas atlas) {
        super(atlas.findRegion("bigheart1"));
        this.atlas = atlas;
        // Sets the amount of heart pieces filled up in accordance with the amount of heart pieces the player has gathered.
        switch (storage.heartPieces) {
            case 0:
                setRegion(atlas.findRegion("bigheart1"));
                break;
            case 1:
                setRegion(atlas.findRegion("bigheart2"));
                break;
            case 2:
                setRegion(atlas.findRegion("bigheart3"));
                break;
            case 3:
                setRegion(atlas.findRegion("bigheart4"));
                break;
            case 4:
                setRegion(atlas.findRegion("bigheart5"));
                break;
        }
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void setState(int s) {
        state = s;
    }

    public void renewPosition() {

    }

}
