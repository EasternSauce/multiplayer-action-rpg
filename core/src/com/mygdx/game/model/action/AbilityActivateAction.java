package com.mygdx.game.model.action;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityActivateAction implements GameStateAction {
    Ability ability;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return ability.params().pos();
    }

    @Override
    public void applyToGame(MyGdxGame game) {
        game.abilitiesToBeActivated().add(ability.params().id());
    }
}
