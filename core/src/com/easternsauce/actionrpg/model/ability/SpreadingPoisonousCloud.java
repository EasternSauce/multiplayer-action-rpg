package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SpreadingPoisonousCloud extends Ability {
    AbilityParams params;

    int currentCloud = 0;

    public static SpreadingPoisonousCloud of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        SpreadingPoisonousCloud ability = SpreadingPoisonousCloud.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(10f);

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
    protected void onActiveUpdate(float delta, CoreGame game) {
        float[] cloudSpreadTimes = {
            0f,
            0.2f,
            0.4f,
            0.6f,
            0.8f,
            1.2f,
            1.6f,
            2.0f,
            2.4f,
            2.8f};

        //noinspection SpellCheckingInspection
        float[] cloudRadiuses = {
            1f,
            2f,
            3f,
            4f,
            5f,
            6f,
            8f,
            10f,
            10f,
            10f};

        float[] cloudDurations = {
            0.2f,
            0.2f,
            0.2f,
            0.2f,
            0.4f,
            0.4f,
            0.4f,
            0.4f,
            0.4f,
            0.4f};

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null && currentCloud < cloudSpreadTimes.length &&
            getParams().getStateTimer().getTime() > cloudSpreadTimes[currentCloud]) {

            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this,
                    AbilityType.POISONOUS_CLOUD,
                    getParams().getPos(),
                    getParams().getDirVector(),
                    cloudRadiuses[currentCloud],
                    cloudDurations[currentCloud],
                    game);

            currentCloud += 1;
        }

        if (currentCloud >= cloudSpreadTimes.length) {
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
}
