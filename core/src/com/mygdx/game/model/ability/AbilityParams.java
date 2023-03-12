package com.mygdx.game.model.ability;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

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

    Float rotationAngle = 0f;

    Boolean isChannelAnimationLooping;
    Boolean isActiveAnimationLooping;

    Float range;

    Map<CreatureId, Float> creaturesAlreadyHit = new ConcurrentSkipListMap<>();

    Vector2 velocity;
    Float speed;

    Float baseDamage;
    Float currentDamage;

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

    Vector2 skillStartPos;

    Float wallBounceCount = 0f;

    Boolean comingBack = false;

    SkillType skillType;


}
