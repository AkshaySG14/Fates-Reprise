package com.inoculates.fatesreprise.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inoculates.fatesreprise.FatesReprise;

// The class that launches the desktop application. Functional and responsible for the jar file.
public class DesktopLauncher {
        public static void main(String[] arg) {
                // Creates an un-resizable, 800x600 in which the user can play.
                LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.title = "Fate's Reprise";
                config.height = 600;
                config.width = 800;
                config.resizable = false;
                new LwjglApplication(new FatesReprise(), config);
        }
}
