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

        renderer.setWorldCamera(new OrthographicCamera());

        renderer.setHudCamera(new OrthographicCamera());
        renderer.getHudCamera().position.set(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f, 0);

        renderer.setWorldViewport(new FitViewport(
                Constants.ViewpointWorldWidth / Constants.PPM,
                Constants.ViewpointWorldHeight / Constants.PPM,
                renderer.getWorldCamera()
        ));

        renderer.setHudViewport(
                new FitViewport((float) Constants.WindowWidth, (float) Constants.WindowHeight,
                        renderer.getHudCamera()));


        renderer.setCreatureSprites(new HashMap<>());

        renderer.setMapLoader(new TmxMapLoader());

        Map<AreaId, String> mapsToLoad = new HashMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");
        renderer.setMapsToLoad(mapsToLoad);

        renderer.setMaps(mapsToLoad.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> renderer.getMapLoader().load(entry.getValue() + "/tile_map.tmx"))));

        renderer.setMapScale(4.0f);

        renderer.setTiledMapRenderer(new OrthogonalTiledMapRenderer(renderer.getMaps().get(AreaId.of("area1")),
                renderer.getMapScale() / Constants.PPM));


        renderer.setWorldDrawingLayer(DrawingLayer.of());
        renderer.setHudDrawingLayer(DrawingLayer.of());


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

        renderer.getTiledMapRenderer().setView(renderer.getWorldCamera());


        updateCamera();


//        game.onRender();
    }

    @Override
    public void render(float delta) {
        update(delta);

        renderer.getWorldDrawingLayer().setProjectionMatrix(renderer.getWorldCamera().combined);
        renderer.getHudDrawingLayer().setProjectionMatrix(renderer.getHudCamera().combined);

        Gdx.gl.glClearColor(1, 0, 0, 1);

        int coverageBuffer;
        if (Gdx.graphics.getBufferFormat().coverageSampling) coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
        else coverageBuffer = 0;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

        renderer.getTiledMapRenderer().render(new int[]{0, 1, 2, 3});

//        ScreenUtils.clear(1, 0, 0, 1);
        renderer.getWorldDrawingLayer().getSpriteBatch().begin();


//        renderer.getWorldDrawingLayer().getSpriteBatch().draw(game.img, 0, 0, 5,5);


        renderer.getCreatureSprites().forEach((creatureId, sprite) -> {
            if (game.gameState.getCreatures().containsKey(creatureId)) {
                sprite.setPosition(game.gameState.getCreatures().get(creatureId).getParams().getPos().getX(),
                        game.gameState.getCreatures().get(creatureId).getParams().getPos().getY());
                sprite.setSize(2.5f, 2.5f);
                sprite.draw(renderer.getWorldDrawingLayer().getSpriteBatch());
            }
        });

        renderer.getWorldDrawingLayer().getSpriteBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        renderer.getWorldViewport().update(width, height);
        renderer.getHudViewport().update(width, height);
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

        Vector3 camPosition = renderer.getWorldCamera().position;

        int camX = 0; // TODO
        int camY = 0;

        camPosition.x = (float) (Math.floor(camX * 100) / 100);
        camPosition.y = (float) (Math.floor(camY * 100) / 100);

        renderer.getWorldCamera().update();

    }

}
