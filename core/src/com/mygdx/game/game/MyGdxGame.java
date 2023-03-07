package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.command.*;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.*;
import com.mygdx.game.model.area.Area;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.skill.ScheduledAbility;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.GameStateBroadcast;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.WorldDirection;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.renderer.AbilityRenderer;
import com.mygdx.game.renderer.CreatureRenderer;
import com.mygdx.game.renderer.GameRenderer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public abstract class MyGdxGame extends Game implements AbilityUpdateable, CreatureUpdatable, AbilityRetrievable {
    final protected GameRenderer gameRenderer = GameRenderer.of();
    final protected GamePhysics gamePhysics = GamePhysics.of();
    protected GameState gameState = GameState.of();
    final MyGdxGamePlayScreen playScreen = MyGdxGamePlayScreen.of();

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean debug = true;
    public final Chat chat = Chat.of();
    protected CreatureId thisPlayerId = null;

    final List<CreatureId> creaturesToBeCreated = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeCreated = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeActivated = Collections.synchronizedList(new ArrayList<>());

    final List<CreatureId> creaturesToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeRemoved = Collections.synchronizedList(new ArrayList<>());

    final Map<CreatureId, Vector2> creaturesToTeleport = new ConcurrentSkipListMap<>();


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

    public List<AbilityId> abilitiesToBeActivated() {
        return abilitiesToBeActivated;
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

    abstract public Set<CreatureId> creaturesToUpdate();

    abstract public Set<AbilityId> abilitiesToUpdate();

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
        Set<CreatureId> creaturesToUpdate = creaturesToUpdate();

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
        Set<AbilityId> abilitiesToUpdate = abilitiesToUpdate();

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
    public Ability getAbility(AbilityId abilityId) {
        if (abilityId == null || !gameState().abilities().containsKey(abilityId)) {
            return null;
        }
        return gameState().abilities().get(abilityId);
    }

    @Override
    public Collection<Creature> getCreatures() {
        return gameState().creatures().values();
    }

    @Override
    public AreaId getCurrentAreaId() {
        return gameState().currentAreaId();
    }

    @Override
    public PhysicsWorld getPhysicsWorld(AreaId areaId) {
        return physics().physicsWorlds().get(areaId);
    }

    @Override
    public PhysicsWorld getWorld(AreaId areaId) {
        return physics().physicsWorlds().get(areaId);
    }

    public void registerEndPointClasses() {
        endPoint().getKryo().setRegistrationRequired(true);

        endPoint().getKryo().register(ArrayList.class);
        endPoint().getKryo().register(LinkedList.class);
        endPoint().getKryo().register(ConcurrentSkipListSet.class);
        endPoint().getKryo().register(ConcurrentSkipListMap.class);

        endPoint().getKryo().register(CreatureId.class);
        endPoint().getKryo().register(Vector2.class);
        endPoint().getKryo().register(AreaId.class);
        endPoint().getKryo().register(SimpleTimer.class);
        endPoint().getKryo().register(AbilityType.class);
        endPoint().getKryo().register(AbilityState.class);
        endPoint().getKryo().register(EnemyType.class);
        endPoint().getKryo().register(SkillType.class);
        endPoint().getKryo().register(EnemySpawn.class);
        endPoint().getKryo().register(AbilityId.class);
        endPoint().getKryo().register(EnemyAiState.class);

        endPoint().getKryo().register(InitPlayerCommand.class);
        endPoint().getKryo().register(PlayerMovementCommand.class);
        endPoint().getKryo().register(SendChatMessageCommand.class);
        endPoint().getKryo().register(SpawnEnemyCommand.class);
        endPoint().getKryo().register(TryPerformSkillCommand.class);

        endPoint().getKryo().register(Ability.class);
        endPoint().getKryo().register(AbilityRect.class);
        endPoint().getKryo().register(SummonGhosts.class);
        endPoint().getKryo().register(Slash.class);
        endPoint().getKryo().register(CrossbowBolt.class);
        endPoint().getKryo().register(Fireball.class);
        endPoint().getKryo().register(FireballExplosion.class);
        endPoint().getKryo().register(LightningChain.class);
        endPoint().getKryo().register(LightningNode.class);
        endPoint().getKryo().register(LightningSpark.class);
        endPoint().getKryo().register(MagicOrb.class);
        endPoint().getKryo().register(VolatileBubble.class);
        endPoint().getKryo().register(IceSpear.class);
        endPoint().getKryo().register(PlayfulGhost.class);
        endPoint().getKryo().register(RicochetBullet.class);
        endPoint().getKryo().register(RicochetBallista.class);
        endPoint().getKryo().register(Boomerang.class);
        endPoint().getKryo().register(SummonShield.class);


        endPoint().getKryo().register(Enemy.class);
        endPoint().getKryo().register(Area.class);
        endPoint().getKryo().register(Player.class);
        endPoint().getKryo().register(ScheduledAbility.class);
        endPoint().getKryo().register(Skill.class);
        endPoint().getKryo().register(WorldDirection.class);
        endPoint().getKryo().register(CreatureParams.class);
        endPoint().getKryo().register(AbilityParams.class);


        endPoint().getKryo().register(AddAbilityAction.class);
        endPoint().getKryo().register(AddPlayerAction.class);
        endPoint().getKryo().register(CreatureDeathAction.class);
        endPoint().getKryo().register(MovePlayerTowardsTargetAction.class);
        endPoint().getKryo().register(RemoveAbilityAction.class);
        endPoint().getKryo().register(RemovePlayerAction.class);
        endPoint().getKryo().register(RespawnCreatureAction.class);
        endPoint().getKryo().register(TryPerformSkillAction.class);
        endPoint().getKryo().register(AbilityActivateAction.class);
        endPoint().getKryo().register(SetCreatureMovingVectorAction.class);

        endPoint().getKryo().register(ActionsHolder.class);
        endPoint().getKryo().register(GameState.class);
        endPoint().getKryo().register(GameStateBroadcast.class);

    }


}
