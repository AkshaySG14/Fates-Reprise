
package com.inoculates.fatesreprise;

import com.badlogic.gdx.Game;
import com.inoculates.fatesreprise.Screens.BeginningScreen;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;

public class FatesReprise extends Game {
    public FatesReprise() {

    }

    @Override
    public void create () {
        setScreen(new BeginningScreen(this));
    }
}
