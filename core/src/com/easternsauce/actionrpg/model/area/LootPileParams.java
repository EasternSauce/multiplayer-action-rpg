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
    private LootPileId id;

    private AreaId areaId;

    private Vector2 pos;
    private Set<Item> items = new ConcurrentSkipListSet<>();

    private Boolean isFullyLooted = false;
}
