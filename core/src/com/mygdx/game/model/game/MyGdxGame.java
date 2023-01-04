package com.mygdx.game.model.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;

import java.io.IOException;

public abstract class MyGdxGame extends Game {
    protected GameRenderer renderer = GameRenderer.of();

    MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();

    protected Texture img;

    TextureAtlas atlas;

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

        img = new Texture("badlogic.jpg");

        atlas = new TextureAtlas("assets/atlas/packed_atlas.atlas");

        try {
            establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        playScreen.init(this);

        setScreen(playScreen);
    }

    @Override
    public void dispose() {
//        batch.dispose();
        img.dispose();
    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;
}
