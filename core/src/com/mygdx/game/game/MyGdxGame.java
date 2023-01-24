package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.CreatureBody;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.CreatureAnimation;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.GameStateHolder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class MyGdxGame extends Game {
    public final Object creaturesLock = new Object();
    final protected GameRenderer gameRenderer = GameRenderer.of();
    final protected GamePhysics gamePhysics = GamePhysics.of();
    final protected GameStateHolder gameStateHolder = GameStateHolder.of(GameState.of());
    final MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();
    public EndPoint _endPoint = null;

    public Chat chat = Chat.of();
    protected CreatureId thisPlayerId = null;

    final List<CreatureId> creaturesToBeCreated = new LinkedList<>();

    public List<CreatureId> creaturesToBeCreated() {
        return creaturesToBeCreated;
    }

    public GameRenderer renderer() {
        return gameRenderer;
    }

    public GamePhysics physics() {
        return gamePhysics;
    }

    public GameState gameState() {
        return gameStateHolder.gameState();
    }

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

    public void createCreatureBodyAndAnimation(CreatureId creatureId) {
        CreatureAnimation creatureAnimation = CreatureAnimation.of(creatureId);
        creatureAnimation.init(gameRenderer.atlas(), gameState());
        gameRenderer.creatureAnimations().put(creatureId, creatureAnimation);
        CreatureBody creatureBody = CreatureBody.of(creatureId);
        creatureBody.init(gamePhysics, gameState());
        gamePhysics.creatureBodies().put(creatureId, creatureBody);
    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

    abstract public void initState();

    public void removeCreatureBodyAndAnimation(CreatureId playerId) {
        System.out.println("3!");
        gameState().creatures().remove(playerId);

        renderer().creatureAnimations().remove(playerId);

        if (physics().creatureBodies().containsKey(playerId)) {
            physics().creatureBodies().get(playerId).onRemove();
            physics().creatureBodies().remove(playerId);
        }
    }
}
