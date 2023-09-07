package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(staticName = "of")
public class LootPile implements Entity {
  @Getter
  private final Float width = 1f;
  @Getter
  private final Float height = 1f;
  @Getter
  private LootPileParams params;

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
