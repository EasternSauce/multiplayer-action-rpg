package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.game.CoreGameServer;

public class DesktopServerLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        config.setTitle("My GDX Game");

        new Lwjgl3Application(CoreGameServer.getInstance(), config);
    }
}
