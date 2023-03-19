package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class LootPileSpawnAction implements GameStateAction {
    LootPile lootPile;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return lootPile.pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        System.out.println("putting loot pile" + lootPile);
        game.getLootPiles().put(lootPile.id(), lootPile);

        game.getLootPilesToBeCreated().add(lootPile.id());
    }
}
