package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.Vector2;
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
    private Vector2 contactPoint;

    public static CreatureHitByAbilityAction of(CreatureId attackerId,
                                                CreatureId targetId,
                                                Ability ability,
                                                Vector2 contactPoint) {
        CreatureHitByAbilityAction action = CreatureHitByAbilityAction.of();
        action.attackerId = attackerId;
        action.targetId = targetId;
        action.ability = ability;
        action.contactPoint = contactPoint;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature targetCreature = game.getGameState().accessCreatures().getCreature(targetId);
        Creature attackerCreature = game.getGameState().accessCreatures().getCreature(attackerId);

        if (targetCreature == null || attackerCreature == null) {
            return;
        }

        Float damage = ability.getDamage(game);

        boolean isMeleeAbilityShielded = targetCreature.isMeleeAbilityShielded(ability, game);
        boolean isMarkedAsShielded = ability.getParams().getIsMarkedAsShielded();
        boolean isShielded = isMarkedAsShielded || isMeleeAbilityShielded;

        if (!(isShielded && targetCreature instanceof Player) && damage > 0f) {
            float realDamage;

            if (isShielded) {
                realDamage = damage / 4f;
            } else {
                realDamage = damage;
            }

            targetCreature.takeLifeDamage(realDamage, contactPoint, game);

            if (ability.isCanStun()) {
                targetCreature.applyEffect(CreatureEffect.STUN, ability.getStunDuration(), game);
            }

            targetCreature.onBeingHit(ability, game);
        }

        handleCreatureDeath(targetCreature, attackerCreature, game);
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(targetId);
    }

    @Override
    public boolean isActionObjectValid(CoreGame game) {
        return true;
    }
}
