package com.mygdx.game.model.action.skillmenu;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class SkillPickerMenuDeactivateAction extends GameStateAction {
    private CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(playerId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerParams playerParams = game.getGameState().getPlayerParams(playerId);

        playerParams.setIsSkillMenuPickerSlotBeingChanged(null);
    }

    public static SkillPickerMenuDeactivateAction of(CreatureId playerId) {
        SkillPickerMenuDeactivateAction action = SkillPickerMenuDeactivateAction.of();
        action.playerId = playerId;
        return action;
    }
}
