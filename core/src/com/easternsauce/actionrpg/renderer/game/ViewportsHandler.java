package com.easternsauce.actionrpg.renderer.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
public class ViewportsHandler {
  private final OrthographicCamera worldCamera = new OrthographicCamera();
  private final OrthographicCamera hudCamera = new OrthographicCamera();
  private final OrthographicCamera worldTextCamera = new OrthographicCamera();
  private Viewport worldViewport;
  private Viewport hudViewport;
  private Viewport worldTextViewport;

  public void initViewports() {
    worldViewport = new FitViewport(Constants.VIEWPOINT_WORLD_WIDTH / Constants.PPM, Constants.VIEWPOINT_WORLD_HEIGHT / Constants.PPM, worldCamera);

    hudViewport = new FitViewport((float) Constants.WINDOW_WIDTH, (float) Constants.WINDOW_HEIGHT, hudCamera);

    worldTextViewport = new FitViewport(Constants.VIEWPOINT_WORLD_WIDTH, Constants.VIEWPOINT_WORLD_HEIGHT, worldTextCamera);

    setHudCameraPosition(Constants.WINDOW_WIDTH / 2f, Constants.WINDOW_HEIGHT / 2f);
  }

  @SuppressWarnings("SameParameterValue")
  private void setHudCameraPosition(float x, float y) {
    hudCamera.position.set(x, y, 0);
  }

  public void updateViewportsOnResize(int width, int height) {
    worldViewport.update(width, height);
    hudViewport.update(width, height);
    worldTextViewport.update(width, height);
  }

  public void updateCameraPositions(CoreGame game) {
    Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

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

  private void setWorldCameraPosition(float x, float y) {
    worldCamera.position.x = x;
    worldCamera.position.y = y;
  }

  private void setWorldTextCameraPosition(float x, float y) {
    worldTextCamera.position.x = x;
    worldTextCamera.position.y = y;
  }

  public Matrix4 getWorldCameraCombinedProjectionMatrix() {
    return worldCamera.combined;
  }

  public void unprojectHudCamera(Vector3 screenCoords) {
    hudCamera.unproject(screenCoords);
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
