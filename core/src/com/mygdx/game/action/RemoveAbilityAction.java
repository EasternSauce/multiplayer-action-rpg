package com.mygdx.game.action;

import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.util.Vector2;
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
    public void applyToGame(MyGdxGame game) {
        synchronized (game.abilitiesToBeRemoved()) {
            game.abilitiesToBeRemoved().add(abilityId);
        }
    }
}
