package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.util.RandomHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayfulGhost extends Projectile {

    AbilityParams params;

    public static PlayfulGhost of(AbilityId abilityId,
                                  AreaId areaId,
                                  CreatureId creatureId,
                                  Vector2 pos,
                                  Vector2 dirVector,
                                  Set<CreatureId> creaturesAlreadyHit) {
        PlayfulGhost ability = PlayfulGhost.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1.5f)
                                      .height(1.5f)
                                      .channelTime(0f)
                                      .activeTime(30f)
                                      .textureName("ghost")
                                      .creatureId(creatureId)
                                      .damage(15f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(true)
                                      .creaturesAlreadyHit(new ConcurrentSkipListSet<>())
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f)
                                      .speed(5f)
                                      .rngSeed(RandomHelper.seededRandomFloat(abilityId));


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public AbilityType type() {
        return AbilityType.PLAYFUL_GHOST;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(AbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(AbilityChainable game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }


    @Override
    void onActiveUpdate(AbilityUpdateable game) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());
        params().flip(params().rotationAngle() >= 90 && params().rotationAngle() < 270);
    }

    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onTerrainHit() {
        deactivate();
    }

    @SuppressWarnings("unused")
    public Float nextPositiveFloat() {
        params().rngSeed(RandomHelper.seededRandomFloat(params().rngSeed()));
        return params().rngSeed();
    }

    @SuppressWarnings("unused")
    public Float nextFloat() {
        params().rngSeed(RandomHelper.seededRandomFloat(params().rngSeed()));
        return (params().rngSeed() - 0.5f) * 2;
    }
}
