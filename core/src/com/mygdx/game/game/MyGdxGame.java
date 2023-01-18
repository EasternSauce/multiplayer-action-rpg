package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.GameStateHolder;

import java.io.IOException;
import java.util.Random;

public abstract class MyGdxGame extends Game {
    protected GameRenderer gameRenderer = GameRenderer.of();

    protected GamePhysics gamePhysics = GamePhysics.of();

    final protected GameStateHolder gameStateHolder = GameStateHolder.of(GameState.of());

    public final Random rand = new Random();
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
        playScreen.init(this);
        setScreen(playScreen);
    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

    abstract public void initState();
}
