package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MeteorAim extends Ability {
    @Getter
    protected AbilityParams params;

    public static MeteorAim of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getCreature(abilityParams.getCreatureId());

        MeteorAim ability = MeteorAim.of();
        ability.params = abilityParams.setWidth(3f).setHeight(3f).setChannelTime(0f).setActiveTime(0.8f).setTextureName(
            "meteor_aim").setBaseDamage(0f).setChannelAnimationLooping(false).setActiveAnimationLooping(false).setPos(
            creature.getParams().getPos());

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

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean canBeDeactivated() {
        return true;
    }

    @Override
    public boolean canStun() {
        return false;
    }
}
