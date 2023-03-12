package com.mygdx.game.model.action;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AddAbilityAction implements GameStateAction {

    Ability ability;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return ability.params().pos();
    }

    @Override
    public void applyToGame(GameActionApplicable game) {

        Creature creature = game.getCreature(ability.params().creatureId());

        if (creature == null) {
            return;
        }

        game.getAbilities().put(ability.params().id(), ability);

        if (ability.params().activeTime() > 0) {
            game.abilitiesToBeCreated().add(ability.params().id());
        }

        ability.init(game);

        creature.onAbilityPerformed(ability);

    }
}
