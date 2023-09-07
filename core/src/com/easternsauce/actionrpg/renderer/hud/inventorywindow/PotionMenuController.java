package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.action.PotionMenuItemUseAction;
import com.esotericsoftware.kryonet.Client;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class PotionMenuController {
  public void performUseItemClick(Client client, CoreGame game) {
    float x = game.hudMousePos().getX();
    float y = game.hudMousePos().getY();

    Integer potionMenuSlotClicked = PotionMenuConsts.getPotionMenuClicked(x, y);

    if (potionMenuSlotClicked != null) {
      sendUseItemAction(potionMenuSlotClicked, client, game);
    }
  }

  public void sendUseItemAction(Integer potionMenuSlotClicked, Client client, CoreGame game) {
    GameStateAction action = PotionMenuItemUseAction.of(game.getGameState().getThisClientPlayerId(),
      potionMenuSlotClicked);

    client.sendTCP(ActionPerformCommand.of(action));
  }
}
