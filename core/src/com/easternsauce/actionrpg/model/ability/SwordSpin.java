package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SwordSpin extends DirectionalAttachedAbility {
    AbilityParams params;

    public static SwordSpin of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        SwordSpin ability = SwordSpin.of();
        ability.params = abilityParams
                .setWidth(2.8f)
                .setHeight(2.8f)
                .setChannelTime(0f)
                .setActiveTime(3f)
                .setRange(2f)
                .setTextureName("sword")
                .setBaseDamage(10f)
                .setIsChannelAnimationLooping(false)
                .setIsActiveAnimationLooping(false)
                .setDirVector(abilityParams.getDirVector().withRotatedDegAngle(90));
        return ability;
    }

    @Override
    public Boolean isPositionChangedOnUpdate() {
        return true;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    public void onChannelUpdate(CoreGame game) {
        updateDirectionalAttachedAbilityPosition(game);
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updateDirectionalAttachedAbilityPosition(game);

        getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(-10));

        Set<CreatureId> creaturesHitRemove = new HashSet<>();

        getParams().getCreaturesAlreadyHit().forEach((creatureId, time) -> {
            if (time < getParams().getStateTimer().getTime() - 0.4f) {
                creaturesHitRemove.add(creatureId);
            }
        });

        creaturesHitRemove.forEach(creatureId -> getParams().getCreaturesAlreadyHit().remove(creatureId));

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());
        creature.applyEffect(CreatureEffect.SELF_SLOW, 0.1f, game);
        creature.getParams().getEffectParams().setCurrentSlowMagnitude(0.3f);
    }

    @Override
    public void init(CoreGame game) {
        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updateDirectionalAttachedAbilityPosition(game);

    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public Float getStunDuration() {
        return 0.05f;
    }

    @Override
    public boolean isCanBeDeactivated() {
        return true;
    }

    @Override
    public boolean isDamagingSkillAllowedDuring() {
        return false;
    }
}
