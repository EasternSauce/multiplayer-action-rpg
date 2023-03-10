package com.mygdx.game.model.action;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class RemoveAbilityAction implements GameStateAction {
    AbilityId abilityId;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return gameState.abilities().get(abilityId).params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        game.abilitiesToBeRemoved().add(abilityId);

    }
}
