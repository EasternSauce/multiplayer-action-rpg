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
import com.mygdx.game.renderer.LootPileRenderer;
import com.mygdx.game.renderer.creature.CreatureRenderer;
import com.mygdx.game.renderer.game.GameRenderer;
import com.mygdx.game.util.RandomHelper;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

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
        Creature creature = gameState().getCreatures().get(creatureId);

        if (creature != null) {
            if (!gameRenderer.getCreatureRenderers().containsKey(creatureId)) {
                CreatureRenderer creatureRenderer = CreatureRenderer.of(creatureId);
                creatureRenderer.init(gameRenderer.getAtlas(), gameState());
                gameRenderer.getCreatureRenderers().put(creatureId, creatureRenderer);
            }
            if (!gamePhysics.getCreatureBodies().containsKey(creatureId)) {
                CreatureBody creatureBody = CreatureBody.of(creatureId);
                creatureBody.init(this, creature.getParams().getAreaId());
                gamePhysics.getCreatureBodies().put(creatureId, creatureBody);
            }
        }
    }

    public void createAbility(AbilityId abilityId) {
        Ability ability = gameState().getAbilities().get(abilityId);

        if (ability != null) {

            if (!gameRenderer.getAbilityRenderers().containsKey(abilityId)) {
                AbilityRenderer abilityRenderer = AbilityRenderer.of(abilityId);
                abilityRenderer.init(gameRenderer.getAtlas(), gameState());
                gameRenderer.getAbilityRenderers().put(abilityId, abilityRenderer);
            }
            if (!gamePhysics.getAbilityBodies().containsKey(abilityId)) {
                AbilityBody abilityBody = AbilityBody.of(abilityId);
                if (ability.getParams().getState() == AbilityState.ACTIVE) {
                    abilityBody.init(this, ability.getParams().getIsSkipCreatingBody());
                }
                gamePhysics.getAbilityBodies().put(abilityId, abilityBody);
            }
        }

    }

    public void activateAbility(AbilityId abilityId) {
        Ability ability = gameState().getAbilities().get(abilityId);

        if (ability != null && physics().getAbilityBodies().containsKey(ability.getParams().getId())) {
            physics().getAbilityBodies()
                     .get(ability.getParams().getId())
                     .init(this, ability.getParams().getIsSkipCreatingBody());
        }

    }

    public void createLootPile(LootPileId lootPileId) {
        LootPile lootPile = getLootPile(lootPileId);

        if (lootPile != null) {
            if (!gameRenderer.getLootPileRenderers().containsKey(lootPileId)) {
                LootPileRenderer lootPileRenderer = LootPileRenderer.of(lootPileId);
                lootPileRenderer.init(gameRenderer.getAtlas(), gameState());
                gameRenderer.getLootPileRenderers().put(lootPileId, lootPileRenderer);
            }
            if (!gamePhysics.getLootPileBodies().containsKey(lootPileId)) {
                LootPileBody lootPileBody = LootPileBody.of(lootPileId);
                lootPileBody.init(this);
                gamePhysics.getLootPileBodies().put(lootPileId, lootPileBody);
            }
        }
    }

    public void spawnEnemy(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn) {
        gameState().getCreatures()
                   .put(creatureId,
                        Enemy.of(CreatureParams.of(creatureId, areaId, enemySpawn)
                                               .setBaseSpeed(7f)
                                               .setAttackDistance(enemySpawn.getEnemyTemplate().getAttackDistance())
                                               .setMainAttackSkill(enemySpawn.getEnemyTemplate().getMainAttackSkill())
                                               .setDropTable(enemySpawn.getEnemyTemplate().getDropTable())));

        getCreatureModelsToBeCreated().add(creatureId);


    }

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

    abstract public void initState();

    public void removeCreature(CreatureId creatureId) {
        if (creatureId != null) {
            gameState().getCreatures().remove(creatureId);

            renderer().getCreatureRenderers().remove(creatureId);

            if (physics().getCreatureBodies().containsKey(creatureId)) {
                physics().getCreatureBodies().get(creatureId).onRemove();
                physics().getCreatureBodies().remove(creatureId);
            }
        }
    }

    public void removeAbility(AbilityId abilityId) {

        if (abilityId != null) {

            gameState().getAbilities().remove(abilityId);

            renderer().getAbilityRenderers().remove(abilityId);

            if (physics().getAbilityBodies().containsKey(abilityId)) {
                physics().getAbilityBodies().get(abilityId).onRemove();
                physics().getAbilityBodies().remove(abilityId);
            }
        }
    }

    public void removeLootPile(LootPileId lootPileId) {
        if (lootPileId != null) {

            gameState().getLootPiles().remove(lootPileId);

            renderer().getLootPileRenderers().remove(lootPileId);

            if (physics().getLootPileBodies().containsKey(lootPileId)) {
                physics().getLootPileBodies().get(lootPileId).onRemove();
                physics().getLootPileBodies().remove(lootPileId);
            }
        }
    }


    abstract public void handleAttackTarget(CreatureId attackingCreatureId,
                                            Vector2 vectorTowardsTarget,
                                            SkillType skillType);

    public void updateCreatures(float delta) {
        Set<CreatureId> creaturesToUpdate = getCreaturesToUpdate();

        creaturesToUpdate.forEach(creatureId -> {
            if (physics().getCreatureBodies().containsKey(creatureId)) {
                physics().getCreatureBodies().get(creatureId).update(gameState());
            }
        });

        // set gamestate position based on b2body position
        creaturesToUpdate.forEach(creatureId -> {
            if (gameState().getCreatures().containsKey(creatureId) &&
                physics().getCreatureBodies().containsKey(creatureId)) {

                gameState().getCreatures()
                           .get(creatureId)
                           .getParams()
                           .setPos(physics().getCreatureBodies().get(creatureId).getBodyPos());

            }
        });

        // if creature is to be updated, then body should be active, otherwise it should be inactive
        gamePhysics.getCreatureBodies()
                   .forEach((key, value) -> gamePhysics.getCreatureBodies()
                                                       .get(key)
                                                       .setActive(creaturesToUpdate.contains(key)));

        creaturesToUpdate.forEach(creatureId -> {
            if (getCreatures().containsKey(creatureId) && renderer().getCreatureRenderers().containsKey(creatureId)) {
                renderer().getCreatureRenderers().get(creatureId).update(this);
            }
        });

        creaturesToUpdate.forEach(creatureId -> {
            if (gameState().getCreatures().containsKey(creatureId)) {
                gameState().getCreatures().get(creatureId).update(delta, this);
            }
        });

    }

    public void updateAbilities(float delta) {
        Set<AbilityId> abilitiesToUpdate = getAbilitiesToUpdate();

        abilitiesToUpdate.forEach(abilityId -> gameState().getAbilities().get(abilityId).update(delta, this));


        abilitiesToUpdate.forEach(abilityId -> {
            if (physics().getAbilityBodies().containsKey(abilityId)) {
                physics().getAbilityBodies().get(abilityId).update(gameState());
            }
        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (physics().getAbilityBodies().containsKey(abilityId)) {
                Ability ability = gameState().getAbilities().get(abilityId);
                if (!ability.isPositionChangedOnUpdate() &&
                    ability.bodyShouldExist() &&
                    physics().getAbilityBodies().get(abilityId).getIsBodyInitialized()) {
                    ability.getParams().setPos(physics().getAbilityBodies().get(abilityId).getBodyPos());
                }

            }

        });

        abilitiesToUpdate.forEach(abilityId -> {
            if (renderer().getAbilityRenderers().containsKey(abilityId)) {
                renderer().getAbilityRenderers().get(abilityId).update(gameState());
            }
        });

    }

    @Override
    public CreatureId getAliveCreatureIdClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded) {

        CreatureId minCreatureId = null;
        float minDistance = Float.MAX_VALUE;
        for (CreatureId creatureId : getCreaturesToUpdate()) {
            Creature creature = gameState().getCreatures().get(creatureId);
            float distance = pos.distance(creature.getParams().getPos());
            if (creature.isAlive() && distance < minDistance && distance < maxRange && !excluded.contains(creatureId)) {
                minDistance = distance;
                minCreatureId = creatureId;
            }
        }
        return minCreatureId;
    }

    @Override
    public Vector2 getCreaturePos(CreatureId creatureId) {
        if (!gameState().getCreatures().containsKey(creatureId)) {
            return null;
        }
        return gameState().getCreatures().get(creatureId).getParams().getPos();
    }

    @Override
    public Creature getCreature(CreatureId creatureId) {
        if (creatureId == null || !gameState().getCreatures().containsKey(creatureId)) {
            return null;
        }
        return gameState().getCreatures().get(creatureId);
    }


    @Override
    public Map<CreatureId, Creature> getCreatures() {
        return gameState().getCreatures();
    }

    @Override
    public Map<CreatureId, Creature> getRemovedCreatures() {
        return gameState().getRemovedCreatures();
    }


    @Override
    public PhysicsWorld getPhysicsWorld(AreaId areaId) {
        return physics().getPhysicsWorlds().get(areaId);
    }

    public AreaId getCurrentPlayerAreaId() {
        if (thisPlayerId != null && gameState.getCreatures().containsKey(thisPlayerId)) {
            return getCreature(thisPlayerId).getParams().getAreaId();
        }
        return gameState.getDefaultAreaId();
    }

    public void teleportCreature(TeleportEvent teleportEvent) {
        if (teleportEvent.getToAreaId().equals(getCreature(teleportEvent.getCreatureId()).getParams().getAreaId())) {
            physics().getCreatureBodies().get(teleportEvent.getCreatureId()).forceSetTransform(teleportEvent.getPos());
        }
        else {
            if (teleportEvent.getCreatureId() != null) {
                Creature creature = getCreature(teleportEvent.getCreatureId());

                creature.getParams().setAreaId(teleportEvent.getToAreaId());

                creature.getParams().setPos(teleportEvent.getPos());
                creature.getParams().setMovementCommandTargetPos(teleportEvent.getPos());

                if (physics().getCreatureBodies().containsKey(teleportEvent.getCreatureId())) {
                    physics().getCreatureBodies().get(teleportEvent.getCreatureId()).onRemove();
                    physics().getCreatureBodies().remove(teleportEvent.getCreatureId());
                }

                if (!gamePhysics.getCreatureBodies().containsKey(teleportEvent.getCreatureId())) {
                    CreatureBody creatureBody = CreatureBody.of(teleportEvent.getCreatureId());
                    creatureBody.init(this, teleportEvent.getToAreaId());
                    gamePhysics.getCreatureBodies().put(teleportEvent.getCreatureId(), creatureBody);
                }

                creature.getParams().setJustTeleportedToGate(true);


            }
        }

    }

    abstract public void performPhysicsWorldStep();

    @Override
    public boolean isLineOfSight(AreaId areaId, Vector2 fromPos, Vector2 toPos) {
        return physics().getPhysicsWorlds().get(areaId).isLineOfSight(fromPos, toPos);
    }

    @Override
    public void updateCameraPositions() {
        renderer().getViewportsHandler().updateCameraPositions(this);
    }

    @Override
    public Map<AbilityId, Ability> getAbilities() {
        return gameState.getAbilities();
    }

    @Override
    public Ability getAbility(AbilityId abilityId) {
        if (abilityId == null || !gameState().getAbilities().containsKey(abilityId)) {
            return null;
        }
        return gameState().getAbilities().get(abilityId);
    }

    @Override
    public Ability getAbility(CreatureId creatureId, SkillType skillType) {
        Optional<Ability> first = gameState.getAbilities()
                                           .values()
                                           .stream()
                                           .filter(ability -> ability.getParams().getCreatureId().equals(creatureId) &&
                                                              ability.getParams().getSkillType() == skillType)
                                           .findFirst();

        return first.orElse(null);
    }

    @Override
    public Vector2 getAbilityPos(AbilityId abilityId) {
        if (abilityId == null || !gameState().getAbilities().containsKey(abilityId)) {
            return null;
        }
        return gameState().getAbilities().get(abilityId).getParams().getPos();
    }

    @Override
    public void renderB2BodyDebug() {
        if (isDebugEnabled()) {
            physics().getDebugRenderer()
                     .render(physics().getPhysicsWorlds().get(getCurrentPlayerAreaId()).getB2world(),
                             renderer().getViewportsHandler().getWorldCameraCombinedProjectionMatrix());
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
        return physics().getPhysicsEventQueue();
    }

    @Override
    public Map<CreatureId, CreatureBody> getCreatureBodies() {
        return physics().getCreatureBodies();
    }

    @Override
    public Map<AbilityId, AbilityBody> getAbilityBodies() {
        return physics().getAbilityBodies();
    }

    @Override
    public boolean isForceUpdateBodyPositions() {
        return physics().getIsForceUpdateBodyPositions();
    }

    @Override
    public void setForceUpdateBodyPositions(boolean value) {
        physics().setIsForceUpdateBodyPositions(value);
    }

    @Override
    public AreaId getDefaultAreaId() {
        return gameState.getDefaultAreaId();
    }

    @Override
    public void initPlayerParams(CreatureId playerId) {
        gameState.getPlayerParams().put(playerId, PlayerParams.of());
    }

    @Override
    public PlayerParams getPlayerParams(CreatureId creatureId) {
        if (creatureId != null) {
            return gameState.getPlayerParams().get(creatureId);
        }
        return null;
    }

    public Vector2 mousePosRelativeToCenter() { // relative to center of screen, in in-game length units
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        gameRenderer.getViewportsHandler().unprojectHudCamera(screenCoords);
        Vector2 mousePos =
                Vector2.of(screenCoords.x - Constants.WindowWidth / 2f, screenCoords.y - Constants.WindowHeight / 2f);

        float viewportRatioX = Constants.ViewpointWorldWidth / Constants.WindowWidth;
        float viewportRatioY = Constants.ViewpointWorldHeight / Constants.WindowHeight;


        return Vector2.of(mousePos.getX() * viewportRatioX / Constants.PPM,
                          mousePos.getY() * viewportRatioY / Constants.PPM);
    }

    public Vector2 hudMousePos() {
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        gameRenderer.getViewportsHandler().unprojectHudCamera(screenCoords);
        return Vector2.of(screenCoords.x, screenCoords.y);
    }

    @Override
    public Set<AreaGate> getAreaGates() {
        return gameState.getAreaGates();
    }

    @Override
    public LootPile getLootPile(LootPileId lootPileId) {
        return gameState.getLootPiles().get(lootPileId);
    }

    @Override
    public Map<LootPileId, LootPile> getLootPiles() {
        return gameState.getLootPiles();
    }

    @Override
    public Float getTime() {
        return gameState.getGeneralTimer().getTime();
    }

    public void goToGamePlayScreen() {
        setScreen(gameplayScreen);
    }

    public abstract void initializePlayer(String playerName);

    @Override
    public Float nextRandomValue() {
        float result = RandomHelper.seededRandomFloat(gameState.getLastRandomValue());

        gameState.setLastRandomValue(result);

        return result;
    }

    @Override
    public void forEachAliveCreature(Consumer<Creature> creatureAction) {
        getCreatures().values().stream().filter(Creature::isAlive).forEach(creatureAction);
    }

    @Override
    public void forEachDeadCreature(Consumer<Creature> creatureAction) {
        getCreatures().values().stream().filter(creature -> !creature.isAlive()).forEach(creatureAction);
    }
}
