package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EquipmentItemPutOnCursorAction extends GameStateAction {
    private CreatureId playerId;

    private Integer slotIndex;

    public static EquipmentItemPutOnCursorAction of(CreatureId creatureId, Integer slotIndex) {
        EquipmentItemPutOnCursorAction action = EquipmentItemPutOnCursorAction.of();
        action.playerId = creatureId;
        action.slotIndex = slotIndex;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        if (playerConfig != null) {
            playerConfig.setEquipmentItemBeingMoved(slotIndex);
        }
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }
}
