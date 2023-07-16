package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SkillPickerMenuSlotChangeAction extends GameStateAction {
    private CreatureId playerId;

    private SkillType skillType;

    public static SkillPickerMenuSlotChangeAction of(CreatureId playerId, SkillType skillType) {
        SkillPickerMenuSlotChangeAction action = SkillPickerMenuSlotChangeAction.of();
        action.playerId = playerId;
        action.skillType = skillType;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        playerConfig.getSkillMenuSlots().put(playerConfig.getSkillMenuPickerSlotBeingChanged(), skillType);
        playerConfig.setSkillMenuPickerSlotBeingChanged(null);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
