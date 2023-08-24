package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class BossSwordSpin extends AttachedAbility {
    @Getter
    protected AbilityParams params;

    public static BossSwordSpin of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        BossSwordSpin ability = BossSwordSpin.of();
        ability.params = abilityParams.setWidth(6f).setHeight(6f).setChannelTime(0f).setActiveTime(4f).setStartingRange(
            4f).setTextureName("sword").setBaseDamage(42f).setChannelAnimationLooping(false).setActiveAnimationLooping(
            false).setDirVector(abilityParams.getDirVector().withRotatedDegAngle(90));
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
        updateAttachedAbilityPosition(game);
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updateAttachedAbilityPosition(game);

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

        updateAttachedAbilityPosition(game);
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public Float getStunDuration() {
        return 0.3f;
    }

    @Override
    public boolean canBeDeactivated() {
        return true;
    }

    @Override
    public boolean isDamagingSkillNotAllowedWhenActive() {
        return true;
    }
}
