package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class LootPileSpawnAction implements GameStateAction {
    AreaId areaId;

    Vector2 pos;
    Set<Item> items;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return pos;
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        LootPileId lootPileId = LootPileId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

        Set<Item>
                lootPileItems =
                this.items()
                    .stream()
                    .map(item -> Item.of(item.template(),
                                         item.quantity(),
                                         item.qualityModifier(),
                                         lootPileId))
                    .collect(Collectors.toCollection(ConcurrentSkipListSet::new));

        LootPile lootPile = LootPile.of(lootPileId, areaId, pos, lootPileItems);

        game.getLootPiles().put(lootPile.id(), lootPile);

        game.getLootPileModelsToBeCreated().add(lootPile.id());
    }
}
