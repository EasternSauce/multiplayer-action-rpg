package com.mygdx.game.chat;

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
        if (this.time() <= other.time()) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
