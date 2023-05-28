package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class FireballExplosion extends Ability {
    AbilityParams params;

    public static FireballExplosion of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        FireballExplosion ability = FireballExplosion.of();
        ability.params = abilityParams
            .setWidth(9f)
            .setHeight(9f)
            .setChannelTime(0f)
            .setActiveTime(0.35f)
            .setTextureName("explosion")
            .setBaseDamage(25f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(false)
            .setAttackWithoutMoving(true)
            .setRotationShift(0f)
            .setCreaturesAlreadyHit(new ConcurrentSkipListMap<>());

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
    public Float getStunDuration() {
        return 0.8f;
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }
}
