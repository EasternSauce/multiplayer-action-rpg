package com.easternsauce.actionrpg.game.chat;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class Chat {
  @Getter
  private List<ChatMessage> messages = new LinkedList<>();

  @Getter
  @Setter
  private Boolean typing = false;
  @Getter
  @Setter
  private String currentMessage = "";

  @Getter
  @Setter
  private Float holdBackspaceTime;
  @Getter
  @Setter
  private Boolean holdingBackspace = false;

  public void sendMessage(String posterId, String message, CoreGame game) {
    if (getMessages().size() < 6) {
      getMessages().add(ChatMessage.of(message, game.getGameState().getTime(), posterId));
    } else {
      List<ChatMessage> newMessages = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        newMessages.add(getMessages().get(i + 1));
      }
      messages = newMessages;
      getMessages().add(ChatMessage.of(message, game.getGameState().getTime(), posterId));
    }
  }

  public void render(RenderingLayer renderingLayer) {
    for (int i = 0; i < Math.min(getMessages().size(), 6); i++) {
      Assets.renderSmallFont(renderingLayer, getMessages().get(i).getPoster() + ": " + getMessages().get(i).getText(),
        Vector2.of(30, 220 - 20 * i), Color.PURPLE);
    }

    Assets.renderSmallFont(renderingLayer, (getTyping() ? "> " : "") + getCurrentMessage(), Vector2.of(30, 70),
      Color.PURPLE);
  }
}
