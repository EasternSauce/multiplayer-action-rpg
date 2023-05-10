package com.mygdx.game.model.util;

import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.skill.SkillType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerConfig {
    Boolean isInventoryVisible = false;
    Integer inventoryItemBeingMoved;
    Integer equipmentItemBeingMoved;
    Set<LootPileId> itemPickupMenuLootPiles = new ConcurrentSkipListSet<>();
    Integer isSkillMenuPickerSlotBeingChanged;
    Map<Integer, SkillType> skillMenuSlots = new ConcurrentSkipListMap<>();
}
