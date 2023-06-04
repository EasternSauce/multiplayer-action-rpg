package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureHitByAbilityAction extends CreatureHitAction {
    private CreatureId attackerId;
    private CreatureId targetId;
    private Ability ability;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(targetId);
    }

    @Override
    public boolean isActionObjectValid(CoreGame game) {
        return true;
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature targetCreature = game.getGameState().accessCreatures().getCreature(targetId);
        Creature attackerCreature = game.getGameState().accessCreatures().getCreature(attackerId);

        if (targetCreature == null || attackerCreature == null) {
            return;
        }

        boolean isShielded = targetCreature.isAbilityShielded(ability, game);

        if (!isShielded && !ability.getParams().getIsHitShielded()) {
            targetCreature.takeLifeDamage(ability.getDamage(game));

            if (ability.isCanStun()) {
                targetCreature.applyEffect(CreatureEffect.STUN, ability.getStunDuration(), game);
            }
        }

        handleCreatureDeath(targetCreature, attackerCreature, game);
    }

    public static CreatureHitByAbilityAction of(CreatureId attackerId, CreatureId targetId, Ability ability) {
        CreatureHitByAbilityAction action = CreatureHitByAbilityAction.of();
        action.attackerId = attackerId;
        action.targetId = targetId;
        action.ability = ability;
        return action;
    }
}
