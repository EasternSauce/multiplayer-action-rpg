package com.mygdx.game.action;

import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.game.MyGdxGame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class RemoveAbilityAction implements GameStateAction {
    AbilityId abilityId;

    @Override
    public void applyToGame(MyGdxGame game) {
        synchronized (game.abilitiesToBeRemoved()) {
            game.abilitiesToBeRemoved().add(abilityId);
        }
    }
}
