package com.mygdx.game.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class ConnectScreen implements Screen {
    private MyGdxGame game;

    private String currentMessage = "";
    private Boolean isHoldingBackspace = false;

    private Float holdBackspaceTime = 0f;

    private SimpleTimer timer = SimpleTimer.of();

    public void init(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        timer.start();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (character != '\b' && !(Character.isWhitespace(character)) && currentMessage.length() <= 20f) {
                    currentMessage = currentMessage.concat("" + character);
                }

                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        timer.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.initializePlayer(currentMessage);
            game.goToGamePlayScreen();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {

            if (isHoldingBackspace) {
                if (!currentMessage.isEmpty() &&
                    timer.time() > holdBackspaceTime + 0.3f) {
                    currentMessage = currentMessage.substring(0, currentMessage.length() - 1);
                }
            }
            else {
                isHoldingBackspace = true;
                holdBackspaceTime = timer.time();
                if (!currentMessage.isEmpty()) {
                    currentMessage = currentMessage.substring(0, currentMessage.length() - 1);
                }
            }
        }
        else {
            if (isHoldingBackspace) {
                isHoldingBackspace = false;
            }
        }


        SpriteBatch spriteBatch = game.renderer().hudRenderingLayer().spriteBatch();

        TextureAtlas.AtlasRegion background2 = game.renderer().atlas().findRegion("background2");
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        spriteBatch.begin();

        spriteBatch.draw(background2,
                         (Gdx.graphics.getWidth() - background2.originalWidth) / 2f,
                         (Gdx.graphics.getHeight() - background2.originalHeight) / 2f);

        Assets.renderMediumFont(game.renderer().hudRenderingLayer(),
                                "Your character name:",
                                Vector2.of(centerX - 120f, centerY + 100f),
                                Color.BLACK);

        Assets.renderMediumFont(game.renderer().hudRenderingLayer(),
                                currentMessage,
                                Vector2.of(centerX - 120f, centerY + 70f),
                                Color.BLACK);

        spriteBatch.end();
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
