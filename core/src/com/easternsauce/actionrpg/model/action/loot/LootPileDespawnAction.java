package com.easternsauce.actionrpg.model.action.loot;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.util.Vector2;
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
    public Vector2 actionObjectPos(CoreGame game) {
        if (!game.getGameState().getLootPiles().containsKey(lootPileId)) {
            return Vector2.of(0f, 0f);
        }
        return game.getGameState().getLootPiles().get(lootPileId).getPos();
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
