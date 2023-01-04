package com.mygdx.game.model.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Constants;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.renderer.DrawingLayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class MyGdxGamePlayScreen implements Screen {

    MyGdxGame game;

    //    Box2DDebugRenderer debugRenderer;

    GameRenderer renderer;

    public void init(MyGdxGame game) {
        this.game = game;
        this.renderer = game.renderer;
//        world = new World(new com.badlogic.gdx.math.Vector2(0, 0), true);
//        debugRenderer = new Box2DDebugRenderer();

        renderer.worldCamera(new OrthographicCamera());

        renderer.hudCamera(new OrthographicCamera());
        renderer.hudCamera().position.set(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f, 0);

        renderer.worldViewport(new FitViewport(
                Constants.ViewpointWorldWidth / Constants.PPM,
                Constants.ViewpointWorldHeight / Constants.PPM,
                renderer.worldCamera()
        ));

        renderer.hudViewport(
                new FitViewport((float) Constants.WindowWidth, (float) Constants.WindowHeight,
                        renderer.hudCamera()));


        renderer.creatureSprites(new HashMap<>());

        renderer.mapLoader(new TmxMapLoader());

        Map<AreaId, String> mapsToLoad = new HashMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");
        renderer.mapsToLoad(mapsToLoad);

        renderer.maps(mapsToLoad.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> renderer.mapLoader().load(entry.getValue() + "/tile_map.tmx"))));

        renderer.mapScale(4.0f);

        renderer.tiledMapRenderer(new OrthogonalTiledMapRenderer(renderer.maps().get(AreaId.of("area1")),
                renderer.mapScale() / Constants.PPM));


        renderer.worldDrawingLayer(DrawingLayer.of());
        renderer.hudDrawingLayer(DrawingLayer.of());


//        gameState = AtomicSTRef(
//                GameState(
//                        creatures = Map(
//                                CreatureId("player") -> Player(id = CreatureId("player"), areaId = AreaId("area1"), pos = Vec2(8, 61)),
//                CreatureId("skellie") -> Skeleton(id = CreatureId("skellie"), areaId = AreaId("area1"), pos = Vec2(24, 4))
//        ),
//        currentPlayerId = CreatureId("player"),
//                currentAreaId = AreaId("area1")
//      )
//    )
//
//        implicit val (_, events) = gameState.commit(GameState.init(mapsToLoad.keys.toList)(gameState.aref.get()))
//
//        RendererController.init(atlas, maps, areaGates, mapScale)(gameState.aref.get())
//        PhysicsEngineController.init(maps)(gameState.aref.get())
//
//        processExternalEvents(events)(
//                gameState.aref.get()
//        ) // process all queued events after init and before game loop starts
    }

    @Override
    public void show() {

    }

    public void update(float delta) {
//        world.step(1 / 60f, 6, 2);
//        debugRenderer.render(world, worldCamera.combined);

        game.onUpdate();

//        PhysicsEngineController.physicsEventQueue.clear()
//        processExternalEvents(events)
//        RendererController.update()
//        PhysicsEngineController.update()

        renderer.tiledMapRenderer().setView(renderer.worldCamera());


        updateCamera();


//        game.onRender();
    }

    @Override
    public void render(float delta) {
        update(delta);

        renderer.worldDrawingLayer().setProjectionMatrix(renderer.worldCamera().combined);
        renderer.hudDrawingLayer().setProjectionMatrix(renderer.hudCamera().combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);

        int coverageBuffer;
        if (Gdx.graphics.getBufferFormat().coverageSampling) coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
        else coverageBuffer = 0;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

        renderer.tiledMapRenderer().render(new int[]{0, 1, 2, 3});

//        ScreenUtils.clear(1, 0, 0, 1);
        renderer.worldDrawingLayer().spriteBatch().begin();


//        renderer.worldDrawingLayer().spriteBatch().draw(game.img, 0, 0, 5,5);


        renderer.creatureSprites().forEach((creatureId, sprite) -> {
            if (game.gameState.creatures().containsKey(creatureId)) {
                sprite.setPosition(game.gameState.creatures().get(creatureId).params().pos().x(),
                        game.gameState.creatures().get(creatureId).params().pos().y());
                sprite.setSize(2.5f, 2.5f);
                sprite.draw(renderer.worldDrawingLayer().spriteBatch());
            }
        });

        renderer.worldDrawingLayer().spriteBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        renderer.worldViewport().update(width, height);
        renderer.hudViewport().update(width, height);
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

        int camX;
        int camY;

        if (game.thisPlayerId != null) {
            Creature player = game.gameState.creatures().get(game.thisPlayerId);

            camX = (int) player.params().pos().x();
            camY = (int) player.params().pos().y();

        } else {
            camX = 0;
            camY = 0;
        }

        Vector3 camPosition = renderer.worldCamera().position;


        camPosition.x = (float) (Math.floor(camX * 100) / 100);
        camPosition.y = (float) (Math.floor(camY * 100) / 100);

        renderer.worldCamera().update();

    }

}
