package com.easternsauce.actionrpg.model.action.ability;

import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class AbilityTryAddAction extends GameStateAction {
    private Ability ability;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return ability.getParams().getPos();
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(ability.getParams().getCreatureId());

        if (creature == null) {
            return;
        }

        game
            .getGameState()
            .accessAbilities()
            .getAbilities()
            .put(ability.getParams().getId(), ability); // TODO: SHOULDNT THIS HAPPEN IN createAbility() METHOD???!?

        game.getEventProcessor().getAbilityModelsToBeCreated().add(ability.getParams().getId());

        ability.init(game);

        creature.onAbilityPerformed(ability);
    }

    public static AbilityTryAddAction of(Ability ability) {
        AbilityTryAddAction action = AbilityTryAddAction.of();
        action.ability = ability;
        return action;
    }
}
