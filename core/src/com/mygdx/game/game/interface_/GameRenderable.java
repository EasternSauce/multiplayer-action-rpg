package com.mygdx.game.game.interface_;

import com.mygdx.game.chat.Chat;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.GameRenderer;

public interface GameRenderable extends GameUpdatable {
    GameRenderer getRenderer();

    void renderB2BodyDebug();

    Chat getChat();

    PlayerParams getPlayerParams(CreatureId currentPlayerId);

    Vector2 hudMousePos();

    Vector2 mousePosRelativeToCenter();
}
