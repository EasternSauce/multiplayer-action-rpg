package com.easternsauce.actionrpg.model.action.skillmenu;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class SkillPickerMenuSlotChangeAction extends GameStateAction {
    private CreatureId playerId;

    private SkillType skillType;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(playerId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        playerConfig.getSkillMenuSlots().put(playerConfig.getIsSkillMenuPickerSlotBeingChanged(), skillType);
        playerConfig.setIsSkillMenuPickerSlotBeingChanged(null);
    }

    public static SkillPickerMenuSlotChangeAction of(CreatureId playerId, SkillType skillType) {
        SkillPickerMenuSlotChangeAction action = SkillPickerMenuSlotChangeAction.of();
        action.playerId = playerId;
        action.skillType = skillType;
        return action;
    }
}
