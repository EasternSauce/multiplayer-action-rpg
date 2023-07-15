package com.easternsauce.actionrpg.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.easternsauce.actionrpg.game.chat.Chat;
import com.easternsauce.actionrpg.game.entity.EntityEventProcessor;
import com.easternsauce.actionrpg.game.entity.GameEntityManager;
import com.easternsauce.actionrpg.game.gamestate.GameState;
import com.easternsauce.actionrpg.game.screen.ConnectScreen;
import com.easternsauce.actionrpg.game.screen.GameplayScreen;
import com.easternsauce.actionrpg.game.screen.MenuScreen;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.body.AbilityBody;
import com.easternsauce.actionrpg.physics.body.CreatureBody;
import com.easternsauce.actionrpg.physics.event.PhysicsEvent;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.hud.HudRenderer;
import com.easternsauce.actionrpg.util.Constants;
import com.esotericsoftware.kryonet.EndPoint;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CoreGame extends Game {

    final protected GameplayScreen gameplayScreen = GameplayScreen.of();
    final protected ConnectScreen connectScreen = ConnectScreen.of();
    @Getter
    final private GameEntityManager entityManager = GameEntityManager.of();
    @Getter
    final private EntityEventProcessor eventProcessor = EntityEventProcessor.of();

    @SuppressWarnings("unused")
    final MenuScreen menuScreen = MenuScreen.of();
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebugEnabled = Constants.IS_DEBUG_ENABLED;

    @Getter
    private final Chat chat = Chat.of();

    @Getter
    private final HudRenderer hudRenderer = HudRenderer.of();

    public void addTeleportEvent(TeleportEvent teleportEvent) {
        eventProcessor.getTeleportEvents().add(teleportEvent);
    }

    @SuppressWarnings("unused")
    public abstract EndPoint getEndPoint();

    public abstract boolean isGameplayRunning();

    @Override
    public void create() {
        try {
            establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TextureAtlas atlas = new TextureAtlas("assets/atlas/packed_atlas.atlas");

        gameplayScreen.init(atlas, this);
        connectScreen.init(atlas, this);

        setStartingScreen();
    }

    abstract public void establishConnection() throws IOException;

    public abstract void setStartingScreen();

    abstract public void onUpdate();

    abstract public void initState();

    public abstract Set<AbilityId> getAbilitiesToUpdate();

    public PhysicsWorld getPhysicsWorld(AreaId areaId) {
        return entityManager.getGameEntityPhysics().getPhysicsWorlds().get(areaId);
    }

    abstract public void performPhysicsWorldStep();

    public boolean isLineBetweenPointsUnobstructedByTerrain(AreaId areaId, Vector2 fromPos, Vector2 toPos) {
        return entityManager
            .getGameEntityPhysics()
            .getPhysicsWorlds()
            .get(areaId)
            .isLineBetweenPointsUnobstructedByTerrain(fromPos, toPos);
    }

    public void updateCameraPositions() {
        entityManager.getGameEntityRenderer().getViewportsHandler().updateCameraPositions(this);
    }

    public void renderB2BodyDebug() {
        if (isDebugEnabled()) {
            entityManager.getGameEntityPhysics().getDebugRenderer().render(entityManager
                    .getGameEntityPhysics()
                    .getPhysicsWorlds()
                    .get(getGameState().getCurrentAreaId())
                    .getB2world(),
                entityManager.getGameEntityRenderer().getViewportsHandler().getWorldCameraCombinedProjectionMatrix()
            );
        }
    }

    public Boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    public abstract GameState getGameState();

    public List<PhysicsEvent> getPhysicsEventQueue() {
        return entityManager.getGameEntityPhysics().getPhysicsEventQueue();
    }

    public Map<CreatureId, CreatureBody> getCreatureBodies() {
        return entityManager.getGameEntityPhysics().getCreatureBodies();
    }

    public Map<AbilityId, AbilityBody> getAbilityBodies() {
        return entityManager.getGameEntityPhysics().getAbilityBodies();
    }

    public boolean isForceUpdateBodyPositions() {
        return entityManager.getGameEntityPhysics().getIsForceUpdateBodyPositions();
    }

    public void setForceUpdateBodyPositions(boolean value) {
        entityManager.getGameEntityPhysics().setIsForceUpdateBodyPositions(value);
    }

    public Vector2 mousePosRelativeToCenter() { // relative to center of screen, in in-game length units
        //noinspection SpellCheckingInspection
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        entityManager.getGameEntityRenderer().getViewportsHandler().unprojectHudCamera(screenCoords);
        Vector2 mousePos = Vector2.of(screenCoords.x - Constants.WINDOW_WIDTH / 2f,
            screenCoords.y - Constants.WINDOW_HEIGHT / 2f
        );

        float viewportRatioX = Constants.VIEWPOINT_WORLD_WIDTH / Constants.WINDOW_WIDTH;
        float viewportRatioY = Constants.VIEWPOINT_WORLD_HEIGHT / Constants.WINDOW_HEIGHT;

        return Vector2.of(mousePos.getX() * viewportRatioX / Constants.PPM,
            mousePos.getY() * viewportRatioY / Constants.PPM
        );
    }

    public Vector2 hudMousePos() {
        //noinspection SpellCheckingInspection
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        entityManager.getGameEntityRenderer().getViewportsHandler().unprojectHudCamera(screenCoords);
        return Vector2.of(screenCoords.x, screenCoords.y);
    }

    public void goToGamePlayScreen() {
        setScreen(gameplayScreen);
    }

    public abstract void initializePlayer(String playerName);

    public abstract void setChatInputProcessor();

    public abstract void renderServerRunningMessage(RenderingLayer renderingLayer);

    public abstract boolean isPathfindingCalculatedForCreature(Creature creature);

    @SuppressWarnings("SameReturnValue")
    public abstract Boolean getIsFirstBroadcastReceived();

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public abstract CoreGame setIsFirstBroadcastReceived(@SuppressWarnings("unused") Boolean isFirstBroadcastReceived);

    public void chainAnotherAbility(Ability chainFromAbility,
                                    AbilityType abilityType,
                                    Vector2 dirVector,
                                    ChainAbilityParams chainAbilityParams) {
        getGameState().accessAbilities().chainAnotherAbility(chainFromAbility,
            abilityType,
            dirVector,
            chainAbilityParams,
            this
        );
    }

    public Map<AbilityId, Ability> getAbilities() {
        return getGameState().accessAbilities().getAbilities();
    }

    public Ability getAbility(AbilityId abilityId) {
        return getGameState().accessAbilities().getAbility(abilityId);
    }

    public Map<CreatureId, Creature> getCreatures() {
        return getGameState().accessCreatures().getCreatures();
    }

    public Creature getCreature(CreatureId creatureId) {
        return getGameState().accessCreatures().getCreature(creatureId);
    }

    public Vector2 getCreaturePos(CreatureId creatureId) {
        return getGameState().accessCreatures().getCreaturePos(creatureId);
    }
}
