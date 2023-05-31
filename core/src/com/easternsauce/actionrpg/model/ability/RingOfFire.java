package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.util.Ability;
import com.easternsauce.actionrpg.model.ability.util.AbilityId;
import com.easternsauce.actionrpg.model.ability.util.AbilityParams;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class RingOfFire extends Ability {
    AbilityParams params;

    public static RingOfFire of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        RingOfFire ability = RingOfFire.of();
        ability.params = abilityParams
            .setWidth(8f)
            .setHeight(8f)
            .setChannelTime(0.4f)
            .setActiveTime(0.16f)
            .setBaseDamage(28f)
            .setTextureName("ring_of_fire")
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(false)
            .setAttackWithoutMoving(true)
            .setRotationShift(0f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    public void updatePosition(CoreGame game) {
        Vector2 pos = game.getGameState().accessCreatures().getCreaturePos(getParams().getCreatureId());

        if (pos != null) {
            getParams().setPos(pos.copy());
        }
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
    public Boolean isPositionChangedOnUpdate() {
        return true;
    }

    @Override
    public void onChannelUpdate(CoreGame game) {
        updatePosition(game);
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updatePosition(game);
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
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean isBlockable() {
        return false;
    }

    @Override
    public Float getStunDuration() {
        return 0.3f;
    }
}
