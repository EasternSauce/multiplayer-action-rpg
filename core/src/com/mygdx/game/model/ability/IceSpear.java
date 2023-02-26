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

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class IceSpear extends Projectile {

    AbilityParams params;

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public AbilityType type() {
        return AbilityType.ICE_SPEAR;
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
    public void onCreatureHit() {
        //deactivate();
    }

    @Override
    public void onTerrainHit() {
        if (params().stateTimer().time() > 0.1f) {
            deactivate();
        }
    }

    public static IceSpear of(AbilityId abilityId,
                              AreaId areaId,
                              CreatureId creatureId,
                              Vector2 pos,
                              Vector2 dirVector,
                              Set<CreatureId> creaturesAlreadyHit) {
        IceSpear ability = IceSpear.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1.05f)
                                      .height(0.5f)
                                      .channelTime(0f)
                                      .activeTime(0.6f)
                                      .textureName("ice_shard")
                                      .creatureId(creatureId)
                                      .damage(10f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(true)
                                      .creaturesAlreadyHit(new ConcurrentSkipListSet<>())
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f)
                                      .speed(15f);


        return ability;
    }
}

