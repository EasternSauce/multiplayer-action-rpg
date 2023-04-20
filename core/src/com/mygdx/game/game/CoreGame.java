package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.Constants;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.game.entity.EntityEventProcessor;
import com.mygdx.game.game.entity.GameEntityManager;
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
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.event.PhysicsEvent;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.util.RandomHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public abstract class CoreGame extends Game implements AbilityUpdatable, CreatureUpdatable, GameRenderable, GameActionApplicable {
    @Getter
    final private GameEntityManager entityManager = GameEntityManager.of();

    final protected GameplayScreen gameplayScreen = GameplayScreen.of();

    final protected ConnectScreen connectScreen = ConnectScreen.of();

    @Getter
    final private EntityEventProcessor eventProcessor = EntityEventProcessor.of();

    final MenuScreen menuScreen = MenuScreen.of();
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebugEnabled = true;

    @Getter
    private final Chat chat = Chat.of();

    @Getter
    @Setter
    private GameState gameState = GameState.of();

    protected CreatureId thisPlayerId = null;

    public Boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    @Override
    public CreatureId getCurrentPlayerId() {
        return thisPlayerId;
    }

    @Override
    public void addTeleportEvent(TeleportEvent teleportEvent) {
        eventProcessor.getTeleportEvents().add(teleportEvent);
    }

    public abstract EndPoint getEndPoint();

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

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

    abstract public void initState();


    abstract public void handleAttackTarget(CreatureId attackingCreatureId,
                                            Vector2 vectorTowardsTarget,
                                            SkillType skillType);

    @Override
    public Vector2 getCreaturePos(CreatureId creatureId) {
        if (!getGameState().getCreatures().containsKey(creatureId)) {
            return null;
        }
        return getGameState().getCreatures().get(creatureId).getParams().getPos();
    }

    @Override
    public Creature getCreature(CreatureId creatureId) {
        if (creatureId == null || !getGameState().getCreatures().containsKey(creatureId)) {
            return null;
        }
        return getGameState().getCreatures().get(creatureId);
    }


    @Override
    public Map<CreatureId, Creature> getCreatures() {
        return getGameState().getCreatures();
    }

    @Override
    public Map<CreatureId, Creature> getRemovedCreatures() {
        return getGameState().getRemovedCreatures();
    }


    @Override
    public PhysicsWorld getPhysicsWorld(AreaId areaId) {
        return entityManager.getGamePhysics().getPhysicsWorlds().get(areaId);
    }

    public AreaId getCurrentPlayerAreaId() {
        if (thisPlayerId != null && gameState.getCreatures().containsKey(thisPlayerId)) {
            return getCreature(thisPlayerId).getParams().getAreaId();
        }
        return gameState.getDefaultAreaId();
    }

    // TODO: move it
    public void teleportCreature(TeleportEvent teleportEvent) {
        if (teleportEvent.getToAreaId().equals(getCreature(teleportEvent.getCreatureId()).getParams().getAreaId())) {
            entityManager.getGamePhysics()
                         .getCreatureBodies()
                         .get(teleportEvent.getCreatureId())
                         .forceSetTransform(teleportEvent.getPos());
        }
        else {
            if (teleportEvent.getCreatureId() != null) {
                Creature creature = getCreature(teleportEvent.getCreatureId());

                creature.getParams().setAreaId(teleportEvent.getToAreaId());

                creature.getParams().setPos(teleportEvent.getPos());
                creature.getParams().setMovementCommandTargetPos(teleportEvent.getPos());

                if (entityManager.getGamePhysics().getCreatureBodies().containsKey(teleportEvent.getCreatureId())) {
                    entityManager.getGamePhysics().getCreatureBodies().get(teleportEvent.getCreatureId()).onRemove();
                    entityManager.getGamePhysics().getCreatureBodies().remove(teleportEvent.getCreatureId());
                }

                if (!entityManager.getGamePhysics().getCreatureBodies().containsKey(teleportEvent.getCreatureId())) {
                    CreatureBody creatureBody = CreatureBody.of(teleportEvent.getCreatureId());
                    creatureBody.init(this, teleportEvent.getToAreaId());
                    entityManager.getGamePhysics().getCreatureBodies().put(teleportEvent.getCreatureId(), creatureBody);
                }

                creature.getParams().setJustTeleportedToGate(true);


            }
        }

    }

    abstract public void performPhysicsWorldStep();

    @Override
    public boolean isLineOfSight(AreaId areaId, Vector2 fromPos, Vector2 toPos) {
        return entityManager.getGamePhysics().getPhysicsWorlds().get(areaId).isLineOfSight(fromPos, toPos);
    }

    @Override
    public void updateCameraPositions() {
        entityManager.getGameRenderer().getViewportsHandler().updateCameraPositions(this);
    }

    @Override
    public Map<AbilityId, Ability> getAbilities() {
        return gameState.getAbilities();
    }

    @Override
    public Ability getAbility(AbilityId abilityId) {
        if (abilityId == null || !getGameState().getAbilities().containsKey(abilityId)) {
            return null;
        }
        return getGameState().getAbilities().get(abilityId);
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
        if (abilityId == null || !getGameState().getAbilities().containsKey(abilityId)) {
            return null;
        }
        return getGameState().getAbilities().get(abilityId).getParams().getPos();
    }

    @Override
    public void renderB2BodyDebug() {
        if (isDebugEnabled()) {
            entityManager.getGamePhysics()
                         .getDebugRenderer()
                         .render(entityManager.getGamePhysics()
                                              .getPhysicsWorlds()
                                              .get(getCurrentPlayerAreaId())
                                              .getB2world(),
                                 entityManager.getGameRenderer()
                                              .getViewportsHandler()
                                              .getWorldCameraCombinedProjectionMatrix());
        }
    }

    @Override
    public List<PhysicsEvent> getPhysicsEventQueue() {
        return entityManager.getGamePhysics().getPhysicsEventQueue();
    }

    @Override
    public Map<CreatureId, CreatureBody> getCreatureBodies() {
        return entityManager.getGamePhysics().getCreatureBodies();
    }

    @Override
    public Map<AbilityId, AbilityBody> getAbilityBodies() {
        return entityManager.getGamePhysics().getAbilityBodies();
    }

    @Override
    public boolean isForceUpdateBodyPositions() {
        return entityManager.getGamePhysics().getIsForceUpdateBodyPositions();
    }

    @Override
    public void setForceUpdateBodyPositions(boolean value) {
        entityManager.getGamePhysics().setIsForceUpdateBodyPositions(value);
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
        entityManager.getGameRenderer().getViewportsHandler().unprojectHudCamera(screenCoords);
        Vector2 mousePos =
                Vector2.of(screenCoords.x - Constants.WindowWidth / 2f, screenCoords.y - Constants.WindowHeight / 2f);

        float viewportRatioX = Constants.ViewpointWorldWidth / Constants.WindowWidth;
        float viewportRatioY = Constants.ViewpointWorldHeight / Constants.WindowHeight;


        return Vector2.of(mousePos.getX() * viewportRatioX / Constants.PPM,
                          mousePos.getY() * viewportRatioY / Constants.PPM);
    }

    public Vector2 hudMousePos() {
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        entityManager.getGameRenderer().getViewportsHandler().unprojectHudCamera(screenCoords);
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
