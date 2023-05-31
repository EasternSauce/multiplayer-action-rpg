package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.util.AbilityParams;
import com.easternsauce.actionrpg.model.ability.util.AbilityType;
import com.easternsauce.actionrpg.model.ability.util.Projectile;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class VolatileBubble extends Projectile {

    AbilityParams params;

    public static VolatileBubble of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        VolatileBubble ability = VolatileBubble.of();
        ability.params = abilityParams
            .setWidth(3.2f)
            .setHeight(3.2f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setTextureName("bubble")
            .setBaseDamage(0f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(true)
            .setRotationShift(0f)
            .setDelayedActionTime(0.001f)
            .setSpeed(7f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onCompleted(CoreGame game) {
        float baseAngle = getParams().getDirVector().angleDeg();

        float[] angles = {
            0f,
            22.5f,
            45f,
            67.5f,
            90f,
            112.5f,
            135f,
            157.5f,
            180f,
            202.5f,
            225f,
            247.5f,
            270f,
            292.5f,
            315f,
            337.5f};

        for (float angle : angles) {
            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this,
                                     AbilityType.ICE_SPEAR,
                                     getParams().getPos(),
                                     params.getDirVector().withSetDegAngle(baseAngle + angle),
                                     null,
                                     null,
                                     game);

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

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        if (getParams().getSpeed() != null) {
            getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        }
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());

        if (getParams().getPos().distance(getParams().getSkillStartPos()) > 10f) {
            deactivate();
        }
    }
}
