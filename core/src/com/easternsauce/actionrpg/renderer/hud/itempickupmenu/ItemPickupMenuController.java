package com.easternsauce.actionrpg.renderer.hud.itempickupmenu;

import com.easternsauce.actionrpg.command.ActionPerformCommand;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.loot.LootPileItemTryPickUpAction;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.renderer.util.Rect;
import com.esotericsoftware.kryonet.Client;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(staticName = "of")
@Data
public class ItemPickupMenuController {
    public boolean performItemPickupMenuClick(Client client, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        AtomicInteger i = new AtomicInteger();
        playerConfig
            .getItemPickupMenuLootPiles()
            .stream()
            .filter(lootPileId -> game.getGameState().getLootPiles().containsKey(lootPileId))
            .flatMap(lootPileId -> game.getGameState().getLootPile(lootPileId).getItems().stream())
            .forEach(item -> {
                Rect rect = ItemPickupMenuPositioning.getMenuOptionRect(i.get());

                if (rect.contains(x, y)) {
                    client.sendTCP(ActionPerformCommand.of(LootPileItemTryPickUpAction.of(game
                                                                                              .getGameState()
                                                                                              .getThisClientPlayerId(), item)));
                    isSuccessful.set(true);
                }

                i.getAndIncrement();
            });
        return isSuccessful.get();
    }
}
