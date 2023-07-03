package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.BiFunction;
import java.util.function.Function;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class InventoryWindowItemMoveStrategy {
    private Function<InventoryWindowState, Boolean> applicableCondition;
    private BiFunction<InventoryWindowState, CoreGame, GameStateAction> action;

    public boolean isApplicable(InventoryWindowState inventoryWindowState) {
        return applicableCondition.apply(inventoryWindowState);
    }

    public GameStateAction apply(InventoryWindowState inventoryWindowState, CoreGame game) {
        return action.apply(inventoryWindowState, game);
    }

}
