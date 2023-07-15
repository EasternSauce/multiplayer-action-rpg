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
public class SkillPickerMenuDeactivateAction extends GameStateAction {
    private CreatureId playerId;

    public static SkillPickerMenuDeactivateAction of(CreatureId playerId) {
        SkillPickerMenuDeactivateAction action = SkillPickerMenuDeactivateAction.of();
        action.playerId = playerId;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        playerConfig.setIsSkillMenuPickerSlotBeingChanged(null);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getCreature(playerId);
    }
}
