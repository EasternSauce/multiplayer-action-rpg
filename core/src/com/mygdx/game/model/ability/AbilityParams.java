package com.mygdx.game.model.ability;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
    @NonNull Vector2 dirVector = Vector2.of(0f, 0f);
    String textureName;

    Float rotationAngle = 0f;

    Boolean isChannelAnimationLooping;
    Boolean isActiveAnimationLooping;

    Float range;

    Map<CreatureId, Float> creaturesAlreadyHit = new ConcurrentSkipListMap<>();

    Vector2 velocity;
    Float speed;

    Float baseDamage;
    Float damageMultiplier = 1.0f;

    Float channelTime;
    Float activeTime;

    Boolean attackWithoutMoving = false;

    Float delayedActionTime;

    Boolean delayedActionCompleted = false;

    Float maxPlacementRange;

    Boolean isSkipCreatingBody = false;

    Float rotationShift;

    Boolean isFlip = false;

    Float abilityRngSeed;

    SimpleTimer changeDirectionTimer = SimpleTimer.getStartedTimer();

    Boolean foundTarget = false;

    Vector2 chainFromPos;
    Vector2 chainToPos;

    Vector2 skillStartPos;

    Float wallBounceCount = 0f;

    Boolean isComingBack = false;

    SkillType skillType;


}
