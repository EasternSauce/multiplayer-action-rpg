package com.mygdx.game.game.intrface;

import com.mygdx.game.chat.Chat;
import com.mygdx.game.renderer.GameRenderer;

public interface GameRenderable extends GameUpdatable {
    GameRenderer getRenderer();

    void renderB2BodyDebug();

    Chat getChat();
}
