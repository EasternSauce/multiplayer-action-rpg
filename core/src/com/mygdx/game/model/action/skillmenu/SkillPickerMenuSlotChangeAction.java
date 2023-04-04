package com.mygdx.game.model.action.skillmenu;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SkillPickerMenuSlotChangeAction implements GameStateAction {
    CreatureId playerId;

    SkillType skillType;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        if (!gameState.creatures().containsKey(playerId)) {
            return Vector2.of(0f, 0f);
        }
        return gameState.creatures().get(playerId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        PlayerParams playerParams = game.getPlayerParams(playerId);

        playerParams.skillMenuSlots().put(playerParams.skillMenuPickerSlotBeingChanged(), skillType);
        playerParams.skillMenuPickerSlotBeingChanged(null);
    }
}