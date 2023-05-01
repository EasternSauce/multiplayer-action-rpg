package com.mygdx.game.model.action.skillmenu;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
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
        PlayerParams playerParams = game.getGameState().getPlayerParams(playerId);

        playerParams.getSkillMenuSlots().put(playerParams.getIsSkillMenuPickerSlotBeingChanged(), skillType);
        playerParams.setIsSkillMenuPickerSlotBeingChanged(null);
    }

    public static SkillPickerMenuSlotChangeAction of(CreatureId playerId, SkillType skillType) {
        SkillPickerMenuSlotChangeAction action = SkillPickerMenuSlotChangeAction.of();
        action.playerId = playerId;
        action.skillType = skillType;
        return action;
    }
}
