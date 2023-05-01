package com.mygdx.game.chat;

import com.mygdx.game.game.CoreGame;
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

    public void sendMessage(String posterId, String message, CoreGame game) {
        if (getMessages().size() < 6) {
            getMessages().add(ChatMessage.of(game.getGameState().getTime(), posterId, message));
        } else {
            List<ChatMessage> newMessages = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                newMessages.add(getMessages().get(i + 1));
            }
            setMessages(newMessages);
            getMessages().add(ChatMessage.of(game.getGameState().getTime(), posterId, message));
        }
    }
}
