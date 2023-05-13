package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureHitsLootPileEvent implements PhysicsEvent {
    CreatureId creatureId;
    LootPileId lootPileId;
}