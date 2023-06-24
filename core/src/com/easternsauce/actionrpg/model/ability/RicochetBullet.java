package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class RicochetBullet extends Projectile {
    AbilityParams params;

    public static RicochetBullet of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        RicochetBullet ability = RicochetBullet.of();
        ability.params = abilityParams
                .setWidth(0.8f)
                .setHeight(0.8f)
                .setChannelTime(0f)
                .setActiveTime(10f)
                .setTextureName("fireball")
                .setBaseDamage(30f)
                .setIsChannelAnimationLooping(false)
                .setIsActiveAnimationLooping(true)
                .setDelayedActionTime(0.001f)
                .setSpeed(25f);

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
        onProjectileTravelUpdate();
    }


    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        params.setDamageMultiplier(params.getDamageMultiplier() * 3 / 5f);
    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

        if (getParams().getDirVector().normalized().dot(abilityPos.vectorTowards(tilePos).normalized()) <
                0.6f) { // check if it is facing the tile
            return;
        }

        if (getParams().getWallBounceCount() > 4) {
            deactivate();
            return;
        }

        getParams().getCreaturesAlreadyHit().clear();

        getParams().setWallBounceCount(getParams().getWallBounceCount() + 1);

        Vector2 collisionVector = abilityPos.vectorTowards(tilePos);

        float collisionAngle = collisionVector.angleDeg();

        float angle = params.getDirVector().multiplyBy(-1).angleDeg();

        float reflectAngle = 0f;
        if (collisionAngle >= 45f && collisionAngle < 135f) {
            if (angle < 90f) {
                reflectAngle = 90f + Math.abs(angle - 90f);
            } else {
                reflectAngle = 90f - Math.abs(angle - 90f);
            }
        }
        if (collisionAngle >= 135f && collisionAngle < 225f) {
            if (angle < 180f) {
                reflectAngle = 180f + Math.abs(angle - 180f);
            } else {
                reflectAngle = 180f - Math.abs(angle - 180f);
            }
        }
        if (collisionAngle >= 225f && collisionAngle < 315f) {
            if (angle < 270f) {
                reflectAngle = 270f + Math.abs(angle - 270f);
            } else {
                reflectAngle = 270f - Math.abs(angle - 270f);
            }
        }
        if ((collisionAngle >= 315f && collisionAngle < 360f) || (collisionAngle >= 0f && collisionAngle < 45f)) {
            if (angle >= 315f && angle < 360f) {
                reflectAngle = Math.abs(angle - 360f);
            } else {
                reflectAngle = 360f - Math.abs(angle - 360f);
            }
        }

        params.setDirVector(params.getDirVector().withSetDegAngle(reflectAngle));

    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }
}
