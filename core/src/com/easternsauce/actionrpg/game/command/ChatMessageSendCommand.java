package com.easternsauce.actionrpg.game.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class ChatMessageSendCommand implements GameCommand {
    String poster;
    String text;
}

