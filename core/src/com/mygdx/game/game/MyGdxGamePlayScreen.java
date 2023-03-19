package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Constants;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.physics.util.PhysicsHelper;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.renderer.util.RendererHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class MyGdxGamePlayScreen implements Screen {

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

        game.renderer().worldDrawingLayer(DrawingLayer.of());
        game.renderer().hudDrawingLayer(DrawingLayer.of());

        game.renderer().atlas(new TextureAtlas("assets/atlas/packed_atlas.atlas"));


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

        try {
            game.establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        game.renderer().init(maps, game);

        game.physics().init(maps, game);

        game.initState();


    }

    @Override
    public void show() {

    }

    public void update(float delta) {

        game.performPhysicsWorldStep();

        PhysicsHelper.handleForceUpdateBodyPositions(game);

        game.onUpdate();

        game.getCreaturesToBeCreated().forEach(creatureId -> game.createCreature(creatureId));
        game.getCreaturesToBeCreated().clear();

        game.getAbilitiesToBeCreated().forEach(abilityId -> game.createAbility(abilityId));
        game.getAbilitiesToBeCreated().clear();

        game.getAbilitiesToBeActivated().forEach(abilityId -> game.activateAbility(abilityId));
        game.getAbilitiesToBeActivated().clear();

        game.getCreaturesToBeRemoved().forEach(creatureId -> game.removeCreature(creatureId));
        game.getCreaturesToBeRemoved().clear();

        game.getAbilitiesToBeRemoved().forEach(abilityId -> game.removeAbility(abilityId));
        game.getAbilitiesToBeRemoved().clear();

        game.getLootPilesToBeCreated().forEach(lootPileId -> game.createLootPile(lootPileId));
        game.getLootPilesToBeCreated().clear();

        game.getLootPilesToBeRemoved().forEach(lootPileId -> game.removeLootPile(lootPileId));
        game.getLootPilesToBeRemoved().clear();

        game.teleportEvents().forEach(teleportInfo -> game.teleportCreature(teleportInfo));
        game.teleportEvents().clear();

        game.gameState().generalTimer().update(delta);

        game.updateCreatures(delta);
        game.updateAbilities(delta);

        PhysicsHelper.processPhysicsEventQueue(game);

        game.renderer().areaRenderers().get(game.getCurrentPlayerAreaId()).setView(game.renderer().worldCamera());

        RendererHelper.updateCamera(game);

    }

    @Override
    public void render(float delta) {


        if (game().isInitialized()) {
            update(delta);
            if (game().isRenderingAllowed()) {
                game.renderer().worldDrawingLayer().setProjectionMatrix(game.renderer().worldCamera().combined);
                game.renderer().hudDrawingLayer().setProjectionMatrix(game.renderer().hudCamera().combined);

                Gdx.gl.glClearColor(0, 0, 0, 1);

                int coverageBuffer;
                if (Gdx.graphics.getBufferFormat().coverageSampling) {
                    coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
                }
                else {
                    coverageBuffer = 0;
                }

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

                RendererHelper.drawWorld(game);

                RendererHelper.drawHud(game);


            }
        }

    }


    @Override
    public void resize(int width, int height) {
        game.renderer().worldViewport().update(width, height);
        game.renderer().hudViewport().update(width, height);
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
