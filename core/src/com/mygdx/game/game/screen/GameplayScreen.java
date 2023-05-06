package com.mygdx.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mygdx.game.Constants;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.physics.util.PhysicsHelper;
import com.mygdx.game.renderer.util.GameplayRendererHelper;
import com.mygdx.game.renderer.util.HudRendererHelper;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class GameplayScreen implements Screen {
    private CoreGame game;
    @SuppressWarnings("FieldCanBeLocal")
    private Map<AreaId, TiledMap> maps;

    public void init(CoreGame game) {
        this.game = game;

        game.getEntityManager().getGamePhysics().setDebugRenderer(new Box2DDebugRenderer());

        Map<AreaId, String> mapsToLoad = new ConcurrentSkipListMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");

        maps = mapsToLoad.entrySet()
                         .stream()
                         .collect(Collectors.toMap(Map.Entry::getKey,
                                                   entry -> game.getEntityManager()
                                                                .getGameRenderer()
                                                                .loadMap(entry.getValue() + "/tile_map.tmx")));


        game.initState();

        game.getEntityManager().getGameRenderer().init();

        game.getEntityManager().getGamePhysics().init(maps, game);

        game.getEntityManager()
            .getGameRenderer()
            .getViewportsHandler()
            .setHudCameraPosition(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f); // TODO: move it inward?

    }

    @Override
    public void show() {
        game.setChatInputProcessor();

        game.getEntityManager().getGameRenderer().setupInitialRendererState(maps, game);
    }

    public void update(float delta) {
        game.performPhysicsWorldStep();

        PhysicsHelper.handleForceUpdateBodyPositions(game);

        game.onUpdate();

        game.getEventProcessor().process(game.getEntityManager(), game);

        game.getEventProcessor()
            .getTeleportEvents()
            .forEach(teleportInfo -> game.getEntityManager().teleportCreature(teleportInfo, game));
        game.getEventProcessor().getTeleportEvents().clear();

        game.getGameState().updateGeneralTimer(delta);

        game.getEntityManager().updateCreatures(delta, game);
        game.getEntityManager().updateAbilities(delta, game);

        PhysicsHelper.processPhysicsEventQueue(game);

        game.getEntityManager()
            .getGameRenderer()
            .getAreaRenderers()
            .get(game.getGameState().getCurrentAreaId())
            .setView(game.getEntityManager().getGameRenderer().getViewportsHandler().getWorldCamera());

        if (game.getGameState().getThisClientPlayerId() != null &&
            game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId()) != null) {
            game.updateCameraPositions();
        }


    }

    @Override
    public void render(float delta) {
        if (game.isInitialized()) {
            update(delta);
            if (game.isRenderingAllowed()) {
                // TODO: move this to viewports handler
                game.getEntityManager()
                    .getGameRenderer()
                    .getWorldElementsRenderingLayer()
                    .setProjectionMatrix(game.getEntityManager()
                                             .getGameRenderer()
                                             .getViewportsHandler()
                                             .getWorldCamera().combined);
                game.getEntityManager()
                    .getGameRenderer()
                    .getHudRenderingLayer()
                    .setProjectionMatrix(game.getEntityManager()
                                             .getGameRenderer()
                                             .getViewportsHandler()
                                             .getHudCamera().combined);
                game.getEntityManager()
                    .getGameRenderer()
                    .getWorldTextRenderingLayer()
                    .setProjectionMatrix(game.getEntityManager()
                                             .getGameRenderer()
                                             .getViewportsHandler()
                                             .getWorldTextCamera().combined);

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

                HudRendererHelper.renderHud(game);
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        game.getEntityManager().getGameRenderer().getViewportsHandler().updateViewportsOnResize(width, height);
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
