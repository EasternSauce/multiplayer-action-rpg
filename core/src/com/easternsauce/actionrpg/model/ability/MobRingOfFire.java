package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MobRingOfFire extends Ability {
    @Getter
    private AbilityParams params;

    public static MobRingOfFire of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        MobRingOfFire ability = MobRingOfFire.of();

        ability.params = abilityParams
            .setWidth(20f)
            .setHeight(20f)
            .setChannelTime(0.4f)
            .setActiveTime(0.16f)
            .setBaseDamage(28f)
            .setTextureName("ring_of_fire")
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false)
            .setAttackWithoutMoving(true);

        return ability;
    }

    @Override
    public Boolean isPositionChangedOnUpdate() {
        return true;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {
        updatePosition(game);
    }

    public void updatePosition(CoreGame game) {
        Vector2 pos = game.getCreaturePos(getParams().getCreatureId());

        if (pos != null) {
            getParams().setPos(pos.copy());
        }
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updatePosition(game);
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
    public boolean isBlockable() {
        return false;
    }
}
