package com.easternsauce.actionrpg.game.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class ChatMessageSendCommand implements GameCommand {
  @Getter
  String poster;
  @Getter
  String text;
}

