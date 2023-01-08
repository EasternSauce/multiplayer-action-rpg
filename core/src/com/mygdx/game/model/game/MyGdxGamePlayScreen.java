package com.mygdx.game.model.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Constants;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.physics.GamePhysics;
import com.mygdx.game.model.renderer.DrawingLayer;
import com.mygdx.game.model.renderer.GameRenderer;
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

    GameRenderer gameRenderer;

    GamePhysics gamePhysics;

    protected Texture img;


    public void init(MyGdxGame game) {
        this.game = game;
        this.gameRenderer = game.gameRenderer;
        this.gamePhysics = game.gamePhysics;
        gamePhysics.world(new World(new com.badlogic.gdx.math.Vector2(0, 0), true));
        gamePhysics.debugRenderer(new Box2DDebugRenderer());

        gameRenderer.worldCamera(new OrthographicCamera());

        gameRenderer.hudCamera(new OrthographicCamera());
        gameRenderer.hudCamera().position.set(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f, 0);

        gameRenderer.worldViewport(new FitViewport(
                Constants.ViewpointWorldWidth / Constants.PPM,
                Constants.ViewpointWorldHeight / Constants.PPM,
                gameRenderer.worldCamera()
        ));

        gameRenderer.hudViewport(
                new FitViewport((float) Constants.WindowWidth, (float) Constants.WindowHeight,
                        gameRenderer.hudCamera()));


//        gameRenderer.creatureSprites(new HashMap<>());

        gameRenderer.mapLoader(new TmxMapLoader());

        Map<AreaId, String> mapsToLoad = new HashMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");
        gameRenderer.mapsToLoad(mapsToLoad);

        gameRenderer.maps(mapsToLoad.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> gameRenderer.mapLoader().load(entry.getValue() + "/tile_map.tmx"))));

        gameRenderer.mapScale(4.0f);

        gameRenderer.tiledMapRenderer(new OrthogonalTiledMapRenderer(gameRenderer.maps().get(AreaId.of("area1")),
                gameRenderer.mapScale() / Constants.PPM));


        gameRenderer.worldDrawingLayer(DrawingLayer.of());
        gameRenderer.hudDrawingLayer(DrawingLayer.of());

        gameRenderer.atlas(new TextureAtlas("assets/atlas/packed_atlas.atlas"));

        gameRenderer.creatureAnimations(new HashMap<>());

        gamePhysics.creatureBodies(new HashMap<>());

        img = new Texture("badlogic.jpg");


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
        game.onUpdate();

//        PhysicsEngineController.physicsEventQueue.clear()
//        processExternalEvents(events)
//        RendererController.update()
//        PhysicsEngineController.update()


        gamePhysics.creatureBodies().forEach((creatureId, creatureBody) -> creatureBody.update(game.gameState));

        // set gamestate position based on b2body position
        game.gameState.creatures().forEach(
                (creatureId, creature) -> creature.params().pos(gamePhysics.creatureBodies().get(creatureId).pos()));

        gameRenderer.creatureAnimations()
                .forEach((creatureId, creatureAnimation) -> creatureAnimation.update(game.gameState));


        //update gamestate

        game.gameState.creatures().forEach((creatureId, creature) -> creature.update(delta));

        gameRenderer.tiledMapRenderer().setView(gameRenderer.worldCamera());


        updateCamera();


//        game.onRender();

        gamePhysics.world().step(1 / 60f, 6, 2);

    }

    @Override
    public void render(float delta) {
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
                .forEach((creatureId, creatureAnimation) -> creatureAnimation.render(gameRenderer.worldDrawingLayer()));

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

        gamePhysics.debugRenderer().render(gamePhysics.world(), gameRenderer.worldCamera().combined);
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
        Creature player = game.gameState.creatures().get(game.thisPlayerId);

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
