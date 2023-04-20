package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class VolatileBubble extends Projectile {

    AbilityParams params;

    public static VolatileBubble of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        VolatileBubble ability = VolatileBubble.of();
        ability.params = abilityParams.setWidth(1.5f)
                                      .setHeight(1.5f)
                                      .setChannelTime(0f)
                                      .setActiveTime(30f)
                                      .setTextureName("bubble")
                                      .setBaseDamage(15f)
                                      .setIsChannelAnimationLooping(false)
                                      .setIsActiveAnimationLooping(true)
                                      .setRotationShift(0f)
                                      .setDelayedActionTime(0.001f)
                                      .setSpeed(10f);


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(AbilityUpdatable game) {

    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {
        float baseAngle = getParams().getDirVector().angleDeg();

        game.chainAbility(this, AbilityType.ICE_SPEAR, getParams().getPos(), params.getDirVector());
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          getParams().getPos(),
                          params.getDirVector().setAngleDeg(baseAngle + 72f));
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          getParams().getPos(),
                          params.getDirVector().setAngleDeg(baseAngle + 144f));
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          getParams().getPos(),
                          params.getDirVector().setAngleDeg(baseAngle + 216f));
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          getParams().getPos(),
                          params.getDirVector().setAngleDeg(baseAngle + 288f));
    }


    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }
}
