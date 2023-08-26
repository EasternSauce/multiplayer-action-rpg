package com.easternsauce.actionrpg.model.ability.teleport;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyTeleportSource extends Ability {
    @Getter
    protected AbilityParams params;

    public static EnemyTeleportSource of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getCreature(abilityParams.getCreatureId());

        EnemyTeleportSource ability = EnemyTeleportSource.of();
        ability.params = abilityParams
            .setWidth(4.5f)
            .setHeight(4.5f)
            .setChannelTime(0f)
            .setActiveTime(1f)
            .setTextureName("warp")
            .setBaseDamage(0f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false)
            .setPos(creature.getParams().getPos())
            .setDelayedActionTime(0.3f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {

    }

    @Override
    public void onStarted(CoreGame game) {
        Creature creature = game.getCreature(getParams().getCreatureId());
        creature.applyEffect(CreatureEffect.SELF_STUN, 0.5f, game);
        creature.stopMoving();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {
        game.chainAnotherAbility(
            this,
            AbilityType.ENEMY_TELEPORT_DESTINATION,
            getParams().getDirVector(),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean canBeDeactivated() {
        return true;
    }

    @Override
    public boolean canStun() {
        return false;
    }
}
