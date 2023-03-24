package com.mygdx.game.model.action.skillmenu;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SkillPickerMenuActivateAction implements GameStateAction {
    CreatureId playerId;

    Integer slotNum;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return gameState.creatures().get(playerId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        PlayerParams playerParams = game.getPlayerParams(playerId);

        playerParams.skillMenuPickerSlotBeingChanged(slotNum);
    }
}
