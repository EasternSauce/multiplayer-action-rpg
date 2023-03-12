package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.game.data.TeleportEvent;
import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.CreatureUpdatable;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.event.PhysicsEvent;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.renderer.AbilityRenderer;
import com.mygdx.game.renderer.CreatureRenderer;
import com.mygdx.game.renderer.GameRenderer;

import java.io.IOException;
import java.util.*;

public abstract class MyGdxGame extends Game implements AbilityUpdatable, CreatureUpdatable, GameRenderable, GameActionApplicable {
    final protected GameRenderer gameRenderer = GameRenderer.of();
    final protected GamePhysics gamePhysics = GamePhysics.of();
    protected GameState gameState = GameState.of();
    final MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebugEnabled = true;
    private final Chat chat = Chat.of();
    protected CreatureId thisPlayerId = null;

    final List<CreatureId> creaturesToBeCreated = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeCreated = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeActivated = Collections.synchronizedList(new ArrayList<>());

    final List<CreatureId> creaturesToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeRemoved = Collections.synchronizedList(new ArrayList<>());

    final List<TeleportEvent> teleportEvents = Collections.synchronizedList(new ArrayList<>());


    public Boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public CreatureId getCurrentPlayerId() {
        return thisPlayerId;
    }

    public List<CreatureId> creaturesToBeCreated() {
        return creaturesToBeCreated;
    }

    public List<AbilityId> abilitiesToBeCreated() {
        return abilitiesToBeCreated;
    }

    public List<AbilityId> abilitiesToBeActivated() {
        return abilitiesToBeActivated;
    }

    public List<CreatureId> creaturesToBeRemoved() {
        return creaturesToBeRemoved;
    }

    public List<AbilityId> abilitiesToBeRemoved() {
        return abilitiesToBeRemoved;
    }

    public List<TeleportEvent> teleportEvents() {
        return teleportEvents;
    }

    @Override
    public void addTeleportEvent(TeleportEvent teleportEvent) {
        teleportEvents.add(teleportEvent);
    }


    public GameRenderer renderer() {
        return gameRenderer;
    }

    public GamePhysics physics() {
        return gamePhysics;
    }

    public GameState gameState() {
        return gameState;
    }

    public abstract EndPoint endPoint();

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
                creatureBody.init(gamePhysics, gameState(), creature.params().areaId());
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
                if (ability.params().state() == AbilityState.ACTIVE) {
                    abilityBody.init(gamePhysics, gameState(), ability.params().inactiveBody());
                }
                gamePhysics.abilityBodies().put(abilityId, abilityBody);
            }
        }

    }

    public void activateAbility(AbilityId abilityId) {
        Ability ability = gameState().abilities().get(abilityId);

        if (ability != null && physics().abilityBodies().containsKey(ability.params().id())) {
            physics().abilityBodies()
                     .get(ability.params().id())
                     .init(physics(),
                           gameState(),
                           ability.params()
                                  .inactiveBody());
        }

    }


    public void spawnEnemy(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn) {
        gameState().creatures()
                   .put(creatureId,
                        Enemy.of(CreatureParams.of(creatureId, areaId, enemySpawn)
                                               .baseSpeed(7f)
                                               .attackDistance(enemySpawn.enemyTemplate()
                                                                         .attackDistance())
                                               .mainAttackSkill(enemySpawn.enemyTemplate()
                                                                          .mainAttackSkill())));

        creaturesToBeCreated().add(creatureId);


    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

    abstract public void initState();

    public void removeCreature(CreatureId creatureId) {
        if (creatureId != null) {
            gameState().creatures().remove(creatureId);

            renderer().creatureRenderers().remove(creatureId);

            if (physics().creatureBodies().containsKey(creatureId)) {
                physics().creatureBodies().get(creatureId).onRemove();
                physics().creatureBodies().remove(creatureId);
            }
        }
    }

    public void removeAbility(AbilityId abilityId) {

        if (abilityId != null) {

            gameState().abilities().remove(abilityId);

            renderer().abilityRenderers().remove(abilityId);

            if (physics().abilityBodies().containsKey(abilityId)) {
                physics().abilityBodies().get(abilityId).onRemove();
                physics().abilityBodies().remove(abilityId);
            }
        }
    }

    abstract public void handleAttackTarget(CreatureId attackingCreatureId,
                                            Vector2 vectorTowardsTarget,
                                            SkillType skillType);

    public void updateCreatures(float delta) {
        Set<CreatureId> creaturesToUpdate = getCreaturesToUpdate();

        creaturesToUpdate.forEach(creatureId -> {
            if (physics().creatureBodies().containsKey(creatureId)) {
                physics().creatureBodies().get(creatureId).update(gameState());
            }
        });

        // set gamestate position based on b2body position
        creaturesToUpdate.forEach(creatureId -> {
            if (gameState().creatures().containsKey(creatureId) && physics()
                    .creatureBodies()
                    .containsKey(creatureId)) {

                gameState()
                        .creatures()
                        .get(creatureId)
                        .params()
                        .pos(physics().creatureBodies().get(creatureId).getBodyPos());
            }
        });

        // if creature is to be updated, then body should be active, otherwise it should be inactive
        gamePhysics.creatureBodies()
                   .forEach((key, value) -> gamePhysics.creatureBodies()
                                                       .get(key)
                                                       .setActive(creaturesToUpdate.contains(key)));

        creaturesToUpdate.forEach(creatureId -> {
            if (renderer().creatureRenderers().containsKey(creatureId)) {
                renderer().creatureRenderers().get(creatureId).update(this);
            }
        });

        creaturesToUpdate.forEach(creatureId -> {
            if (gameState().creatures().containsKey(creatureId)) {
                gameState().creatures().get(creatureId).update(delta, this);
            }
        });

    }

    public void updateAbilities(float delta) {
        Set<AbilityId> abilitiesToUpdate = getAbilitiesToUpdate();

        abilitiesToUpdate.forEach(abilityId -> {
            if (physics().abilityBodies().containsKey(abilityId)) {
                physics().abilityBodies().get(abilityId).update(gameState());
            }
        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (physics().abilityBodies().containsKey(abilityId)) {
                Ability ability = gameState().abilities().get(abilityId);
                if (physics().abilityBodies().get(abilityId).isBodyInitialized() && ability.bodyShouldExist()) {
                    ability.params().pos(physics().abilityBodies().get(abilityId).getBodyPos());
                }

            }

        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (renderer().abilityRenderers().containsKey(abilityId)) {
                renderer().abilityRenderers().get(abilityId).update(gameState());
            }
        });


        abilitiesToUpdate.forEach(abilityId -> gameState().abilities().get(abilityId).update(delta, this));


    }

    @Override
    public CreatureId getAliveCreatureIdClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded) {

        CreatureId minCreatureId = null;
        float minDistance = Float.MAX_VALUE;
        for (CreatureId creatureId : getCreaturesToUpdate()) {
            Creature creature = gameState().creatures().get(creatureId);
            float distance = pos.distance(creature.params().pos());
            if (creature.isAlive() && distance < minDistance && distance < maxRange && !excluded.contains(creatureId)) {
                minDistance = distance;
                minCreatureId = creatureId;
            }
        }
        return minCreatureId;
    }

    @Override
    public Vector2 getCreaturePos(CreatureId creatureId) {
        if (!gameState().creatures().containsKey(creatureId)) {
            return null;
        }
        return gameState().creatures().get(creatureId).params().pos();
    }

    @Override
    public Creature getCreature(CreatureId creatureId) {
        if (creatureId == null || !gameState().creatures().containsKey(creatureId)) {
            return null;
        }
        return gameState().creatures().get(creatureId);
    }


    @Override
    public Map<CreatureId, Creature> getCreatures() {
        return gameState().creatures();
    }

    @Override
    public PhysicsWorld getPhysicsWorld(AreaId areaId) {
        return physics().physicsWorlds().get(areaId);
    }

    public AreaId getCurrentPlayerAreaId() {
        if (thisPlayerId != null && gameState.creatures().containsKey(thisPlayerId)) {
            return getCreature(thisPlayerId).params().areaId();
        }
        return gameState.defaultAreaId();
    }

    public void teleportCreature(TeleportEvent teleportEvent) {
        if (teleportEvent.toAreaId().equals(getCreature(teleportEvent.creatureId()).params().areaId())) {
            physics().creatureBodies().get(teleportEvent.creatureId()).forceSetTransform(teleportEvent.pos());
        }
        else {
            if (teleportEvent.creatureId() != null) {
                Creature creature = getCreature(teleportEvent.creatureId());

                creature.params().areaId(teleportEvent.toAreaId());

                creature.params().pos(teleportEvent.pos());
                creature.params().movementCommandTargetPos(teleportEvent.pos());

                if (physics().creatureBodies().containsKey(teleportEvent.creatureId())) {
                    physics().creatureBodies().get(teleportEvent.creatureId()).onRemove();
                    physics().creatureBodies().remove(teleportEvent.creatureId());
                }

                if (!gamePhysics.creatureBodies().containsKey(teleportEvent.creatureId())) {
                    CreatureBody creatureBody = CreatureBody.of(teleportEvent.creatureId());
                    creatureBody.init(gamePhysics, gameState(), teleportEvent.toAreaId());
                    gamePhysics.creatureBodies().put(teleportEvent.creatureId(), creatureBody);
                }

                creature.params().justTeleportedToGate(true);


            }
        }

    }

    abstract void performPhysicsWorldStep();

    @Override
    public boolean isLineOfSight(AreaId areaId, Vector2 fromPos, Vector2 toPos) {
        return physics().physicsWorlds().get(areaId).isLineOfSight(fromPos, toPos);
    }

    @Override
    public Vector3 getWorldCameraPosition() {
        return renderer().worldCamera().position;
    }

    @Override
    public void updateWorldCamera() {
        renderer().worldCamera().update();
    }

    @Override
    public Map<AbilityId, Ability> getAbilities() {
        return gameState.abilities();
    }

    @Override
    public Ability getAbility(AbilityId abilityId) {
        if (abilityId == null || !gameState().abilities().containsKey(abilityId)) {
            return null;
        }
        return gameState().abilities().get(abilityId);
    }

    @Override
    public Ability getAbility(CreatureId creatureId, SkillType skillType) {


        Optional<Ability> first = gameState.abilities()
                                           .values()
                                           .stream()
                                           .filter(ability -> ability.params().creatureId().equals(creatureId) &&
                                                              ability.params().skillType() == skillType)
                                           .findFirst();

        return first.orElse(null);
    }

    @Override
    public Vector2 getAbilityPos(AbilityId abilityId) {
        if (abilityId == null || !gameState().abilities().containsKey(abilityId)) {
            return null;
        }
        return gameState().abilities().get(abilityId).params().pos();
    }

    @Override
    public void renderB2BodyDebug() {
        if (isDebugEnabled()) {
            physics()
                    .debugRenderer()
                    .render(physics().physicsWorlds().get(getCurrentPlayerAreaId()).b2world(),
                            renderer().worldCamera().combined);
        }
    }

    @Override
    public GameRenderer getRenderer() {
        return renderer();
    }

    @Override
    public Chat getChat() {
        return chat;
    }

    @Override
    public List<PhysicsEvent> getPhysicsEventQueue() {
        return physics().physicsEventQueue();
    }

    @Override
    public Map<CreatureId, CreatureBody> getCreatureBodies() {
        return physics().creatureBodies();
    }

    @Override
    public Map<AbilityId, AbilityBody> getAbilityBodies() {
        return physics().abilityBodies();
    }

    @Override
    public boolean isForceUpdateBodyPositions() {
        return physics().isForceUpdateBodyPositions();
    }

    @Override
    public void setForceUpdateBodyPositions(boolean value) {
        physics().isForceUpdateBodyPositions(value);
    }

    @Override
    public AreaId getDefaultAreaId() {
        return gameState.defaultAreaId();
    }
}
