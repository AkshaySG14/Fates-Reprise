package com.inoculates.fatesreprise.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.inoculates.fatesreprise.FatesReprise;

// The class that launches the desktop application. Functional and responsible for the jar file.
public class DesktopLauncher {
	public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new FatesReprise(), config);
	}
}
