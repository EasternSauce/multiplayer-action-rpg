package com.easternsauce.actionrpg.model.util;

import com.easternsauce.actionrpg.model.id.CreatureId;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
public class CreaturesHitCounter {
  private final Map<CreatureId, Integer> creatureCounts = new ConcurrentSkipListMap<>();

  public void incrementForCreature(CreatureId creatureId) {
    if (!creatureCounts.containsKey(creatureId)) {
      creatureCounts.put(creatureId, 1);
    } else {
      creatureCounts.put(creatureId, creatureCounts.get(creatureId) + 1);
    }
  }

  public int getCount(CreatureId creatureId) {
    return creatureCounts.getOrDefault(creatureId, 0);

  }
}
