package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
public class LootPile implements Entity {
    LootPileParams params;

    Float width = 1f;
    Float height = 1f;

    public static LootPile of(LootPileId id, AreaId areaId, Vector2 pos, Set<Item> items) {
        LootPileParams params = LootPileParams.of();
        params.setId(id);
        params.setAreaId(areaId);
        params.setPos(pos);
        params.setItems(items);

        return LootPile.of(params);
    }

    public static LootPile of(LootPileParams params) {
        LootPile lootPile = LootPile.of();
        lootPile.params = params;
        return lootPile;
    }
}
