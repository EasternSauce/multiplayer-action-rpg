package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
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
    public void updatePosition(CoreGame game) {

    }

    @Override
    public void onAbilityStarted(CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {

    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {

    }

    @Override
    public void onChannelUpdate(CoreGame game) {

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

        float[] cloudRadiuses = {
            1f,
            2f,
            3f,
            4f,
            5f,
            6f,
            7f,
            8f,
            9f,
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
            3f};

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
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {

    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    @Override
    public boolean usesEntityModel() {
        return false;
    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }
}
