package com.mygdx.game.model.game;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;

import java.io.IOException;

public abstract class MyGdxGame extends Game {
    protected GameRenderer gameRenderer = GameRenderer.of();

    final MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();

    protected CreatureId thisPlayerId = null;
    public EndPoint _endPoint = null;

    public GameState gameState;

    public EndPoint endPoint() {
        return _endPoint;
    }


    @Override
    public void create() {
        gameState = GameState.builder()
                .defaultAreaId(AreaId.of("zzz"))
                .build();

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
