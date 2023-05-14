package com.easternsauce.actionrpg.model.action.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class AbilityActivateAction extends GameStateAction {
    private Ability ability;

    @Override
    public boolean isActionObjectValid(CoreGame game) {
        return true;
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return ability;
    }

    @Override
    public void applyToGame(CoreGame game) {
        game.getEventProcessor().getAbilityModelsToBeActivated().add(ability.getParams().getId());
    }

    public static AbilityActivateAction of(Ability ability) {
        AbilityActivateAction action = AbilityActivateAction.of();
        action.ability = ability;
        return action;
    }
}
