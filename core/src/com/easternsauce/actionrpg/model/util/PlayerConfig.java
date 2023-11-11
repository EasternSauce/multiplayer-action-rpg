package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerConfig {
  private Boolean inventoryVisible = false;
  private Integer inventoryItemBeingMoved;
  private Integer equipmentItemBeingMoved;
  private Integer potionMenuItemBeingMoved;
  private Set<EntityId<LootPile>> itemPickupMenuLootPiles = new ConcurrentSkipListSet<>();
  private Set<EntityId<Checkpoint>> checkpointMenuCheckpoints = new ConcurrentSkipListSet<>();
  private Integer skillMenuPickerSlotBeingChanged;
  private Map<Integer, SkillType> skillMenuSlots = new OrderedMap<>();
  private Boolean areAreasLoaded = false;
}
