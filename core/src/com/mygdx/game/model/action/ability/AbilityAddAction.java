package com.mygdx.game.model.action.ability;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class AbilityAddAction extends GameStateAction {

    Ability ability;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return ability.getParams().getPos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {

        Creature creature = game.getCreature(ability.getParams().getCreatureId());

        if (creature == null) {
            return;
        }

        game.getAbilities()
            .put(ability.getParams().getId(), ability); // TODO: SHOULDNT THIS HAPPEN IN createAbility() METHOD???!?

        if (ability.getParams().getActiveTime() > 0) {
            game.getEventProcessor().getAbilityModelsToBeCreated().add(ability.getParams().getId());
        }

        ability.init(game);

        creature.onAbilityPerformed(ability);
    }
}
