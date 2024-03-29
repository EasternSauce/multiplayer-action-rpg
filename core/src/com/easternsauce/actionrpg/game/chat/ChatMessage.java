package com.easternsauce.actionrpg.game.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class ChatMessage implements Comparable<ChatMessage> {
  @Getter
  String text;
  @Getter
  private Float time;
  @Getter
  private String poster;

  @Override
  public int compareTo(ChatMessage other) {
    if (this.getTime() <= other.getTime()) {
      return -1;
    } else {
      return 1;
    }
  }
}
