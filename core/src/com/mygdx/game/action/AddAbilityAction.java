package com.mygdx.game.action;

import com.mygdx.game.ability.Ability;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.util.Vector2;
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
    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature creature = gameState.creatures().get(ability.params().creatureId());

        if (creature == null) {
            return;
        }

        if (ability.params().performableByCreature()) {
            creature.params().actionCooldownTimer().restart();
        }
        creature.params().abilityCooldowns().get(ability.type()).restart();

        if (creature.params().isMoving()) { // TODO: should this logic happen as part of this action? or elsewhere?
            Vector2 movementVector = creature.params()
                                             .pos()
                                             .vectorTowards(creature.params().movementCommandTargetPos())
                                             .normalized()
                                             .multiplyBy(0.15f);
            // move slightly forward if attacking while moving
            if (!ability.params().attackWithoutMoving()) {
                creature.params().movementCommandTargetPos(creature.params().pos().add(movementVector));
            }
        }

        gameState.abilities().put(ability.params().id(), ability);


        synchronized (game.abilitiesToBeCreated()) {
            game.abilitiesToBeCreated().add(ability.params().id());
        }

        ability.init(game);

    }
}
