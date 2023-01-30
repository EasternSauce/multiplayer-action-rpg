package com.mygdx.game.action;

import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityFactory;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AddAbilityAction implements GameStateAction {
    AbilityId abilityId;
    CreatureId creatureId;

    Vector2 pos;
    Vector2 dirVector;

    String abilityType;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return pos;
    }

    @Override
    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature creature = gameState.creatures().get(creatureId);

        if (creature == null) return;

        creature.params().attackCooldownTimer().restart();

        Ability ability =
                AbilityFactory.produceAbility(abilityType, abilityId, creature.params().areaId(), creatureId, gameState,
                        dirVector);
        if (pos != null) ability.params().pos(pos);

        if (creature.params().isMoving()) { // TODO: should this logic happen as part of this action? or elsewhere?
            Vector2 movementVector =
                    creature.params().pos()
                            .vectorTowards(creature.params().movementCommandTargetPos()).normalized()
                            .multiplyBy(0.15f);
            // move slightly forward if attacking while moving
            if (!ability.params().attackWithoutMoving())
                creature.params().movementCommandTargetPos(creature.params().pos().add(movementVector));
        }

        gameState.abilities().put(abilityId, ability);


        synchronized (game.abilitiesToBeCreated()) {
            game.abilitiesToBeCreated().add(abilityId);
        }

    }
}
