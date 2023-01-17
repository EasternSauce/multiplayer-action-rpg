package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Enemy;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;

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

        try {
            establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        setScreen(playScreen);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (chat.isTyping() && character != '\n') {
                    chat.currentMessage(chat.currentMessage() + character);
                }

                return true;
            }
        });

        System.out.println("adding enemy");
        CreatureId enemyId = CreatureId.of("Enemy_" + Math.abs(rand.nextInt()));
        gameStateHolder.gameState().creatures().put(enemyId, Enemy.of(
                CreatureParams.of(enemyId, gameStateHolder.gameState().defaultAreaId(), Vector2.of(18, 10),
                        "skeleton")));

    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;
}
