package com.mygdx.game.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SendChatMessageCommand implements GameCommand {
    String poster;
    String text;
}

