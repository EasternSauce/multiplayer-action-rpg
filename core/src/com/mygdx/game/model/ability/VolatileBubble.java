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
public class VolatileBubble extends Projectile {

    AbilityParams params;

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
        float baseAngle = params().dirVector().angleDeg();

        game.chainAbility(this, AbilityType.ICE_SPEAR, params().pos(), params.dirVector(), game);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 72f),
                          game);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 144f),
                          game);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 216f),
                          game);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 288f),
                          game);
    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {
        deactivate();
    }

    public static VolatileBubble of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        VolatileBubble ability = VolatileBubble.of();
        ability.params =
                abilityParams
                        .width(1.5f)
                        .height(1.5f)
                        .channelTime(0f)
                        .activeTime(30f)
                        .textureName("bubble")
                        .baseDamage(15f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(true)
                        .rotationShift(0f)
                        .delayedActionTime(0.001f)
                        .speed(10f);


        return ability;
    }
}
