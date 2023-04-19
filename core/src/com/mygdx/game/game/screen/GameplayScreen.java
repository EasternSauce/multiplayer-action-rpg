package com.mygdx.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Constants;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.physics.util.PhysicsHelper;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.util.RendererHelper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
public class GameplayScreen implements Screen {

    MyGdxGame game;

    //    Box2DDebugRenderer debugRenderer;

    Map<AreaId, TiledMap> maps;

    public void init(MyGdxGame game) {
        this.game = game;

        game.physics().debugRenderer(new Box2DDebugRenderer());

        game.renderer().hudCamera().position.set(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f, 0);

        game.renderer()
            .worldViewport(new FitViewport(Constants.ViewpointWorldWidth / Constants.PPM,
                                           Constants.ViewpointWorldHeight / Constants.PPM,
                                           game.renderer().worldCamera()));

        game.renderer()
            .hudViewport(new FitViewport((float) Constants.WindowWidth,
                                         (float) Constants.WindowHeight,
                                         game.renderer().hudCamera()));

        game.renderer()
            .worldTextViewport(new FitViewport(Constants.ViewpointWorldWidth,
                                               Constants.ViewpointWorldHeight,
                                               game.renderer().worldTextCamera()));

        Map<AreaId, String> mapsToLoad = new ConcurrentSkipListMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");
        //        game.renderer().mapsToLoad(mapsToLoad);


        maps(mapsToLoad.entrySet()
                       .stream()
                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                 entry -> game.renderer()
                                                              .mapLoader()
                                                              .load(entry.getValue() + "/tile_map.tmx"))));

        game.renderer().mapScale(4.0f);

        game.renderer().worldRenderingLayer(RenderingLayer.of());
        game.renderer().hudRenderingLayer(RenderingLayer.of());
        game.renderer().worldTextRenderingLayer(RenderingLayer.of());


        game.renderer().atlas(new TextureAtlas("assets/atlas/packed_atlas.atlas"));

        game.renderer().init(maps, game);

        game.physics().init(maps, game);

        game.initState();


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (game.getChat().isTyping() &&
                    character != '\b' &&
                    (character == ' ' || !(Character.isWhitespace(character)))) {
                    game.getChat().currentMessage(game.getChat().currentMessage() + character);
                }

                return true;
            }
        });

    }

    public void update(float delta) {
        game.performPhysicsWorldStep();

        PhysicsHelper.handleForceUpdateBodyPositions(game);

        game.onUpdate();

        game.getCreatureModelsToBeCreated().forEach(creatureId -> game.createCreature(creatureId));
        game.getCreatureModelsToBeCreated().clear();

        game.getAbilityModelsToBeCreated().forEach(abilityId -> game.createAbility(abilityId));
        game.getAbilityModelsToBeCreated().clear();

        game.getAbilitiesToBeActivated().forEach(abilityId -> game.activateAbility(abilityId));
        game.getAbilitiesToBeActivated().clear();

        game.getCreatureModelsToBeRemoved().forEach(creatureId -> game.removeCreature(creatureId));
        game.getCreatureModelsToBeRemoved().clear();

        game.getAbilityModelsToBeRemoved().forEach(abilityId -> game.removeAbility(abilityId));
        game.getAbilityModelsToBeRemoved().clear();

        game.getLootPileModelsToBeCreated().forEach(lootPileId -> game.createLootPile(lootPileId));
        game.getLootPileModelsToBeCreated().clear();

        game.getLootPileModelsToBeRemoved().forEach(lootPileId -> game.removeLootPile(lootPileId));
        game.getLootPileModelsToBeRemoved().clear();

        game.teleportEvents().forEach(teleportInfo -> game.teleportCreature(teleportInfo));
        game.teleportEvents().clear();

        game.gameState().generalTimer().update(delta);

        game.updateCreatures(delta);
        game.updateAbilities(delta);

        PhysicsHelper.processPhysicsEventQueue(game);

        game.renderer().areaRenderers().get(game.getCurrentPlayerAreaId()).setView(game.renderer().worldCamera());

        RendererHelper.updateCameraPositions(game);

    }

    @Override
    public void render(float delta) {


        if (game().isInitialized()) {
            update(delta);
            if (game().isRenderingAllowed()) {
                game.renderer().worldRenderingLayer().setProjectionMatrix(game.renderer().worldCamera().combined);
                game.renderer().hudRenderingLayer().setProjectionMatrix(game.renderer().hudCamera().combined);
                game.renderer()
                    .worldTextRenderingLayer()
                    .setProjectionMatrix(game.renderer().worldTextCamera().combined);

                Gdx.gl.glClearColor(0, 0, 0, 1);

                int coverageBuffer;
                if (Gdx.graphics.getBufferFormat().coverageSampling) {
                    coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
                }
                else {
                    coverageBuffer = 0;
                }

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

                RendererHelper.renderWorld(game);

                RendererHelper.renderHud(game);


            }
        }

    }


    @Override
    public void resize(int width, int height) {
        game.renderer().worldViewport().update(width, height);
        game.renderer().hudViewport().update(width, height);
        game.renderer().worldTextViewport().update(width, height);
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
