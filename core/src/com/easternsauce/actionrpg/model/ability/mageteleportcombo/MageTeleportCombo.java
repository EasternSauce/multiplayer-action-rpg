package com.easternsauce.actionrpg.model.ability.mageteleportcombo;

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
public class MageTeleportCombo extends Ability {
    @Getter
    protected AbilityParams params;
    int currentFireRingToProc = 0;

    public static MageTeleportCombo of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        MageTeleportCombo ability = MageTeleportCombo.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(3.5f);

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
        creature.applyEffect(CreatureEffect.SELF_STUN, 3.5f, game);
        creature.stopMoving();

        game.chainAnotherAbility(
            this,
            AbilityType.ENEMY_TELEPORT_SOURCE,
            getParams().getDirVector(),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        float[] fireRingProcTimes = {0.25f, 1.25f, 2f, 2.6f, 3.2f};
        float[] fireRingScales = {1f, 1.2f, 1.5f, 2f, 3f};

        Creature creature = game.getCreature(getParams().getCreatureId());

        if (creature != null &&
            currentFireRingToProc < fireRingProcTimes.length &&
            getParams().getStateTimer().getTime() > fireRingProcTimes[currentFireRingToProc]) {
            game.chainAnotherAbility(
                this,
                AbilityType.RING_OF_FIRE,
                getParams().getDirVector(),
                ChainAbilityParams.of().setOverrideScale(fireRingScales[currentFireRingToProc]).setOverrideDamage(40f)
            );

            currentFireRingToProc += 1;
        }

        if (currentFireRingToProc >= fireRingProcTimes.length) {
            deactivate();
        }
    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }

    @Override
    public boolean usesEntityModel() {
        return false;
    }

    @Override
    public boolean isAbleToChainAfterCreatureDeath() {
        return false;
    }
}
