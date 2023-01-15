package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.GameRenderer;

import java.io.IOException;
import java.util.HashMap;

public abstract class MyGdxGame extends Game {
    protected GameRenderer gameRenderer = GameRenderer.of();

    protected GamePhysics gamePhysics = GamePhysics.of();

    protected GameState gameState = GameState.of();

    final MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();

    protected CreatureId thisPlayerId = null;
    public EndPoint _endPoint = null;

    public Chat chat = Chat.of();


    public EndPoint endPoint() {
        return _endPoint;
    }

    public boolean isInitialized() {
        return true;
    }

    @Override
    public void create() {
        gameState.creatures(new HashMap<>());
        gameState.areas(new HashMap<>());
        gameState.defaultAreaId(AreaId.of("area1"));
        gameState.currentAreaId(AreaId.of("area1"));

        playScreen.init(this);

        try {
            establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        setScreen(playScreen);
    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;
}
