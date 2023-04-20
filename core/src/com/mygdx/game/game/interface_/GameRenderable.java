package com.mygdx.game.game.interface_;

import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.game.GameRenderer;

public interface GameRenderable extends GameUpdatable, CurrentPlayerRetrievable {
    GameRenderer getRenderer();

    void renderB2BodyDebug();

    Chat getChat();

    Vector2 hudMousePos();

    Vector2 mousePosRelativeToCenter();

}
