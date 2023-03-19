package com.mygdx.game.model.util;

import com.mygdx.game.model.area.LootPileId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerParams {
    Boolean isInventoryVisible = false;
    Integer inventoryItemBeingMoved;
    Integer equipmentItemBeingMoved;
    Set<LootPileId> itemPickupMenuLootPiles = new ConcurrentSkipListSet<>();
}
