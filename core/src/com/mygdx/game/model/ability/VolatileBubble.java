package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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
    public AbilityType type() {
        return AbilityType.VOLATILE_BUBBLE;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(AbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(AbilityChainable game) {
        float baseAngle = params().dirVector().angleDeg();
        game.chainAbility(this, AbilityType.ICE_SPEAR, params().pos(), params.dirVector(), null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 72f),
                          null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 144f),
                          null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 216f),
                          null);
        game.chainAbility(this,
                          AbilityType.ICE_SPEAR,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 288f),
                          null);
    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onTerrainHit() {

    }

    public static VolatileBubble of(AbilityId abilityId,
                                    AreaId areaId,
                                    CreatureId creatureId,
                                    Vector2 pos,
                                    Vector2 dirVector,
                                    Set<CreatureId> creaturesAlreadyHit) {
        VolatileBubble ability = VolatileBubble.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1.5f)
                                      .height(1.5f)
                                      .channelTime(0f)
                                      .activeTime(30f)
                                      .textureName("bubble")
                                      .creatureId(creatureId)
                                      .damage(40f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(true)
                                      .creaturesAlreadyHit(new HashSet<>())
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f)
                                      .speed(5f);


        return ability;
    }
}
