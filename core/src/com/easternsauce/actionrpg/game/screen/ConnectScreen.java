package com.easternsauce.actionrpg.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class ConnectScreen implements Screen {
  private final ConnectScreenMessageHolder messageHolder = ConnectScreenMessageHolder.of();
  private final SimpleTimer timer = SimpleTimer.of();
  private CoreGame game;
  private boolean holdingBackspace = false;
  private double holdBackspaceTime = 0f;
  private TextureAtlas.AtlasRegion background;
  private boolean waitingToEnter = false;

  public void init(TextureAtlas atlas, CoreGame game) {
    this.game = game;

    background = atlas.findRegion("background2");
  }

  @Override
  public void show() {
    timer.start();

    setInputProcessor();
  }

  private void setInputProcessor() {
    Gdx.input.setInputProcessor(new InputAdapter() {
      @Override
      public boolean keyTyped(char character) {
        if ((Character.isAlphabetic(character) || Character.isDigit(character)) &&
          messageHolder.getCurrentMessage().length() <= 20f) {
          messageHolder.setCurrentMessage(messageHolder.getCurrentMessage().concat("" + character));
        }

        return true;
      }
    });
  }

  @Override
  public void render(float delta) {
    timer.update(delta);

    if (waitingToEnter) {
      if (game.getFirstNonStubBroadcastReceived()) {
        game.goToGamePlayScreen();
        waitingToEnter = false;
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      if (isNameValid(messageHolder.getCurrentMessage())) {
        game.initializePlayer(messageHolder.getCurrentMessage());

        game.askForBroadcast();

        waitingToEnter = true;
      }
    }

    if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
      if (holdingBackspace) {
        if (!messageHolder.getCurrentMessage().isEmpty() && timer.getTime() > holdBackspaceTime + 0.3f) {
          messageHolder.setCurrentMessage(
            messageHolder.getCurrentMessage().substring(0, messageHolder.getCurrentMessage().length() - 1));
        }
      } else {
        holdingBackspace = true;
        holdBackspaceTime = timer.getTime();
        if (!messageHolder.getCurrentMessage().isEmpty()) {
          messageHolder.setCurrentMessage(
            messageHolder.getCurrentMessage().substring(0, messageHolder.getCurrentMessage().length() - 1));
        }
      }
    } else {
      if (holdingBackspace) {
        holdingBackspace = false;
      }
    }

    SpriteBatch spriteBatch = game.getHudRenderingLayer().getSpriteBatch();

    spriteBatch.begin();

    spriteBatch.draw(background, ConnectScreenConsts.BACKGROUND_POS_X, ConnectScreenConsts.BACKGROUND_POS_Y);

    Assets.renderMediumFont(game.getHudRenderingLayer(), "Your character name:",
      Vector2.of(ConnectScreenConsts.PROMPT_POS_X, ConnectScreenConsts.PROMPT_POS_Y), Color.BLACK);

    Assets.renderMediumFont(game.getHudRenderingLayer(), messageHolder.getCurrentMessage(),
      Vector2.of(ConnectScreenConsts.INPUT_POS_X, ConnectScreenConsts.INPUT_POS_Y), Color.BLACK);

    spriteBatch.end();
  }

  private boolean isNameValid(String currentMessage) {
    return !currentMessage.isEmpty();
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
}
