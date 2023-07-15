package com.easternsauce.actionrpg.launcher;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.easternsauce.actionrpg.game.client.CoreGameClient;
import com.easternsauce.actionrpg.util.Constants;

public class DesktopClientLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        config.setTitle("My GDX Game");

        new Lwjgl3Application(CoreGameClient.getInstance(), config);
    }
}
