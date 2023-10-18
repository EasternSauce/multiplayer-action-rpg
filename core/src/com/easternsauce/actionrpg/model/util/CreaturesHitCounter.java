package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
public class CreaturesHitCounter {
  private final Map<EntityId<Creature>, Integer> creatureCounts = new ConcurrentSkipListMap<>();

  public void incrementForCreature(EntityId<Creature> creatureId) {
    if (!creatureCounts.containsKey(creatureId)) {
      creatureCounts.put(creatureId, 1);
    } else {
      creatureCounts.put(creatureId, creatureCounts.get(creatureId) + 1);
    }
  }

  public int getCount(EntityId<Creature> creatureId) {
    return creatureCounts.getOrDefault(creatureId, 0);

  }
}
