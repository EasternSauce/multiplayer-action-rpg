package com.mygdx.game.model.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class MyGdxGame extends Game {

    public MyGdxGamePlayScreen playScreen = new MyGdxGamePlayScreen(this);
    Map<CreatureId, Sprite> creatureSprites;

    SpriteBatch batch;
    Texture img;

    public EndPoint _endPoint = null;

    public GameState gameState;

    public EndPoint endPoint() {
        return _endPoint;
    }

    ;

    @Override
    public void create() {
        gameState = GameState.builder()
                .defaultAreaId(AreaId.of("zzz"))
                .build();

        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        creatureSprites = new HashMap<>();

        try {
            establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        playScreen.init();

    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    public void onRender() {


        creatureSprites.forEach((creatureId, sprite) -> {
            if (gameState.getCreatures().containsKey(creatureId)) {
                sprite.setPosition(gameState.getCreatures().get(creatureId).getParams().getPos().getX(), gameState.getCreatures().get(creatureId).getParams().getPos().getY());
            }
        });
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();

//        if (!gameState.getCreatures().isEmpty()) {
//            CreatureId id = (CreatureId) (gameState.getCreatures().keySet().toArray()[0]);
//            Creature creature = gameState.getCreatures().get(id);
//            batch.draw(img, creature.getParams().getPos().getX(), creature.getParams().getPos().getY());
//        }

        creatureSprites.forEach((creatureId, sprite) -> {
            sprite.draw(batch);
        });

        batch.end();
    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;
}
