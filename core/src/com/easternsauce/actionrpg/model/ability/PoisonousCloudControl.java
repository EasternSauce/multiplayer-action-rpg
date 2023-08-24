package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PoisonousCloudControl extends Ability {
    @Getter
    protected AbilityParams params;
    int currentCloudSet = 0;

    public static PoisonousCloudControl of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        PoisonousCloudControl ability = PoisonousCloudControl.of();
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
        float[] cloudSetSpawnTimes = {0f, 0.4f, 0.8f, 1.6f, 2.4f};

        float[] cloudSetSpawnCounts = {5, 7, 9, 11, 13};

        float[] cloudSetDistances = {1f, 2f, 3f, 4f, 5f};

        float totalDuration = 3.2f;

        Creature creature = game.getCreature(getParams().getCreatureId());

        if (creature != null &&
            currentCloudSet < cloudSetSpawnTimes.length &&
            getParams().getStateTimer().getTime() > cloudSetSpawnTimes[currentCloudSet]) {

            for (int i = 0; i < cloudSetSpawnCounts[currentCloudSet]; i++) {
                float x = getParams().getPos().getX() + (float) Math.sin(2 * Math.PI /
                    cloudSetSpawnCounts[currentCloudSet] * i) * cloudSetDistances[currentCloudSet];
                float y = getParams().getPos().getY() + (float) Math.cos(2 * Math.PI /
                    cloudSetSpawnCounts[currentCloudSet] * i) * cloudSetDistances[currentCloudSet];

                game.chainAnotherAbility(
                    this,
                    AbilityType.POISONOUS_CLOUD,
                    getParams().getDirVector(),
                    ChainAbilityParams
                        .of()
                        .setChainToPos(Vector2.of(x, y))
                        .setOverrideDuration(totalDuration - cloudSetSpawnTimes[currentCloudSet])
                );
            }

            currentCloudSet += 1;
        }

        if (currentCloudSet >= cloudSetSpawnTimes.length) {
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
