package com.mygdx.game.renderer.hud.itempickupmenu;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.command.ActionPerformCommand;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.loot.LootPileItemTryPickUpAction;
import com.mygdx.game.model.util.PlayerConfig;
import com.mygdx.game.renderer.util.Rect;
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
                Rect rect = Rect.of(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 5f - 40f,
                                    30f + 25f * i.get() - 17f,
                                    Gdx.graphics.getWidth() / 6f,
                                    20f);

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
