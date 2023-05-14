package com.easternsauce.actionrpg.model.action.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class AbilityRemoveAction extends GameStateAction {
    private AbilityId abilityId;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessAbilities().getAbility(abilityId);
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
