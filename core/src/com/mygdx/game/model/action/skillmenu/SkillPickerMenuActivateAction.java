package com.mygdx.game.model.action.skillmenu;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerConfig;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class SkillPickerMenuActivateAction extends GameStateAction {
    private CreatureId playerId;

    private Integer slotNum;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(playerId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        playerConfig.setIsSkillMenuPickerSlotBeingChanged(slotNum);
    }

    public static SkillPickerMenuActivateAction of(CreatureId playerId, Integer slotNum) {
        SkillPickerMenuActivateAction action = SkillPickerMenuActivateAction.of();
        action.playerId = playerId;
        action.slotNum = slotNum;
        return action;
    }
}
