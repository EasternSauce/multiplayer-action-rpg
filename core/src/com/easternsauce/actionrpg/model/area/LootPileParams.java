package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class LootPileParams implements EntityParams {

    LootPileId id;

    AreaId areaId;

    Vector2 pos;
    Set<Item> items = new ConcurrentSkipListSet<>();

    Boolean isFullyLooted = false;
}
