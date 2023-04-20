package com.mygdx.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mygdx.game.Constants;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.physics.util.PhysicsHelper;
import com.mygdx.game.renderer.util.GameplayRendererHelper;
import com.mygdx.game.renderer.util.HudRendererHelper;
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

        game.physics().setDebugRenderer(new Box2DDebugRenderer());

        Map<AreaId, String> mapsToLoad = new ConcurrentSkipListMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");
        //        game.renderer().mapsToLoad(mapsToLoad);


        maps = mapsToLoad.entrySet()
                         .stream()
                         .collect(Collectors.toMap(Map.Entry::getKey,
                                                   entry -> game.renderer()
                                                                .loadMap(entry.getValue() + "/tile_map.tmx")));


        game.renderer().init(maps, game);

        game.physics().init(maps, game);

        game.renderer()
            .getViewportsHandler()
            .setHudCameraPosition(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f); // TODO: move it inward?

        game.initState();


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                char backspaceCharacter = '\b';
                if (game.getChat().getIsTyping() &&
                    character != backspaceCharacter &&
                    isCharacterNonWhitespaceExcludingSpace(character)) {
                    game.getChat().setCurrentMessage(game.getChat().getCurrentMessage() + character);
                }

                return true;
            }

            private boolean isCharacterNonWhitespaceExcludingSpace(char character) {
                return character == ' ' || !(Character.isWhitespace(character));
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

        game.gameState().getGeneralTimer().update(delta);

        game.updateCreatures(delta);
        game.updateAbilities(delta);

        PhysicsHelper.processPhysicsEventQueue(game);

        game.renderer()
            .getAreaRenderers()
            .get(game.getCurrentPlayerAreaId())
            .setView(game.renderer().getViewportsHandler().getWorldCamera());

        if (game.getCurrentPlayerId() != null && game.getCreature(game.getCurrentPlayerId()) != null) {
            game.updateCameraPositions();
        }


    }

    @Override
    public void render(float delta) {
        if (getGame().isInitialized()) {
            update(delta);
            if (getGame().isRenderingAllowed()) {
                getGame().renderer()
                         .getWorldElementsRenderingLayer()
                         .setProjectionMatrix(getGame().renderer().getViewportsHandler().getWorldCamera().combined);
                getGame().renderer()
                         .getHudRenderingLayer()
                         .setProjectionMatrix(getGame().renderer().getViewportsHandler().getHudCamera().combined);
                getGame().renderer()
                         .getWorldTextRenderingLayer()
                         .setProjectionMatrix(getGame().renderer().getViewportsHandler().getWorldTextCamera().combined);

                Gdx.gl.glClearColor(0, 0, 0, 1);

                int coverageBuffer;
                if (Gdx.graphics.getBufferFormat().coverageSampling) {
                    coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
                }
                else {
                    coverageBuffer = 0;
                }

                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

                GameplayRendererHelper.renderGameplay(getGame());

                HudRendererHelper.renderHud(getGame());
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        game.renderer().getViewportsHandler().updateViewportsOnResize(width, height);
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
