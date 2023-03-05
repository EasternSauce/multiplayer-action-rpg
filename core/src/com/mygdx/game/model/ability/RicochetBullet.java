package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class RicochetBullet extends Projectile {
    AbilityParams params;

    public static RicochetBullet of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        RicochetBullet ability = RicochetBullet.of();
        ability.params =
                abilityParams
                        .width(0.8f)
                        .height(0.8f)
                        .channelTime(0f)
                        .activeTime(10f)
                        .textureName("fireball")
                        .baseDamage(12f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(true)
                        .rotationShift(0f)
                        .delayedActionTime(0.001f)
                        .speed(25f);


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {
        params.currentDamage(params().currentDamage() * 3 / 5f);
    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {

        if (params().wallBounceCount() > 4) {
            deactivate();
            return;
        }

        System.out.println("dot = " + params().dirVector().dot(params().pos().vectorTowards(tileCenter)));
        if (
                (params().lastTileHitPos() != null && params().lastTileHitPos().equals(tileCenter)) ||
                params().dirVector().dot(params().pos().vectorTowards(tileCenter)) <=
                4 // check if it is facing the tile
        ) {
            return;
        }

        params().creaturesAlreadyHit().clear();

        params.currentDamage(params().baseDamage());

        params().wallBounceCount(params().wallBounceCount() + 1);

        System.out.println("here");
        params().lastTileHitPos(tileCenter);

        Vector2 collisionVector = params().pos().vectorTowards(tileCenter);

        float collisionAngle = collisionVector.angleDeg();

        float angle = params.dirVector().multiplyBy(-1).angleDeg();


        float reflectAngle = 0f;
        if (collisionAngle >= 45f && collisionAngle < 135f) {
            if (angle < 90f) {
                reflectAngle = 90f + Math.abs(angle - 90f);
            }
            else {
                reflectAngle = 90f - Math.abs(angle - 90f);
            }
        }
        if (collisionAngle >= 135f && collisionAngle < 225f) {
            if (angle < 180f) {
                reflectAngle = 180f + Math.abs(angle - 180f);
            }
            else {
                reflectAngle = 180f - Math.abs(angle - 180f);
            }
        }
        if (collisionAngle >= 225f && collisionAngle < 315f) {
            if (angle < 270f) {
                reflectAngle = 270f + Math.abs(angle - 270f);
            }
            else {
                reflectAngle = 270f - Math.abs(angle - 270f);
            }
        }
        if ((collisionAngle >= 315f && collisionAngle < 360f) || (collisionAngle >= 0f && collisionAngle < 45f)) {
            if (angle >= 315f && angle < 360f) {
                reflectAngle = Math.abs(angle - 360f);
            }
            else {
                reflectAngle = 360f - Math.abs(angle - 360f);
            }
        }

        params.dirVector(params.dirVector().setAngleDeg(reflectAngle));

    }


}
