package com.mygdx.game.model.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Constants;

public class MyGdxGamePlayScreen implements Screen {
    MyGdxGame game;

    public MyGdxGamePlayScreen(MyGdxGame game) {
        this.game = game;
    }

    World world;
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera worldCamera;
    FitViewport worldViewport;

    public void init() {
        world = new World(new com.badlogic.gdx.math.Vector2(0, 0), true);
        debugRenderer = new Box2DDebugRenderer();

        worldCamera = new OrthographicCamera();

        worldViewport = new FitViewport(
                Constants.ViewpointWorldWidth / Constants.PPM,
                Constants.ViewpointWorldHeight / Constants.PPM,
                worldCamera
        );
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        world.step(1 / 60f, 6, 2);
        debugRenderer.render(world, worldCamera.combined);

        game.onUpdate();

        updateCamera();

        game.onRender();
    }

    @Override
    public void resize(int width, int height) {

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

        Vector3 camPosition = worldCamera.position;

        Integer camX = 0; // TODO
        Integer camY = 0;

        camPosition.x = (float) (Math.floor(camX * 100) / 100);
        camPosition.y = (float) (Math.floor(camY * 100) / 100);

        worldCamera.update();

    }
}
