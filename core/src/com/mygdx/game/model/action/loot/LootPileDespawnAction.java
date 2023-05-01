package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.CoreGame;
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
