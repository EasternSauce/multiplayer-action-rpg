package com.mygdx.game.model.action.ability;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class AbilityActivateAction extends GameStateAction {
    Ability ability;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return ability.getParams().getPos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        game.getEventProcessor().getAbilitiesToBeActivated().add(ability.getParams().getId());
    }
}
