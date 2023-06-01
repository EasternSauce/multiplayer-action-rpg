package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.abstracts.Projectile;
import com.easternsauce.actionrpg.model.ability.util.AbilityParams;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrossbowBolt extends Projectile {
    AbilityParams params;

    public static CrossbowBolt of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        CrossbowBolt ability = CrossbowBolt.of();
        ability.params = abilityParams
            .setWidth(0.9f)
            .setHeight(0.9f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setTextureName("arrow")
            .setBaseDamage(10f)
            .setIsChannelAnimationLooping(true)
            .setIsActiveAnimationLooping(true)
            .setRotationShift(0f)
            .setSpeed(30f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
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
        return true;
    }

    @Override
    public Float getStunDuration() {
        return 0.15f;
    }
}
