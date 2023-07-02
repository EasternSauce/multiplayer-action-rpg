package com.easternsauce.actionrpg.renderer.hud.potionmenu;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.model.action.PotionMenuItemPutOnCursorAction;
import com.esotericsoftware.kryonet.Client;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;

@NoArgsConstructor(staticName = "of")
@Data
public class PotionMenuController {
    public boolean performPotionMenuClick(Client client, CoreGame game) {
        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        PotionMenuConsts.slotRectangles.forEach((slotNum, rect) -> {
            if (rect.contains(x, y)) {
                client.sendTCP(ActionPerformCommand.of(PotionMenuItemPutOnCursorAction.of(game
                    .getGameState()
                    .getThisClientPlayerId(), slotNum)));
                isSuccessful.set(true);
            }
        });

        return isSuccessful.get();

    }
}
