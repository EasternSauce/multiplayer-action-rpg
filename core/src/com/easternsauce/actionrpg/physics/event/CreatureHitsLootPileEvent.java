package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CreatureHitsLootPileEvent implements PhysicsEvent {
    @Getter
    private CreatureId creatureId;
    @Getter
    private LootPileId lootPileId;
}
