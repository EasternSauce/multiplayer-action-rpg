package com.easternsauce.actionrpg.game.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class ChatMessage implements Comparable<ChatMessage> {
    Float time;
    String poster;
    String text;

    @Override
    public int compareTo(ChatMessage other) {
        if (this.getTime() <= other.getTime()) {
            return -1;
        } else {
            return 1;
        }
    }
}
