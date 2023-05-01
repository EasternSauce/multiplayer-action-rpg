package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileDespawnAction extends GameStateAction {
    private LootPileId lootPileId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.getLootPiles().containsKey(lootPileId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.getLootPiles().get(lootPileId).getPos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        game.getEventProcessor().getLootPileModelsToBeRemoved().add(lootPileId);
    }

    public static LootPileDespawnAction of(LootPileId lootPileId) {
        LootPileDespawnAction action = LootPileDespawnAction.of();
        action.lootPileId = lootPileId;
        return action;
    }
}
