package com.mygdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Constants;
import com.mygdx.game.ability.Ability;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.physics.event.AbilityHitsCreatureEvent;
import com.mygdx.game.physics.event.AbilityHitsTerrainEvent;
import com.mygdx.game.renderer.DrawingLayer;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class MyGdxGamePlayScreen implements Screen {

    MyGdxGame game;

    //    Box2DDebugRenderer debugRenderer;

    Map<AreaId, TiledMap> maps;

    private boolean debug = true;

    public void init(MyGdxGame game) {
        this.game = game;

        game.physics().debugRenderer(new Box2DDebugRenderer());

        game.renderer().hudCamera().position.set(Constants.WindowWidth / 2f, Constants.WindowHeight / 2f, 0);

        game.renderer().worldViewport(new FitViewport(
                Constants.ViewpointWorldWidth / Constants.PPM,
                Constants.ViewpointWorldHeight / Constants.PPM,
                game.renderer().worldCamera()
        ));

        game.renderer().hudViewport(
                new FitViewport((float) Constants.WindowWidth, (float) Constants.WindowHeight,
                        game.renderer().hudCamera()));

        Map<AreaId, String> mapsToLoad = new HashMap<>();
        mapsToLoad.put(AreaId.of("area1"), "assets/areas/area1");
        mapsToLoad.put(AreaId.of("area2"), "assets/areas/area2");
        mapsToLoad.put(AreaId.of("area3"), "assets/areas/area3");
        game.renderer().mapsToLoad(mapsToLoad);

        maps(mapsToLoad.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> game.renderer().mapLoader().load(entry.getValue() + "/tile_map.tmx"))));

        game.renderer().mapScale(4.0f);

        game.renderer().tiledMapRenderer(new OrthogonalTiledMapRenderer(maps().get(AreaId.of("area1")),
                game.renderer().mapScale() / Constants.PPM));


        game.renderer().worldDrawingLayer(DrawingLayer.of());
        game.renderer().hudDrawingLayer(DrawingLayer.of());

        game.renderer().atlas(new TextureAtlas("assets/atlas/packed_atlas.atlas"));

        game.physics().init(maps, game.gameState());

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (game.chat.isTyping() && character != '\b' &&
                        (character == ' ' || !(Character.isWhitespace(character)))) {
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

        game.physics().physicsWorlds().get(game.gameState().currentAreaId()).step();

        if (game.physics().forceUpdateCreaturePositions()) { // only runs after receiving game state update
            game.physics().forceUpdateCreaturePositions(false);

            game.gameState().creatures().forEach((creatureId, creature) ->
            {
                if (game.physics().creatureBodies().containsKey(creatureId) &&
                        game.physics().creatureBodies().get(creatureId)
                                .getBodyPos().distance(creature.params().pos()) >
                                0.2f // only setTransform if positions are far apart
                ) {
                    game.physics().creatureBodies().get(creatureId).trySetTransform(creature.params().pos());
                }
            });

            game.gameState().abilities().forEach((abilityId, ability) ->
            {
                if (game.physics().abilityBodies().containsKey(abilityId) &&
                        game.physics().abilityBodies().get(abilityId)
                                .getBodyPos().distance(ability.params().pos()) >
                                0.2f // only setTransform if positions are far apart
                ) {
                    game.physics().abilityBodies().get(abilityId).trySetTransform(ability.params().pos());
                }
            });
        }

        game.onUpdate();

        synchronized (game.creaturesToBeCreated()) {
            game.creaturesToBeCreated().forEach(creatureId -> game.createCreatureBodyAndAnimation(creatureId));
            game.creaturesToBeCreated().clear();
        }

        synchronized (game.abilitiesToBeCreated()) {
            game.abilitiesToBeCreated().forEach(abilityId -> game.createAbilityBodyAndAnimation(abilityId));
            game.abilitiesToBeCreated().clear();
        }

        synchronized (game.creaturesToBeRemoved()) {
            game.creaturesToBeRemoved().forEach(creatureId -> game.removeCreatureBodyAndAnimation(creatureId));
            game.creaturesToBeRemoved().clear();
        }

        synchronized (game.abilitiesToBeRemoved()) {
            game.abilitiesToBeRemoved().forEach(abilityId -> game.removeAbilityBodyAndAnimation(abilityId));
            game.abilitiesToBeRemoved().clear();
        }

        synchronized (game.creaturesToTeleport()) {
            game.creaturesToTeleport().forEach((creatureId, pos) -> {
                game.physics().creatureBodies().get(creatureId).forceSetTransform(pos);
                game.physics().setBodySensor(creatureId, false);
            });


            game.creaturesToTeleport().clear();
        }

        game.gameState().generalTimer().update(delta);

        game.physics().creatureBodies()
                .forEach((creatureId, creatureBody) -> creatureBody.update(game.gameState()));

        game.physics().abilityBodies()
                .forEach((abilityId, abilityBody) -> abilityBody.update(game.gameState()));


        // set gamestate position based on b2body position
        game.gameState().creatures().forEach(
                (creatureId, creature) -> {
                    if (game.physics().creatureBodies().containsKey(creatureId)) {
                        creature.params().pos(game.physics().creatureBodies().get(creatureId).getBodyPos());
                    }

                });

        game.gameState().abilities().forEach(
                (abilityId, ability) -> {
                    if (game.physics().abilityBodies().containsKey(abilityId)) {
                        ability.params().pos(game.physics().abilityBodies().get(abilityId).getBodyPos());
                    }

                });

        game.renderer().creatureRenderers()
                .forEach((creatureId, creatureAnimation) -> creatureAnimation.update(game.gameState()));

        game.renderer().abilityRenderers()
                .forEach((creatureId, creatureAnimation) -> creatureAnimation.update(game.gameState()));


        //update gamestate


        game.gameState().creatures()
                .forEach((creatureId, creature) -> creature.update(delta, game));

        game.gameState().abilities()
                .forEach((abilityId, ability) -> ability.update(delta, game));


        // process physics queue

        synchronized (game.physics().physicsEventQueue()) {
            game.physics().physicsEventQueue().forEach(physicsEvent -> {
                if (physicsEvent instanceof AbilityHitsCreatureEvent) {
                    AbilityHitsCreatureEvent event = (AbilityHitsCreatureEvent) physicsEvent;

                    Creature attackedCreature = game.gameState().creatures().get(event.attackedCreatureId());

                    Creature attackingCreature = game.gameState().creatures().get(event.attackingCreatureId());

                    boolean attackedIsPlayer = (attackedCreature instanceof Player);
                    boolean attackingIsPlayer = (attackingCreature instanceof Player);

                    Ability ability = game.gameState().abilities().get(event.abilityId());

                    if (ability != null && attackedCreature.isAlive()) {
                        if ((attackedIsPlayer || attackingIsPlayer) &&
                                !ability.params().creaturesAlreadyHit().contains(event.attackedCreatureId())) {
                            attackedCreature.takeDamage(ability.params().damage());
                        }

                        ability.params().creaturesAlreadyHit().add(event.attackedCreatureId());
                        ability.onCreatureHit();
                    }

                }
                if (physicsEvent instanceof AbilityHitsTerrainEvent) {
                    AbilityHitsTerrainEvent event = (AbilityHitsTerrainEvent) physicsEvent;

                    Ability ability = game.gameState().abilities().get(event.abilityId());

                    if (ability != null) {
                        ability.onTerrainHit();
                    }

                }
            });
            game.physics().physicsEventQueue().clear();
        }

        game.renderer().tiledMapRenderer().setView(game.renderer().worldCamera());


        updateCamera();


//        game.onRender();


    }

    @Override
    public void render(float delta) {

        if (game().isInitialized()) {
            update(delta);

            game.renderer().worldDrawingLayer().setProjectionMatrix(game.renderer().worldCamera().combined);
            game.renderer().hudDrawingLayer().setProjectionMatrix(game.renderer().hudCamera().combined);

            Gdx.gl.glClearColor(0, 0, 0, 1);

            int coverageBuffer;
            if (Gdx.graphics.getBufferFormat().coverageSampling) coverageBuffer = GL20.GL_COVERAGE_BUFFER_BIT_NV;
            else coverageBuffer = 0;

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | coverageBuffer);

            game.renderer().tiledMapRenderer().render(new int[]{0, 1, 2, 3});

//        ScreenUtils.clear(1, 0, 0, 1);
            game.renderer().worldDrawingLayer().spriteBatch().begin();


//        renderer.worldDrawingLayer().spriteBatch().draw(game.img, 0, 0, 5,5);


            game.renderer().abilityRenderers()
                    .forEach((abilityId, abilityAnimation) -> abilityAnimation.render(
                            game.renderer().worldDrawingLayer(), game.gameState()));

            game.renderer().renderDeadCreatures(game.renderer().worldDrawingLayer(), game.gameState());
            game.renderer().renderAliveCreatures(game.renderer().worldDrawingLayer(), game.gameState());

//        renderer.creatureSprites().forEach((creatureId, sprite) -> {
//            if (game.gameState.creatures().containsKey(creatureId)) {
//                sprite.setPosition(game.gameState.creatures().get(creatureId).params().pos().x(),
//                        game.gameState.creatures().get(creatureId).params().pos().y());
//                sprite.setSize(2.5f, 2.5f);
//                sprite.draw(renderer.worldDrawingLayer().spriteBatch());
//            }
//        });

//        game.renderer().worldDrawingLayer().spriteBatch().draw(img, 10, 10);

            game.renderer().worldDrawingLayer().spriteBatch().end();

            if (debug) {
                game.physics().debugRenderer()
                        .render(game.physics().physicsWorlds().get(game.gameState().currentAreaId()).b2world(),
                                game.renderer().worldCamera().combined);
            }

            game.renderer().hudDrawingLayer().spriteBatch().begin();


            for (int i = 0; i < Math.min(game.chat.messages().size(), 6); i++) {
                Assets.drawFont(game.renderer().hudDrawingLayer(),
                        game.chat.messages().get(i).poster() + ": " + game.chat.messages().get(i).text(),
                        Vector2.of(30, 180 - 20 * i), Color.PURPLE);
            }

            Assets.drawFont(game.renderer().hudDrawingLayer(),
                    (game.chat.isTyping() ? "> " : "") + game.chat.currentMessage(), Vector2.of(30, 30), Color.PURPLE);


            float fps = Gdx.graphics.getFramesPerSecond();
            Assets.drawFont(game.renderer().hudDrawingLayer(), fps + " fps", Vector2.of(3, Constants.WindowHeight - 3),
                    Color.WHITE);

            if (game.thisPlayerId != null) {
                Creature creature = game.gameState().creatures().get(game.thisPlayerId);

                if (creature != null && !creature.isAlive()) {
                    if (creature.params().respawnTimer().time() < creature.params().respawnTime()) {
                        Assets.drawLargeFont(game.renderer().hudDrawingLayer(), "You are dead!\nRespawning...\n" +
                                        String.format(Locale.US, "%.2f",
                                                (creature.params().respawnTime() - creature.params().respawnTimer().time())),
                                Vector2.of(Constants.WindowWidth / 2f - Constants.WindowWidth / 8f,
                                        Constants.WindowHeight / 2f + Constants.WindowHeight / 5f),
                                Color.RED);
                    }
                }
            }

            game.renderer().hudDrawingLayer().spriteBatch().end();


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

    public void updateCamera() {
        Creature player = null;

        if (game.thisPlayerId != null) player = game.gameState().creatures().get(game.thisPlayerId);

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

            Vector3 camPosition = game.renderer().worldCamera().position;


            camPosition.x = (float) (Math.floor(camX * 100) / 100);
            camPosition.y = (float) (Math.floor(camY * 100) / 100);

            game.renderer().worldCamera().update();
        }


    }

}
