package com.easternsauce.actionrpg.model.ability.fireball;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Fireball extends Projectile {
    @Getter
    protected AbilityParams params;

    public static Fireball of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getCreature(abilityParams.getCreatureId());

        Fireball ability = Fireball.of();
        ability.params = abilityParams
            .setWidth(2.5f)
            .setHeight(2.5f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setStartingRange(2.5f)
            .setTextureName("fireball")
            .setBaseDamage(20f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setPos(creature.getParams().getPos());

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {
        onProjectileTravelUpdate();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        //projectile speeds up over time
        onProjectileTravelUpdate();

        if (getParams().getStateTimer().getTime() < 1.5f) {
            getParams().setSpeed(10f + (getParams().getStateTimer().getTime() / 1.5f) * 70f);
        } else {
            getParams().setSpeed(80f);
        }
    }

    @Override
    protected void onCompleted(CoreGame game) {
        game.chainAnotherAbility(
            this,
            AbilityType.FIREBALL_EXPLOSION,
            params.getDirVector(),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        deactivate();
    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public Map<Integer, Float> levelScalings() {
        ConcurrentSkipListMap<Integer, Float> scalings = new ConcurrentSkipListMap<>();
        scalings.put(1, 1.0f);
        scalings.put(2, 1.1f);
        scalings.put(3, 1.2f);
        return scalings;
    }
}