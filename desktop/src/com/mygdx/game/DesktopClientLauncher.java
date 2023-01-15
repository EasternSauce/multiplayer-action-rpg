package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.game.MyGdxGameClient;

public class DesktopClientLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(Constants.WindowWidth, Constants.WindowHeight);
        config.setTitle("My GDX Game");

        new Lwjgl3Application(MyGdxGameClient.getInstance(), config);
    }
}
