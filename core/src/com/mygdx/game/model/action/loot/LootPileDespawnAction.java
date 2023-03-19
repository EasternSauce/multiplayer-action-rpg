package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class LootPileDespawnAction implements GameStateAction {
    LootPileId lootPileId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        LootPile lootPile = gameState.lootPiles().get(lootPileId);
        return lootPile.pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        game.getLootPilesToBeRemoved().add(lootPileId);
    }
}
