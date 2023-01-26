package com.mygdx.game.action;

import com.mygdx.game.ability.Ability;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.ability.AbilityParams;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
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
    CreatureId playerId;
    Vector2 pos;

    String abilityType;

    @Override
    public void applyToGame(MyGdxGame game) {
        GameState gameState = game.gameState();

        Ability ability = Ability.of(AbilityParams.of(abilityId, gameState.defaultAreaId(), pos, 2f, 2f, abilityType));
        ability.start(Vector2.of(0, 0));

        synchronized (game.lock) {
            gameState.abilities().put(abilityId, ability);
        }

        synchronized (game.abilitiesToBeCreated()) {
            game.abilitiesToBeCreated().add(abilityId);
        }

    }
}
