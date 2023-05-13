package com.easternsauce.actionrpg.model.action.ability;

import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.util.Vector2;
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
        if (!game.getGameState().accessAbilities().getAbilities().containsKey(abilityId)) {
            return Vector2.of(0f, 0f);
        }
        return game.getGameState().accessAbilities().getAbilities().get(abilityId).getParams().getPos();
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
