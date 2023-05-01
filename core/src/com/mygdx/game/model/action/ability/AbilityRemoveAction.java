package com.mygdx.game.model.action.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class AbilityRemoveAction extends GameStateAction {
    private AbilityId abilityId;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        if (!game.getGameState().getAbilities().containsKey(abilityId)) {
            return Vector2.of(0f, 0f);
        }
        return game.getGameState().getAbilities().get(abilityId).getParams().getPos();
    }

    @Override
    public void applyToGame(CoreGame game) {
        game.getEventProcessor().getAbilityModelsToBeRemoved().add(abilityId);

    }

    public static AbilityRemoveAction of(AbilityId abilityId) {
        AbilityRemoveAction action = AbilityRemoveAction.of();
        action.abilityId = abilityId;
        return action;
    }
}
