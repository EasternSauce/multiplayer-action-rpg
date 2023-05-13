package com.mygdx.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class ConnectScreen implements Screen {
    private CoreGame game;

    private ConnectScreenMessageHolder messageHolder = ConnectScreenMessageHolder.of();

    private Boolean isHoldingBackspace = false;

    private Float holdBackspaceTime = 0f;

    private SimpleTimer timer = SimpleTimer.of();

    private TextureAtlas.AtlasRegion background;

    public void init(TextureAtlas atlas, CoreGame game) {
        this.game = game;

        background = atlas.findRegion("background2");
    }

    @Override
    public void show() {
        timer.start();

        game.setConnectScreenInputProcessor(messageHolder);
    }

    @Override
    public void render(float delta) {
        timer.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (isNameValid(messageHolder.getCurrentMessage())) {
                game.initializePlayer(messageHolder.getCurrentMessage());
                game.goToGamePlayScreen();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {

            if (isHoldingBackspace) {
                if (!messageHolder.getCurrentMessage().isEmpty() && timer.getTime() > holdBackspaceTime + 0.3f) {
                    messageHolder.setCurrentMessage(messageHolder
                                                        .getCurrentMessage()
                                                        .substring(0, messageHolder.getCurrentMessage().length() - 1));
                }
            }
            else {
                isHoldingBackspace = true;
                holdBackspaceTime = timer.getTime();
                if (!messageHolder.getCurrentMessage().isEmpty()) {
                    messageHolder.setCurrentMessage(messageHolder
                                                        .getCurrentMessage()
                                                        .substring(0, messageHolder.getCurrentMessage().length() - 1));
                }
            }
        }
        else {
            if (isHoldingBackspace) {
                isHoldingBackspace = false;
            }
        }

        SpriteBatch spriteBatch = game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer().getSpriteBatch();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        spriteBatch.begin();

        spriteBatch.draw(background,
                         (Gdx.graphics.getWidth() - background.originalWidth) / 2f,
                         (Gdx.graphics.getHeight() - background.originalHeight) / 2f);

        Assets.renderMediumFont(game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer(),
                                "Your character name:",
                                Vector2.of(centerX - 120f, centerY + 100f),
                                Color.BLACK);

        Assets.renderMediumFont(game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer(),
                                messageHolder.getCurrentMessage(),
                                Vector2.of(centerX - 120f, centerY + 70f),
                                Color.BLACK);

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
