package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.Constants;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.CreatureUpdatable;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.game.screen.ConnectScreen;
import com.mygdx.game.game.screen.GameplayScreen;
import com.mygdx.game.game.screen.MenuScreen;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.*;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.body.LootPileBody;
import com.mygdx.game.physics.event.PhysicsEvent;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.renderer.AbilityRenderer;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.renderer.LootPileRenderer;
import com.mygdx.game.renderer.creature.CreatureRenderer;
import com.mygdx.game.util.RandomHelper;

import java.io.IOException;
import java.util.*;

public abstract class MyGdxGame extends Game implements AbilityUpdatable, CreatureUpdatable, GameRenderable, GameActionApplicable {
    final protected GameRenderer gameRenderer = GameRenderer.of();
    final protected GamePhysics gamePhysics = GamePhysics.of();
    final protected GameplayScreen gameplayScreen = GameplayScreen.of();

    final protected ConnectScreen connectScreen = ConnectScreen.of();

    final MenuScreen menuScreen = MenuScreen.of();
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebugEnabled = true;
    private final Chat chat = Chat.of();
    final List<CreatureId> creatureModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilityModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilitiesToBeActivated = Collections.synchronizedList(new ArrayList<>());
    final List<CreatureId> creatureModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    final List<AbilityId> abilityModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());

    final List<LootPileId> lootPileModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());

    final List<LootPileId> lootPileModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    final List<TeleportEvent> teleportEvents = Collections.synchronizedList(new ArrayList<>());
    protected GameState gameState = GameState.of();
    protected CreatureId thisPlayerId = null;

    public Boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public CreatureId getCurrentPlayerId() {
        return thisPlayerId;
    }

    @Override
    public List<CreatureId> getCreatureModelsToBeCreated() {
        return creatureModelsToBeCreated;
    }

    @Override
    public List<AbilityId> getAbilityModelsToBeCreated() {
        return abilityModelsToBeCreated;
    }

    @Override
    public List<AbilityId> getAbilitiesToBeActivated() {
        return abilitiesToBeActivated;
    }

    @Override
    public List<CreatureId> getCreatureModelsToBeRemoved() {
        return creatureModelsToBeRemoved;
    }

    @Override
    public List<AbilityId> getAbilityModelsToBeRemoved() {
        return abilityModelsToBeRemoved;
    }

    @Override
    public List<LootPileId> getLootPileModelsToBeCreated() {
        return lootPileModelsToBeCreated;
    }

    @Override
    public List<LootPileId> getLootPileModelsToBeRemoved() {
        return lootPileModelsToBeRemoved;
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
        try {
            establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gameplayScreen.init(this);
        connectScreen.init(this);

        setStartingScreen();
    }

    public abstract void setStartingScreen();

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
                creatureBody.init(this, creature.params().areaId());
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
                    abilityBody.init(this, ability.params().isSkipCreatingBody());
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
                     .init(this, ability.params().isSkipCreatingBody());
        }

    }

    public void createLootPile(LootPileId lootPileId) {
        LootPile lootPile = getLootPile(lootPileId);

        if (lootPile != null) {
            if (!gameRenderer.lootPileRenderers().containsKey(lootPileId)) {
                LootPileRenderer lootPileRenderer = LootPileRenderer.of(lootPileId);
                lootPileRenderer.init(gameRenderer.atlas(), gameState());
                gameRenderer.lootPileRenderers().put(lootPileId, lootPileRenderer);
            }
            if (!gamePhysics.lootPileBodies().containsKey(lootPileId)) {
                LootPileBody lootPileBody = LootPileBody.of(lootPileId);
                lootPileBody.init(this);
                gamePhysics.lootPileBodies().put(lootPileId, lootPileBody);
            }
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
                                                                          .mainAttackSkill())
                                               .dropTable(enemySpawn.enemyTemplate().dropTable())));

        getCreatureModelsToBeCreated().add(creatureId);


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

    public void removeLootPile(LootPileId lootPileId) {
        if (lootPileId != null) {

            gameState().lootPiles().remove(lootPileId);

            renderer().lootPileRenderers().remove(lootPileId);

            if (physics().lootPileBodies().containsKey(lootPileId)) {
                physics().lootPileBodies().get(lootPileId).onRemove();
                physics().lootPileBodies().remove(lootPileId);
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
            if (getCreatures().containsKey(creatureId) && renderer().creatureRenderers().containsKey(creatureId)) {
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

        abilitiesToUpdate.forEach(abilityId -> gameState().abilities().get(abilityId).update(delta, this));


        abilitiesToUpdate.forEach(abilityId -> {
            if (physics().abilityBodies().containsKey(abilityId)) {
                physics().abilityBodies().get(abilityId).update(gameState());
            }
        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (physics().abilityBodies().containsKey(abilityId)) {
                Ability ability = gameState().abilities().get(abilityId);
                if (!ability.isPositionChangedOnUpdate() &&
                    ability.bodyShouldExist() &&
                    physics().abilityBodies().get(abilityId).isBodyInitialized()) {
                    ability.params().pos(physics().abilityBodies().get(abilityId).getBodyPos());
                }

            }

        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (renderer().abilityRenderers().containsKey(abilityId)) {
                renderer().abilityRenderers().get(abilityId).update(gameState());
            }
        });

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
    public Map<CreatureId, Creature> getRemovedCreatures() {
        return gameState().removedCreatures();
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
                    creatureBody.init(this, teleportEvent.toAreaId());
                    gamePhysics.creatureBodies().put(teleportEvent.creatureId(), creatureBody);
                }

                creature.params().justTeleportedToGate(true);


            }
        }

    }

    abstract public void performPhysicsWorldStep();

    @Override
    public boolean isLineOfSight(AreaId areaId, Vector2 fromPos, Vector2 toPos) {
        return physics().physicsWorlds().get(areaId).isLineOfSight(fromPos, toPos);
    }

    @Override
    public void setWorldCameraPosition(float x, float y) {
        renderer().worldCamera().position.x = x;
        renderer().worldCamera().position.y = y;
    }

    @Override
    public void setWorldTextCameraPosition(float x, float y) {
        renderer().worldTextCamera().position.x = x;
        renderer().worldTextCamera().position.y = y;
    }

    @Override
    public void updateCameras() {
        renderer().worldCamera().update();
        renderer().worldTextCamera().update();
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

    @Override
    public void initPlayerParams(CreatureId playerId) {
        gameState.playerParams().put(playerId, PlayerParams.of());
    }

    @Override
    public PlayerParams getPlayerParams(CreatureId creatureId) {
        if (creatureId != null) {
            return gameState.playerParams().get(creatureId);
        }
        return null;
    }

    public Vector2 mousePosRelativeToCenter() { // relative to center of screen, in in-game length units
        Vector3 v = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        gameRenderer.hudCamera().unproject(v);
        Vector2 mousePos = Vector2.of(v.x - Constants.WindowWidth / 2f, v.y - Constants.WindowHeight / 2f);

        float viewportRatioX = Constants.ViewpointWorldWidth / Constants.WindowWidth;
        float viewportRatioY = Constants.ViewpointWorldHeight / Constants.WindowHeight;


        return Vector2.of(mousePos.x() * viewportRatioX / Constants.PPM,
                          mousePos.y() * viewportRatioY / Constants.PPM);
    }

    public Vector2 hudMousePos() {
        Vector3 v = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        gameRenderer.hudCamera().unproject(v);
        return Vector2.of(v.x, v.y);
    }

    @Override
    public Set<AreaGate> getAreaGates() {
        return gameState.areaGates();
    }

    @Override
    public LootPile getLootPile(LootPileId lootPileId) {
        return gameState.lootPiles().get(lootPileId);
    }

    @Override
    public Map<LootPileId, LootPile> getLootPiles() {
        return gameState.lootPiles();
    }

    @Override
    public Float getTime() {
        return gameState.generalTimer().time();
    }

    public void goToGamePlayScreen() {
        setScreen(gameplayScreen);
    }

    public abstract void initializePlayer(String playerName);

    @Override
    public Float nextRandomValue() {
        float result = RandomHelper.seededRandomFloat(gameState.lastRandomValue());

        gameState.lastRandomValue(result);

        return result;
    }
}
