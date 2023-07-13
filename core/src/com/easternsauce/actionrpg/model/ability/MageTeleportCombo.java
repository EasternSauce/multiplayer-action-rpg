package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class MageTeleportCombo extends Ability {
    AbilityParams params;

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
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());
        creature.applyEffect(CreatureEffect.SELF_STUN, 3.5f, game);
        creature.stopMoving();

        game.getGameState().accessAbilities().chainAnotherAbility(
            this,
            AbilityType.MOB_TELEPORT_SOURCE,
            getParams().getPos(),
            getParams().getDirVector(),
            null,
            null,
            null,
            game
        );
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        float[] fireRingProcTimes = {0.25f, 1.25f, 2f, 2.6f, 3.2f};
        float[] fireRingSizes = {6f, 8f, 15f, 20f, 27f};

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null &&
            currentFireRingToProc < fireRingProcTimes.length &&
            getParams().getStateTimer().getTime() > fireRingProcTimes[currentFireRingToProc]) {
            game.getGameState().accessAbilities().chainAnotherAbility(
                this,
                AbilityType.RING_OF_FIRE,
                null,
                getParams().getDirVector(),
                fireRingSizes[currentFireRingToProc],
                null,
                40f,
                game
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
