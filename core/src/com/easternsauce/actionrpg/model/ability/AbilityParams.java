package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityParams implements EntityParams {
    @NonNull AbilityId id;
    @NonNull AreaId areaId;
    @NonNull AbilityState state = AbilityState.INACTIVE;
    @NonNull Vector2 pos;
    @NonNull Float width;
    @NonNull Float height;
    @NonNull CreatureId creatureId;
    @NonNull SimpleTimer stateTimer = SimpleTimer.getExpiredTimer();
    @NonNull Vector2 dirVector = Vector2.of(0f, 0f);
    @NonNull Vector2 vectorTowardsTarget = Vector2.of(0f, 0f);
    @NonNull String textureName;

    @NonNull Float rotationAngle = 0f;

    @NonNull Boolean isChannelAnimationLooping;
    @NonNull Boolean isActiveAnimationLooping;

    @NonNull Float range;

    @NonNull Map<CreatureId, Float> creaturesAlreadyHit = new ConcurrentSkipListMap<>();

    @NonNull Vector2 velocity;
    @NonNull Float speed;

    @NonNull Float baseDamage;
    @NonNull Float weaponDamage;
    @NonNull Float damageMultiplier = 1.0f;

    @NonNull Boolean isPlayerAbility = false;

    @NonNull Float channelTime;
    @NonNull Float activeTime;

    @NonNull Boolean attackWithoutMoving = false;

    @NonNull Float delayedActionTime;

    @NonNull Boolean delayedActionCompleted = false;

    @NonNull Float maxPlacementRange;

    @NonNull Boolean isSkipCreatingBody = false;

    @NonNull Float rotationShift;

    @NonNull Boolean isFlip = false;

    @NonNull Float abilityRngSeed;

    @NonNull SimpleTimer changeDirectionTimer = SimpleTimer.getStartedTimer();

    @NonNull Boolean foundTarget = false;

    @NonNull Vector2 chainFromPos;
    Vector2 chainToPos;

    @NonNull Vector2 skillStartPos;

    @NonNull Float wallBounceCount = 0f;

    @NonNull Boolean isComingBack = false;

    @NonNull SkillType skillType;

    @NonNull Boolean isHitShielded = false;

    Float overrideSize;
    Float overrideDuration;

}
