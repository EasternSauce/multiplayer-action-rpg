package com.mygdx.game.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.Constants;
import com.mygdx.game.chat.Chat;
import com.mygdx.game.game.entity.EntityEventProcessor;
import com.mygdx.game.game.entity.GameEntityManager;
import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.screen.ConnectScreen;
import com.mygdx.game.game.screen.ConnectScreenMessageHolder;
import com.mygdx.game.game.screen.GameplayScreen;
import com.mygdx.game.game.screen.MenuScreen;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.event.PhysicsEvent;
import com.mygdx.game.physics.world.PhysicsWorld;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.hud.HudRenderer;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CoreGame extends Game {

    @Getter
    final private GameEntityManager entityManager = GameEntityManager.of();

    final protected GameplayScreen gameplayScreen = GameplayScreen.of();

    final protected ConnectScreen connectScreen = ConnectScreen.of();

    @Getter
    final private EntityEventProcessor eventProcessor = EntityEventProcessor.of();

    @SuppressWarnings("unused")
    final MenuScreen menuScreen = MenuScreen.of();
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebugEnabled = false;

    @Getter
    private final Chat chat = Chat.of();

    @Getter
    private final HudRenderer hudRenderer = HudRenderer.of();

    public Boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    public void addTeleportEvent(TeleportEvent teleportEvent) {
        eventProcessor.getTeleportEvents().add(teleportEvent);
    }

    public abstract EndPoint getEndPoint();

    public boolean isInitialized() { // this is pointless right now TODO
        return true;
    }

    public boolean isGameplayRenderingAllowed() {
        return true;
    }

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

    public abstract void setStartingScreen();

    abstract public void onUpdate();

    abstract public void establishConnection() throws IOException;

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
            entityManager
                .getGameEntityPhysics()
                .getDebugRenderer()
                .render(entityManager
                            .getGameEntityPhysics()
                            .getPhysicsWorlds()
                            .get(getGameState().getCurrentAreaId())
                            .getB2world(),
                        entityManager.getGameEntityRenderer().getViewportsHandler().getWorldCameraCombinedProjectionMatrix());
        }
    }

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
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        entityManager.getGameEntityRenderer().getViewportsHandler().unprojectHudCamera(screenCoords);
        Vector2 mousePos = Vector2.of(screenCoords.x - Constants.WINDOW_WIDTH / 2f,
                                      screenCoords.y - Constants.WINDOW_HEIGHT / 2f);

        float viewportRatioX = Constants.VIEWPOINT_WORLD_WIDTH / Constants.WINDOW_WIDTH;
        float viewportRatioY = Constants.VIEWPOINT_WORLD_HEIGHT / Constants.WINDOW_HEIGHT;

        return Vector2.of(mousePos.getX() * viewportRatioX / Constants.PPM, mousePos.getY() * viewportRatioY / Constants.PPM);
    }

    public Vector2 hudMousePos() {
        Vector3 screenCoords = new Vector3((float) Gdx.input.getX(), (float) Gdx.input.getY(), 0f);
        entityManager.getGameEntityRenderer().getViewportsHandler().unprojectHudCamera(screenCoords);
        return Vector2.of(screenCoords.x, screenCoords.y);
    }

    public void goToGamePlayScreen() {
        setScreen(gameplayScreen);
    }

    public abstract void initializePlayer(String playerName);

    public abstract GameState getGameState();

    public abstract void setConnectScreenInputProcessor(ConnectScreenMessageHolder messageHolder);

    public abstract void setChatInputProcessor();

    public abstract void renderServerRunningMessage(RenderingLayer renderingLayer);

    public abstract boolean isPathfindingCalculatedForCreature(Creature creature);
}