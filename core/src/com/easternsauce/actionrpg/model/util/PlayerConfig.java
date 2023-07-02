package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.skill.SkillType;
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
    Integer potionMenuItemBeingMoved;
    Set<LootPileId> itemPickupMenuLootPiles = new ConcurrentSkipListSet<>();
    Integer isSkillMenuPickerSlotBeingChanged;
    Map<Integer, SkillType> skillMenuSlots = new ConcurrentSkipListMap<>();
    Boolean areAreasLoaded = false;
}
