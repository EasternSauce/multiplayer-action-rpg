package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class DigTunnelSplash extends Ability {
    @Getter
    private AbilityParams params;

    public static DigTunnelSplash of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        DigTunnelSplash ability = DigTunnelSplash.of();
        ability.params = abilityParams
            .setWidth(2.5f)
            .setHeight(2.5f)
            .setChannelTime(0f)
            .setActiveTime(0.5f)
            .setTextureName("dig")
            .setBaseDamage(0f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false)
            .setDelayedActionTime(0.3f);

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
        Creature creature = game.getCreature(getParams().getCreatureId());
        creature.applyEffect(CreatureEffect.SELF_STUN, 0.3f, game);
        creature.applyEffect(CreatureEffect.INVISIBILITY, 0.3f, game);
        creature.applyEffect(CreatureEffect.NO_COLLIDE, 0.3f, game);
        game.addTeleportEvent(TeleportEvent.of(
            getParams().getCreatureId(),
            getParams().getPos(),
            getParams().getAreaId(),
            getParams().getAreaId(),
            false
        ));
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
