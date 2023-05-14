package com.easternsauce.actionrpg.model.action.loot;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.area.LootPileId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileDespawnAction extends GameStateAction {
    private LootPileId lootPileId;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().getLootPiles().get(lootPileId);
    }

    @Override
    public void applyToGame(CoreGame game) {
        game.getEventProcessor().getLootPileModelsToBeRemoved().add(lootPileId);
    }

    public static LootPileDespawnAction of(LootPileId lootPileId) {
        LootPileDespawnAction action = LootPileDespawnAction.of();
        action.lootPileId = lootPileId;
        return action;
    }
}
