package com.mygdx.game.model.action.skillmenu;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
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
    private Boolean isServerSideOnly = false;
    private CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, playerId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        PlayerParams playerParams = game.getGameState().getPlayerParams(playerId);

        playerParams.setIsSkillMenuPickerSlotBeingChanged(null);
    }

    public static SkillPickerMenuDeactivateAction of(CreatureId playerId) {
        SkillPickerMenuDeactivateAction action = SkillPickerMenuDeactivateAction.of();
        action.playerId = playerId;
        return action;
    }
}
