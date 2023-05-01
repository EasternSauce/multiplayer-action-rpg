package com.mygdx.game.model.action.ability;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class AbilityActivateAction extends GameStateAction {
    private Ability ability;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return ability.getParams().getPos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        game.getEventProcessor().getAbilitiesToBeActivated().add(ability.getParams().getId());
    }

    public static AbilityActivateAction of(Ability ability) {
        AbilityActivateAction action = AbilityActivateAction.of();
        action.ability = ability;
        return action;
    }
}
