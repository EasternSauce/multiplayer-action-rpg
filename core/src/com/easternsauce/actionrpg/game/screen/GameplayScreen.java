package com.easternsauce.actionrpg.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.physics.util.PhysicsEventQueueProcessor;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.util.GameplayRenderer;
import com.easternsauce.actionrpg.util.Constants;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class GameplayScreen implements Screen {
  private final PhysicsEventQueueProcessor processPhysicsEventQueueProcessor = PhysicsEventQueueProcessor.of();
  private final GameplayRenderer gameplayRenderer = GameplayRenderer.of();

  private CoreGame game;
  private Map<EntityId<Area>, TiledMap> maps;
  private TextureAtlas atlas;

  public void init(TextureAtlas atlas, CoreGame game) {
    this.game = game;
    this.atlas = atlas;

    game.getEntityManager().getGameEntityPhysics().setDebugRenderer(new Box2DDebugRenderer());

    maps = loadMaps();

    game.getGameState().setRandomGenerator(RandomGenerator.of(Instant.now().getNano()));

    game.initState();

    game.getEntityManager().getGameEntityRenderer().init(atlas);
    game.getHudRenderer().init(atlas);

    game.getEntityManager().getGameEntityPhysics().init(maps, game);

    initializeRenderingLayers(game);

    game.getViewportsHandler().initViewports();

  }

  private Map<EntityId<Area>, TiledMap> loadMaps() {
    Map<EntityId<Area>, String> mapsToLoad = new OrderedMap<>();
    mapsToLoad.put(EntityId.of("Area1"), "assets/areas/area1");
    mapsToLoad.put(EntityId.of("Area2"), "assets/areas/area2");
    mapsToLoad.put(EntityId.of("Area3"), "assets/areas/area3");
    mapsToLoad.put(EntityId.of("Area4"), "assets/areas/area4");

    return mapsToLoad.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
      entry -> game.getEntityManager().getGameEntityRenderer().loadMap(entry.getValue() + "/tile_map.tmx"), (o1, o2) -> o1, OrderedMap::new));
  }

  private void initializeRenderingLayers(CoreGame game) {
    game.setWorldElementsRenderingLayer(RenderingLayer.of());
    game.setHudRenderingLayer(RenderingLayer.of());
    game.setWorldTextRenderingLayer(RenderingLayer.of());
  }

  @Override
  public void show() {
    game.setChatInputProcessor();
  }

  @Override
  public void render(float delta) {
    update(delta);

    clearScreen();

    if (game.isGameplayRunning()) {
      if (game.getGameState().getThisClientPlayerId().isEmpty()) {
        game.renderServerRunningMessage();
      } else {
        if (game.getEntityManager().getGameEntityRenderer().getAreaRenderers()
          .containsKey(game.getGameState().getCurrentAreaId())) {
          game.getEntityManager().getGameEntityRenderer().getAreaRenderers().get(game.getGameState().getCurrentAreaId())
            .setView(game.getViewportsHandler().getWorldCamera());
        }

        setProjectionMatrices(game);

        gameplayRenderer.updateRenderer(game);
        gameplayRenderer.renderGameplay(game);

        game.getHudRenderer().render(game);

        game.renderServerRunningMessage();
      }
    }
  }

  public void update(float delta) {
    if (game.isGameplayRunning()) {
      game.performPhysicsWorldStep();

      handleForceUpdateBodyPositions(game);

      game.onUpdate();

      game.getEventProcessor().process(game.getEntityManager(), atlas, game);

      game.getEventProcessor().getTeleportEvents()
        .forEach(teleportInfo -> game.getEntityManager().teleportCreature(teleportInfo, game));
      game.getEventProcessor().getTeleportEvents().clear();

      game.getGameState().updateGeneralTimer(delta);

      game.getEntityManager().updateCreatures(delta, game);
      game.getEntityManager().updateAbilities(delta, game);
      game.getEntityManager().updateEnemyRallyPoints(delta, game);

      processPhysicsEventQueueProcessor.process(game);

      EntityId<Creature> thisClientPlayerId = game.getGameState().getThisClientPlayerId();

      if (!thisClientPlayerId.isEmpty() && !game.getCreature(thisClientPlayerId).isEmpty()) {
        game.updateCameraPositions();
      }

      if (!game.getEntityManager().getGameEntityRenderer().getAreasLoaded() &&
        game.getFirstNonStubBroadcastReceived()) {
        game.getEntityManager().getGameEntityRenderer().loadAreaRenderers(maps, game);
      }
    }
  }

  private static void clearScreen() {
    Gdx.gl.glClearColor(0, 0, 0, 1);

    int coverageBuffer;
    if (Gdx.graphics.getBufferFormat().coverageSampling) {
      coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
    } else {
      coverageBuffer = 0;
    }

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);
  }

  public void setProjectionMatrices(CoreGame game) {
    game.getWorldElementsRenderingLayer().setProjectionMatrix(game.getViewportsHandler().getWorldCamera().combined);

    game.getHudRenderingLayer().setProjectionMatrix(game.getViewportsHandler().getHudCamera().combined);

    game.getWorldTextRenderingLayer().setProjectionMatrix(game.getViewportsHandler().getWorldTextCamera().combined);
  }

  private void handleForceUpdateBodyPositions(CoreGame game) {
    if (game.isForceUpdateBodyPositions()) { // only runs after receiving gameState state update
      game.setForceUpdateBodyPositions(false);

      game.getActiveCreatures().forEach((creatureId, creature) -> {
        if (game.getCreatureBodies().containsKey(creatureId) &&
          game.getCreatureBodies().get(creatureId).getBodyPos().distance(creature.getParams().getPos()) >
            Constants.FORCE_UPDATE_MINIMUM_DISTANCE // only setTransform if positions
          // are far apart
        ) {
          game.getCreatureBodies().get(creatureId).trySetTransform(creature.getParams().getPos());
        }
      });

      game.getAbilities().forEach((abilityId, ability) -> {
        //noinspection SpellCheckingInspection
        if (game.getAbilityBodies().containsKey(abilityId) &&
          game.getAbilityBodies().get(abilityId).getBodyInitialized() &&
          // this is needed to fix body created client/server desync
          !ability.getParams().getSkipCreatingBody() &&
          game.getAbilityBodies().get(abilityId).getBodyPos().distance(ability.getParams().getPos()) >
            Constants.FORCE_UPDATE_MINIMUM_DISTANCE
          // only setTransform if positions are far apart
        ) {
          game.getAbilityBodies().get(abilityId).trySetTransform(ability.getParams().getPos());
        }
      });
    }
  }

  @Override
  public void resize(int width, int height) {
    game.getViewportsHandler().updateViewportsOnResize(width, height);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
  }

}
