package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
public class LootPile {
    LootPileId id;

    AreaId areaId;

    Vector2 pos;
    Set<Item> items = new ConcurrentSkipListSet<>();

    Float width = 1f;
    Float height = 1f;

    Boolean isFullyLooted = false;

    public static LootPile of(LootPileId id, AreaId areaId, Vector2 pos, Set<Item> items) {
        LootPile lootPile = LootPile.of();
        lootPile.id = id;
        lootPile.areaId = areaId;
        lootPile.pos = pos;
        lootPile.items = items;

        return lootPile;
    }
}
