package com.mygdx.game.physics.event;

import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureLeavesLootPileEvent implements PhysicsEvent {
    CreatureId creatureId;
    LootPileId lootPileId;
}
