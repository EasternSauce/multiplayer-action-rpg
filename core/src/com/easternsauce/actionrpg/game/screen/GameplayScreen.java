package com.easternsauce.actionrpg.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.easternsauce.actionrpg.Constants;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.physics.util.PhysicsHelper;
import com.easternsauce.actionrpg.renderer.util.GameplayRendererHelper;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class GameplayScreen implements Screen {
    private CoreGame game;
    @SuppressWarnings("FieldCanBeLocal")
    private Map<AreaId, TiledMap> maps;

    private TextureAtlas atlas;

    public void init(TextureAtlas atlas, CoreGame game) {
        this.game = game;
        this.atlas = atlas;

        game.getEntityManager().getGameEntityPhysics().setDebugRenderer(new Box2DDebugRenderer());

        Map<AreaId, String> mapsToLoad = new ConcurrentSkipListMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");

        maps = mapsToLoad
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                                      entry -> game
                                          .getEntityManager()
                                          .getGameEntityRenderer()
                                          .loadMap(entry.getValue() + "/tile_map.tmx")));

        game.initState();

        game.getEntityManager().getGameEntityRenderer().init(atlas);
        game.getHudRenderer().init(atlas);

        game.getEntityManager().getGameEntityPhysics().init(maps, game);

        game
            .getEntityManager()
            .getGameEntityRenderer()
            .getViewportsHandler()
            .setHudCameraPosition(Constants.WINDOW_WIDTH / 2f, Constants.WINDOW_HEIGHT / 2f); // TODO: move it inward?

    }

    @Override
    public void show() {
        game.setChatInputProcessor();

        game.getEntityManager().getGameEntityRenderer().resetRendererState(maps, atlas, game);
    }

    public void update(float delta) {
        game.performPhysicsWorldStep();

        PhysicsHelper.handleForceUpdateBodyPositions(game);

        game.onUpdate();
        game.getGameState().handleExpiredAbilities(game);

        game.getEventProcessor().process(game.getEntityManager(), atlas, game);

        game
            .getEventProcessor()
            .getTeleportEvents()
            .forEach(teleportInfo -> game.getEntityManager().teleportCreature(teleportInfo, game));
        game.getEventProcessor().getTeleportEvents().clear();

        game.getGameState().updateGeneralTimer(delta);

        game.getEntityManager().updateCreatures(delta, game);
        game.getEntityManager().updateAbilities(delta, game);

        PhysicsHelper.processPhysicsEventQueue(game);

        game
            .getEntityManager()
            .getGameEntityRenderer()
            .getAreaRenderers()
            .get(game.getGameState().getCurrentAreaId())
            .setView(game.getEntityManager().getGameEntityRenderer().getViewportsHandler().getWorldCamera());

        if (game.getGameState().getThisClientPlayerId() != null &&
            game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId()) != null) {
            game.updateCameraPositions();
        }

    }

    @Override
    public void render(float delta) {
        if (game.isInitialized()) {
            update(delta);
            if (game.isGameplayRenderingAllowed()) {
                game.getEntityManager().getGameEntityRenderer().setProjectionMatrices();

                Gdx.gl.glClearColor(0, 0, 0, 1);

                int coverageBuffer;
                if (Gdx.graphics.getBufferFormat().coverageSampling) {
                    coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
                }
                else {
                    coverageBuffer = 0;
                }

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

                GameplayRendererHelper.renderGameplay(game);

                game.getHudRenderer().render(game);
            }
            else {
                game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer().begin();
                game.renderServerRunningMessage(game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer());
                game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer().end();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        game.getEntityManager().getGameEntityRenderer().getViewportsHandler().updateViewportsOnResize(width, height);
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
