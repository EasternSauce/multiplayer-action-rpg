package com.mygdx.game.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Chat {
    List<ChatMessage> messages = new LinkedList<>();

    Boolean isTyping = false;
    String currentMessage = "";
}
