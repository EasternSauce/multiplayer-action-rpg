package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.ability.AbilityType;
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
import java.util.*;

public abstract class MyGdxGame extends Game {
    final protected GameRenderer gameRenderer = GameRenderer.of();
    final protected GamePhysics gamePhysics = GamePhysics.of();
    final protected GameStateHolder gameStateHolder = GameStateHolder.of(GameState.of());
    final MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();
    final public EndPoint _endPoint = null;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean debug = false;
    public final Chat chat = Chat.of();
    protected CreatureId thisPlayerId = null;

    final List<CreatureId> creaturesToBeCreated = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeCreated = Collections.synchronizedList(new ArrayList<>());

    final List<CreatureId> creaturesToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeRemoved = Collections.synchronizedList(new ArrayList<>());

    final Map<CreatureId, Vector2> creaturesToTeleport = new HashMap<>();


    public Boolean debug() {
        return debug;
    }

    public CreatureId thisPlayerId() {
        return thisPlayerId;
    }

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

    public void createCreature(CreatureId creatureId) {
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

    public void createAbility(AbilityId abilityId) {
        Ability ability = gameState().abilities().get(abilityId);

        if (ability != null) {
            if (!gameRenderer.abilityRenderers().containsKey(abilityId)) {
                AbilityRenderer abilityRenderer = AbilityRenderer.of(abilityId);
                abilityRenderer.init(gameRenderer.atlas(), gameState());
                gameRenderer.abilityRenderers().put(abilityId, abilityRenderer);
            }
            if (!gamePhysics.abilityBodies().containsKey(abilityId)) {
                AbilityBody abilityBody = AbilityBody.of(abilityId);
                abilityBody.init(gamePhysics, gameState(), ability.params().inactiveBody());
                gamePhysics.abilityBodies().put(abilityId, abilityBody);
            }
        }

    }

    public void spawnEnemy(CreatureId creatureId, AreaId areaId, Vector2 pos, String enemyType) {
        gameState().creatures()
                   .put(creatureId, Enemy.of(CreatureParams.of(creatureId, areaId, pos, enemyType).speed(5f)));

        creaturesToBeCreated().add(creatureId);


    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

    abstract public void initState();

    abstract public Set<CreatureId> creaturesToUpdate();

    abstract public Set<AbilityId> abilitiesToUpdate();

    public void removeCreature(CreatureId creatureId) {
        gameState().creatures().remove(creatureId);

        renderer().creatureRenderers().remove(creatureId);

        if (physics().creatureBodies().containsKey(creatureId)) {
            physics().creatureBodies().get(creatureId).onRemove();
            physics().creatureBodies().remove(creatureId);
        }
    }

    public void removeAbility(AbilityId abilityId) {
        gameState().abilities().remove(abilityId);

        renderer().abilityRenderers().remove(abilityId);

        if (physics().abilityBodies().containsKey(abilityId)) {
            physics().abilityBodies().get(abilityId).onRemove();
            physics().abilityBodies().remove(abilityId);
        }
    }

    public void handleAttackTarget(CreatureId attackingCreatureId,
                                   Vector2 vectorTowardsTarget,
                                   AbilityType abilityType) {
    }


    abstract public void chainAbility(Ability chainFromAbility,
                                      AbilityType abilityType,
                                      Vector2 chainToPos,
                                      CreatureId creatureId);


    public void updateCreatures(float delta, MyGdxGame game) {
        Set<CreatureId> creaturesToUpdate = creaturesToUpdate();

        creaturesToUpdate.forEach(creatureId -> {
            if (game.physics().creatureBodies().containsKey(creatureId)) {
                game.physics().creatureBodies().get(creatureId).update(game.gameState());
            }
        });

        // set gamestate position based on b2body position
        creaturesToUpdate.forEach(creatureId -> {
            if (game.gameState().creatures().containsKey(creatureId) && game.physics()
                                                                            .creatureBodies()
                                                                            .containsKey(creatureId)) {
                game.gameState()
                    .creatures()
                    .get(creatureId)
                    .params()
                    .pos(game.physics().creatureBodies().get(creatureId).getBodyPos());
            }
        });

        // if creature is to be updated, then body should be active, otherwise it should be inactive
        game.gamePhysics.creatureBodies()
                        .forEach((key, value) -> game.gamePhysics.creatureBodies()
                                                                 .get(key)
                                                                 .setActive(creaturesToUpdate.contains(key)));

        creaturesToUpdate.forEach(creatureId -> {
            if (game.renderer().creatureRenderers().containsKey(creatureId)) {
                game.renderer().creatureRenderers().get(creatureId).update(game.gameState());
            }
        });

        creaturesToUpdate.forEach(creatureId -> {
            if (game.gameState().creatures().containsKey(creatureId)) {
                game.gameState().creatures().get(creatureId).update(delta, game);
            }
        });

    }

    public void updateAbilities(float delta, MyGdxGame game) {
        Set<AbilityId> abilitiesToUpdate = abilitiesToUpdate();

        abilitiesToUpdate.forEach(abilityId -> {
            if (game.physics().abilityBodies().containsKey(abilityId)) {
                game.physics().abilityBodies().get(abilityId).update(game.gameState());
            }
        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (game.physics().abilityBodies().containsKey(abilityId)) {
                Ability ability = game.gameState().abilities().get(abilityId);
                if (!ability.params().inactiveBody()) {
                    ability.params().pos(game.physics().abilityBodies().get(abilityId).getBodyPos());
                }

            }

        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (game.renderer().abilityRenderers().containsKey(abilityId)) {
                game.renderer().abilityRenderers().get(abilityId).update(game.gameState());
            }
        });


        abilitiesToUpdate.forEach(abilityId -> game.gameState().abilities().get(abilityId).update(delta, game));


    }

    public CreatureId aliveCreatureClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded) {

        CreatureId minCreatureId = null;
        float minDistance = Float.MAX_VALUE;
        for (CreatureId creatureId : creaturesToUpdate()) {
            Creature creature = gameState().creatures().get(creatureId);
            float distance = pos.distance(creature.params().pos());
            if (creature.isAlive() && distance < minDistance && distance < maxRange && !excluded.contains(creatureId)) {
                minDistance = distance;
                minCreatureId = creatureId;
            }
        }
        return minCreatureId;
    }
}
