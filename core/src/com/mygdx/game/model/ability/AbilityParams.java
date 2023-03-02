package com.mygdx.game.model.ability;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityParams {
    AbilityId id;
    AreaId areaId;
    AbilityState state = AbilityState.INACTIVE;
    Vector2 pos;
    Float width;
    Float height;
    CreatureId creatureId;
    SimpleTimer stateTimer = SimpleTimer.getExpiredTimer();
    Vector2 dirVector;
    String textureName;
    //    AbilityRect rect;

    Float rotationAngle = 0f;

    Boolean isChannelAnimationLooping;
    Boolean isActiveAnimationLooping;

    Float range;

    Set<CreatureId> creaturesAlreadyHit = new ConcurrentSkipListSet<>();

    Vector2 velocity;
    Float speed;

    Float damage;

    Float channelTime;
    Float activeTime;

    Boolean attackWithoutMoving = false;

    Float delayedActionTime;

    Boolean delayedActionCompleted = false;

    Float maxPlacementRange;

    Boolean inactiveBody = false;

    Float rotationShift;

    Boolean flip = false;

    Float rngSeed;

    SimpleTimer changeDirectionTimer = SimpleTimer.getStartedTimer();

    Boolean foundTarget = false;

    Vector2 chainFromPos;
    Vector2 chainToPos;

    //    public static AbilityParams of(AbilityParams abilityParams, MyGdxGame game) {
    //        return AbilityParams.of()
    //                            .id(abilityInitialParams.abilityId())
    //                            .areaId(abilityInitialParams.areaId())
    //                            .creatureId(abilityInitialParams.creatureId())
    //                            .dirVector(abilityInitialParams.abilityDirVector())
    //                            .chainFromPos(abilityInitialParams.abilityChainFromPos())
    //                            .pos(abilityInitialParams.creaturePosWhenSkillPerformed())
    //                            .width(abilityInitialParams.abilityWidth())
    //                            .height(abilityInitialParams.abilityHeight())
    //                            .rotationAngle(abilityInitialParams.abilityRotationAngle())
    //                            .creaturesAlreadyHit(abilityInitialParams.abilityCreaturesAlreadyHit())
    //                            .creatureStartingPos(abilityInitialParams.creaturePosCurrent());
    //    }

}
