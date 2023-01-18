package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Constants;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.renderer.GameRenderer;
import com.mygdx.game.util.GameStateHolder;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class MyGdxGamePlayScreen implements Screen {

    MyGdxGame game;

    //    Box2DDebugRenderer debugRenderer;

    GameRenderer gameRenderer;

    GamePhysics gamePhysics;

    GameStateHolder gameStateHolder;

    protected Texture img;
    Map<AreaId, TiledMap> maps;

    public void init(MyGdxGame game) {
        this.game = game;
        this.gameRenderer = game.gameRenderer;
        this.gamePhysics = game.gamePhysics;
        this.gameStateHolder = game.gameStateHolder;

        gamePhysics.debugRenderer(new Box2DDebugRenderer());

        gameRenderer.hudCamera().position.set(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f, 0);

        gameRenderer.worldViewport(new FitViewport(
                Constants.ViewpointWorldWidth / Constants.PPM,
                Constants.ViewpointWorldHeight / Constants.PPM,
                gameRenderer.worldCamera()
        ));

        gameRenderer.hudViewport(
                new FitViewport((float) Constants.WindowWidth, (float) Constants.WindowHeight,
                        gameRenderer.hudCamera()));

        Map<AreaId, String> mapsToLoad = new HashMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");
        gameRenderer.mapsToLoad(mapsToLoad);

        maps(mapsToLoad.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> gameRenderer.mapLoader().load(entry.getValue() + "/tile_map.tmx"))));

        gameRenderer.mapScale(4.0f);

        gameRenderer.tiledMapRenderer(new OrthogonalTiledMapRenderer(maps().get(AreaId.of("area1")),
                gameRenderer.mapScale() / Constants.PPM));


        gameRenderer.worldDrawingLayer(DrawingLayer.of());
        gameRenderer.hudDrawingLayer(DrawingLayer.of());

        gameRenderer.atlas(new TextureAtlas("assets/atlas/packed_atlas.atlas"));

        gamePhysics.init(maps, gameStateHolder.gameState());

        img = new Texture("badlogic.jpg");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (game.chat.isTyping() && character != '\n') {
                    game.chat.currentMessage(game.chat.currentMessage() + character);
                }

                return true;
            }
        });

        try {
            game.establishConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        game.initState();

    }

    @Override
    public void show() {

    }

    public void update(float delta) {

        gamePhysics.physicsWorlds().get(gameStateHolder.gameState().currentAreaId()).b2world().step(1 / 60f, 6, 2);

        if (gamePhysics.forceUpdateCreaturePositions()) {
            gamePhysics.forceUpdateCreaturePositions(false);

            gameStateHolder.gameState().creatures().forEach((creatureId, creature) ->
            {
                System.out.println("updating...");
                System.out.println(
                        "contains " + creatureId + "? " + gamePhysics.creatureBodies().containsKey(creatureId));
                if (gamePhysics.creatureBodies().containsKey(creatureId)) {
                    System.out.println("setting transform to " + creature.params().pos() + " for creature " +
                            creature.params().creatureId());
                    gamePhysics.creatureBodies().get(creatureId).setTransform(creature.params().pos());
                }
            });
        }

        game.onUpdate();

        game.gameStateHolder.gameState().generalTimer().update(delta);

        gamePhysics.creatureBodies()
                .forEach((creatureId, creatureBody) -> creatureBody.update(game.gameStateHolder.gameState()));

        // set gamestate position based on b2body position
        game.gameStateHolder.gameState().creatures().forEach(
                (creatureId, creature) -> {
                    if (gamePhysics.creatureBodies().containsKey(creatureId)) {
                        creature.params().pos(gamePhysics.creatureBodies().get(creatureId).setTransform());
                    }

                });

        gameRenderer.creatureAnimations()
                .forEach((creatureId, creatureAnimation) -> creatureAnimation.update(game.gameStateHolder.gameState()));


        //update gamestate

        game.gameStateHolder.gameState().creatures().forEach((creatureId, creature) -> creature.update(delta));

        gameRenderer.tiledMapRenderer().setView(gameRenderer.worldCamera());


        updateCamera();


//        game.onRender();


    }

    @Override
    public void render(float delta) {

        if (game().isInitialized()) {
            update(delta);

            gameRenderer.worldDrawingLayer().setProjectionMatrix(gameRenderer.worldCamera().combined);
            gameRenderer.hudDrawingLayer().setProjectionMatrix(gameRenderer.hudCamera().combined);

            Gdx.gl.glClearColor(0, 0, 0, 1);

            int coverageBuffer;
            if (Gdx.graphics.getBufferFormat().coverageSampling) coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
            else coverageBuffer = 0;

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

            gameRenderer.tiledMapRenderer().render(new int[]{0, 1, 2, 3});

//        ScreenUtils.clear(1, 0, 0, 1);
            gameRenderer.worldDrawingLayer().spriteBatch().begin();


//        renderer.worldDrawingLayer().spriteBatch().draw(game.img, 0, 0, 5,5);


            gameRenderer.creatureAnimations()
                    .forEach((creatureId, creatureAnimation) -> creatureAnimation.render(
                            gameRenderer.worldDrawingLayer()));

//        renderer.creatureSprites().forEach((creatureId, sprite) -> {
//            if (game.gameState.creatures().containsKey(creatureId)) {
//                sprite.setPosition(game.gameState.creatures().get(creatureId).params().pos().x(),
//                        game.gameState.creatures().get(creatureId).params().pos().y());
//                sprite.setSize(2.5f, 2.5f);
//                sprite.draw(renderer.worldDrawingLayer().spriteBatch());
//            }
//        });

//        gameRenderer.worldDrawingLayer().spriteBatch().draw(img, 10, 10);

            gameRenderer.worldDrawingLayer().spriteBatch().end();

            gamePhysics.debugRenderer()
                    .render(gamePhysics.physicsWorlds().get(gameStateHolder.gameState().currentAreaId()).b2world(),
                            gameRenderer.worldCamera().combined);

            gameRenderer.hudDrawingLayer().spriteBatch().begin();


            for (int i = 0; i < Math.min(game.chat.messages().size(), 6); i++) {
                Assets.drawFont(gameRenderer.hudDrawingLayer(),
                        game.chat.messages().get(i).poster() + ": " + game.chat.messages().get(i).text(),
                        Vector2.of(30, 180 - 20 * i), Color.PURPLE);
            }

            Assets.drawFont(gameRenderer.hudDrawingLayer(),
                    (game.chat.isTyping() ? "> " : "") + game.chat.currentMessage(), Vector2.of(30, 30), Color.PURPLE);


            gameRenderer.hudDrawingLayer().spriteBatch().end();


        }
    }

    @Override
    public void resize(int width, int height) {
        gameRenderer.worldViewport().update(width, height);
        gameRenderer.hudViewport().update(width, height);
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

    public void updateCamera() {
        Creature player = game.gameStateHolder.gameState().creatures().get(game.thisPlayerId);

        if (player != null) {
            float camX;
            float camY;

            if (game.thisPlayerId != null) {

                camX = player.params().pos().x();
                camY = player.params().pos().y();

            } else {
                camX = 0;
                camY = 0;
            }

            Vector3 camPosition = gameRenderer.worldCamera().position;


            camPosition.x = (float) (Math.floor(camX * 100) / 100);
            camPosition.y = (float) (Math.floor(camY * 100) / 100);

            gameRenderer.worldCamera().update();
        }


    }

}
