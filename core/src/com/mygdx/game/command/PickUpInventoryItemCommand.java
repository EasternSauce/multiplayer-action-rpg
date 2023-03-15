package com.mygdx.game.command;

import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PickUpInventoryItemCommand implements GameCommand {
    CreatureId creatureId;

    Integer slotIndex;


}