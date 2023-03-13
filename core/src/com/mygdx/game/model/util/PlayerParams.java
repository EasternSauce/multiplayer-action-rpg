package com.mygdx.game.model.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PlayerParams {
    Boolean isVisible = false;
    Integer inventoryItemBeingMoved;
    Integer equipmentItemBeingMoved;
}
