package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.LootPileId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileDespawnAction extends GameStateAction {
    private LootPileId lootPileId;

    public static LootPileDespawnAction of(LootPileId lootPileId) {
        LootPileDespawnAction action = LootPileDespawnAction.of();
        action.lootPileId = lootPileId;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        game.getEventProcessor().getLootPileModelsToBeRemoved().add(lootPileId);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().getLootPiles().get(lootPileId);
    }
}
