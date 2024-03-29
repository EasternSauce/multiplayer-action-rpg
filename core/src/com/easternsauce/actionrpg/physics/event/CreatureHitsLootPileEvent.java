package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CreatureHitsLootPileEvent implements PhysicsEvent {
  @Getter
  private EntityId<Creature> creatureId = NullCreatureId.of();
  @Getter
  private EntityId<LootPile> lootPileId;
}
