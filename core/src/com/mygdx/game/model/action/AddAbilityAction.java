package com.mygdx.game.model.action;

import com.mygdx.game.game.MyGdxGame;
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
    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Creature creature = gameState.creatures().get(ability.params().creatureId());

        if (creature == null) {
            return;
        }

        gameState.abilities().put(ability.params().id(), ability);

        if (ability.params().activeTime() > 0) {
            game.abilitiesToBeCreated().add(ability.params().id());
        }

        ability.init(game);

        creature.onAbilityPerformed(ability);

    }
}
