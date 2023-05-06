package com.mygdx.game.model.action.loot;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class LootPileSpawnAction extends GameStateAction {
    private AreaId areaId;

    private Vector2 pos;
    private Set<Item> items;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return pos;
    }

    @Override
    public void applyToGame(CoreGame game) {
        LootPileId lootPileId = LootPileId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

        Set<Item> lootPileItems = this.getItems()
                                      .stream()
                                      .map(item -> Item.of()
                                                       .setTemplate(item.getTemplate())
                                                       .setQuantity(item.getQuantity())
                                                       .setQualityModifier(item.getQualityModifier())
                                                       .setLootPileId(lootPileId))
                                      .collect(Collectors.toCollection(ConcurrentSkipListSet::new));

        LootPile lootPile = LootPile.of(lootPileId, areaId, pos, lootPileItems);

        game.getGameState().getLootPiles().put(lootPile.getId(), lootPile);

        game.getEventProcessor().getLootPileModelsToBeCreated().add(lootPile.getId());
    }

    public static LootPileSpawnAction of(AreaId areaId, Vector2 pos, Set<Item> items) {
        LootPileSpawnAction action = LootPileSpawnAction.of();
        action.areaId = areaId;
        action.pos = pos;
        action.items = items;
        return action;
    }
}
