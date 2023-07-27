package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MobVolatileBubble extends Projectile {
    @Getter
    private AbilityParams params;

    public static MobVolatileBubble of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        MobVolatileBubble ability = MobVolatileBubble.of();
        ability.params = abilityParams
            .setWidth(2f)
            .setHeight(2f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setStartingRange(1.5f)
            .setTextureName("bubble")
            .setBaseDamage(0f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setSpeed(18f)
            .setMaximumRange(17f);

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
    public void onStarted(CoreGame game) {
        Creature creature = game.getCreature(getParams().getCreatureId());
        creature.applyEffect(CreatureEffect.SELF_STUN, 0.1f, game);
        creature.stopMoving();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        onProjectileTravelUpdate();
    }

    @Override
    protected void onCompleted(CoreGame game) {
        float baseAngle = getParams().getDirVector().angleDeg();

        float[] angles = {0f, 22.5f, 45f, 67.5f, 90f, 112.5f, 135f, 157.5f, 180f, 202.5f, 225f, 247.5f, 270f, 292.5f, 315f, 337.5f};

        for (float angle : angles) {
            game.chainAnotherAbility(
                this,
                AbilityType.ICE_SPEAR,
                params.getDirVector().withSetDegAngle(baseAngle + angle),
                ChainAbilityParams.of().setChainToPos(getParams().getPos())
            );

        }
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
}
