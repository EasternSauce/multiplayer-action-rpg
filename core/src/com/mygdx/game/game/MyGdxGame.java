package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.CreatureParams;
import com.mygdx.game.model.creature.Enemy;
import com.mygdx.game.physics.AbilityBody;
import com.mygdx.game.physics.CreatureBody;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.AbilityRenderer;
import com.mygdx.game.renderer.CreatureRenderer;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class MyGdxGame extends Game {
    final protected GameRenderer gameRenderer = GameRenderer.of();
    final protected GamePhysics gamePhysics = GamePhysics.of();
    final protected GameStateHolder gameStateHolder = GameStateHolder.of(GameState.of());
    final MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();
    public EndPoint _endPoint = null;

    public Chat chat = Chat.of();
    protected CreatureId thisPlayerId = null;

    final List<CreatureId> creaturesToBeCreated = new LinkedList<>();
    final List<AbilityId> abilitiesToBeCreated = new LinkedList<>();

    final List<CreatureId> creaturesToBeRemoved = new LinkedList<>();
    final List<AbilityId> abilitiesToBeRemoved = new LinkedList<>();

    final Map<CreatureId, Vector2> creaturesToTeleport = new HashMap<>();


    public List<CreatureId> creaturesToBeCreated() {
        return creaturesToBeCreated;
    }

    public List<AbilityId> abilitiesToBeCreated() {
        return abilitiesToBeCreated;
    }

    public List<CreatureId> creaturesToBeRemoved() {
        return creaturesToBeRemoved;
    }

    public List<AbilityId> abilitiesToBeRemoved() {
        return abilitiesToBeRemoved;
    }

    public Map<CreatureId, Vector2> creaturesToTeleport() {
        return creaturesToTeleport;
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

    public boolean isRenderingAllowed() {
        return true;
    }

    @Override
    public void create() {
        playScreen.init(this);
        setScreen(playScreen);
    }

    public void createCreatureBodyAndAnimation(CreatureId creatureId) {
        Creature creature = gameState().creatures().get(creatureId);

        if (creature != null) {
            if (!gameRenderer.creatureRenderers().containsKey(creatureId)) {
                CreatureRenderer creatureRenderer = CreatureRenderer.of(creatureId);
                creatureRenderer.init(gameRenderer.atlas(), gameState());
                gameRenderer.creatureRenderers().put(creatureId, creatureRenderer);
            }
            if (!gamePhysics.creatureBodies().containsKey(creatureId)) {
                CreatureBody creatureBody = CreatureBody.of(creatureId);
                creatureBody.init(gamePhysics, gameState());
                gamePhysics.creatureBodies().put(creatureId, creatureBody);
            }
        }
    }

    public void createAbilityBodyAndAnimation(AbilityId abilityId) {
        Ability ability = gameState().abilities().get(abilityId);

        if (ability != null) {
            if (!gameRenderer.abilityRenderers().containsKey(abilityId)) {
                AbilityRenderer abilityRenderer = AbilityRenderer.of(abilityId);
                abilityRenderer.init(gameRenderer.atlas(), gameState());
                gameRenderer.abilityRenderers().put(abilityId, abilityRenderer);
            }
            if (!gamePhysics.abilityBodies().containsKey(abilityId)) {
                AbilityBody abilityBody = AbilityBody.of(abilityId);
                abilityBody.init(gamePhysics, gameState());
                gamePhysics.abilityBodies().put(abilityId, abilityBody);
            }
        }

    }

    public void spawnEnemy(CreatureId creatureId, AreaId areaId, Vector2 pos, String enemyType) {
        gameState().creatures().put(creatureId,
                Enemy.of(CreatureParams.of(creatureId, areaId, pos, enemyType).speed(5f)));

        synchronized (creaturesToBeCreated()) {
            creaturesToBeCreated().add(creatureId);

        }
    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

    abstract public void initState();

    public void removeCreatureBodyAndAnimation(CreatureId creatureId) {
        gameState().creatures().remove(creatureId);

        renderer().creatureRenderers().remove(creatureId);

        if (physics().creatureBodies().containsKey(creatureId)) {
            physics().creatureBodies().get(creatureId).onRemove();
            physics().creatureBodies().remove(creatureId);
        }
    }

    public void removeAbilityBodyAndAnimation(AbilityId abilityId) {
        gameState().abilities().remove(abilityId);

        renderer().abilityRenderers().remove(abilityId);

        if (physics().abilityBodies().containsKey(abilityId)) {
            physics().abilityBodies().get(abilityId).onRemove();
            physics().abilityBodies().remove(abilityId);
        }
    }

    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget,
                                   String abilityType) {
    }

    public void chainAbility(Ability ability, String abilityType) {

    }

    public abstract void updateCreaturesAndAbilites(float delta, MyGdxGame game);
}
