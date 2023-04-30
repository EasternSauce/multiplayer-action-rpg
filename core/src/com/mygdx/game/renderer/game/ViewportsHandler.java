package com.mygdx.game.renderer.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Constants;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.creature.Creature;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class ViewportsHandler {
    private Viewport worldViewport;
    private Viewport hudViewport;
    private Viewport worldTextViewport;

    private final OrthographicCamera worldCamera = new OrthographicCamera();
    private final OrthographicCamera hudCamera = new OrthographicCamera();
    private final OrthographicCamera worldTextCamera = new OrthographicCamera();

    public void initViewports() {
        worldViewport = new FitViewport(Constants.ViewpointWorldWidth / Constants.PPM,
                Constants.ViewpointWorldHeight / Constants.PPM,
                worldCamera);


        hudViewport = new FitViewport((float) Constants.WindowWidth, (float) Constants.WindowHeight, hudCamera);


        worldTextViewport =
                new FitViewport(Constants.ViewpointWorldWidth, Constants.ViewpointWorldHeight, worldTextCamera);
    }

    public void updateViewportsOnResize(int width, int height) {
        worldViewport.update(width, height);
        hudViewport.update(width, height);
        worldTextViewport.update(width, height);
    }

    public void updateCameraPositions(GameRenderable game) {
        Creature player = game.getGameState().getCreature(game.getThisClientPlayerId());

        float cameraX = player.getParams().getPos().getX();
        float cameraY = player.getParams().getPos().getY();

        float smoothenedCameraX = (float) (Math.floor(cameraX * 100) / 100);
        float smoothenedCameraY = (float) (Math.floor(cameraY * 100) / 100);

        setWorldCameraPosition(smoothenedCameraX, smoothenedCameraY);
        // world text viewport is not scaled down!
        setWorldTextCameraPosition(smoothenedCameraX * Constants.PPM, smoothenedCameraY * Constants.PPM);

        worldCamera.update();
        worldTextCamera.update();
    }

    public Matrix4 getWorldCameraCombinedProjectionMatrix() {
        return worldCamera.combined;
    }

    public void unprojectHudCamera(Vector3 screenCoords) {
        hudCamera.unproject(screenCoords);
    }

    public void setHudCameraPosition(float x, float y) {
        hudCamera.position.set(x, y, 0);
    }

    private void setWorldCameraPosition(float x, float y) {
        worldCamera.position.x = x;
        worldCamera.position.y = y;
    }

    private void setWorldTextCameraPosition(float x, float y) {
        worldTextCamera.position.x = x;
        worldTextCamera.position.y = y;
    }


    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    public OrthographicCamera getWorldTextCamera() {
        return worldTextCamera;
    }
}
