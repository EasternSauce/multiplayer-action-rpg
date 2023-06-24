package com.easternsauce.actionrpg.game.chat;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Chat {
    List<ChatMessage> messages = new LinkedList<>();

    Boolean isTyping = false;
    String currentMessage = "";

    Float holdBackspaceTime;
    Boolean isHoldingBackspace = false;

    public void sendMessage(
        String posterId,
        String message,
        CoreGame game
    ) {
        if (getMessages().size() < 6) {
            getMessages().add(ChatMessage.of(
                game.getGameState().getTime(),
                posterId,
                message
            ));
        } else {
            List<ChatMessage> newMessages = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                newMessages.add(getMessages().get(i + 1));
            }
            setMessages(newMessages);
            getMessages().add(ChatMessage.of(
                game.getGameState().getTime(),
                posterId,
                message
            ));
        }
    }

    public void render(RenderingLayer renderingLayer) {
        for (int i = 0; i < Math.min(
            getMessages().size(),
            6
        ); i++) {
            Assets.renderSmallFont(
                renderingLayer,
                getMessages().get(i).getPoster() + ": " + getMessages().get(i).getText(),
                Vector2.of(
                    30,
                    220 - 20 * i
                ),
                Color.PURPLE
            );
        }

        Assets.renderSmallFont(
            renderingLayer,
            (getIsTyping() ? "> " : "") + getCurrentMessage(),
            Vector2.of(
                30,
                70
            ),
            Color.PURPLE
        );
    }
}
