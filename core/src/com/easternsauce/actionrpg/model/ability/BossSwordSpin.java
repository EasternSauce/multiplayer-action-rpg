package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class BossSwordSpin extends DirectionalAttachedAbility {
    AbilityParams params;

    public static BossSwordSpin of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        BossSwordSpin ability = BossSwordSpin.of();
        ability.params = abilityParams
            .setWidth(4f)
            .setHeight(4f)
            .setChannelTime(0f)
            .setActiveTime(3f)
            .setRange(4f)
            .setTextureName("sword")
            .setBaseDamage(42f)
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
    protected void onChannelUpdate(CoreGame game) {
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
        return 0.35f;
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
