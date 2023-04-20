package com.mygdx.game.game.interface_;

import com.mygdx.game.chat.Chat;
import com.mygdx.game.game.entity.GameEntityManager;
import com.mygdx.game.model.util.Vector2;

public interface GameRenderable extends GameUpdatable, CurrentPlayerRetrievable {
    GameEntityManager getEntityManager();

    void renderB2BodyDebug();

    Chat getChat();

    Vector2 hudMousePos();

    Vector2 mousePosRelativeToCenter();

}
