package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class SkillPickerMenuActivateAction extends GameStateAction {
    private CreatureId playerId;

    private Integer slotNum;

    public static SkillPickerMenuActivateAction of(CreatureId playerId, Integer slotNum) {
        SkillPickerMenuActivateAction action = SkillPickerMenuActivateAction.of();
        action.playerId = playerId;
        action.slotNum = slotNum;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        playerConfig.setIsSkillMenuPickerSlotBeingChanged(slotNum);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }
}
