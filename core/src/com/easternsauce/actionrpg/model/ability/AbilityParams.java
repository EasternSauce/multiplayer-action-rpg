package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.CreaturesHitCounter;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
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
  Vector2 chainToPos;
  @NonNull
  private AbilityId id;
  @NonNull
  private AreaId areaId;
  @NonNull
  private AbilityState state = AbilityState.INACTIVE;
  private Vector2 pos;
  @NonNull
  private Float width;
  @NonNull
  private Float height;
  @NonNull
  private CreatureId creatureId;
  @NonNull
  private SimpleTimer stateTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Vector2 dirVector = Vector2.of(0f, 0f);
  @NonNull
  private Vector2 vectorTowardsTarget = Vector2.of(0f, 0f);
  @NonNull
  private String textureName;
  @NonNull
  private Float rotationAngle = 0f;
  @NonNull
  private Boolean channelAnimationLooping;
  @NonNull
  private Boolean activeAnimationLooping;
  @NonNull
  private Float startingRange = 0f;
  @NonNull
  private Map<CreatureId, Float> creaturesAlreadyHit = new ConcurrentSkipListMap<>();
  @NonNull
  private CreaturesHitCounter damagingHitCreaturesHitCounter = CreaturesHitCounter.of();
  @NonNull
  private Vector2 velocity = Vector2.of(0f, 0f);
  @NonNull
  private Float speed = 0f;
  @NonNull
  private Float baseDamage;
  @NonNull
  private Float weaponDamage;
  @NonNull
  private Float damageMultiplier = 1.0f;
  @NonNull
  private Boolean playerAbility = false;
  @NonNull
  private Float channelTime;
  @NonNull
  private Float activeTime;
  @NonNull
  private Boolean attackWithoutMoving = false;
  private Float delayedActionTime;
  @NonNull
  private Boolean delayedActionCompleted = false;
  @NonNull
  private Float maxPlacementRange;
  @NonNull
  private Boolean skipCreatingBody = false;
  @NonNull
  private Float rotationShift = 0f;
  @NonNull
  private Boolean flip = false;
  @NonNull
  private SimpleTimer changeDirectionTimer = SimpleTimer.getStartedTimer();
  @NonNull
  private Boolean foundTarget = false;
  @NonNull
  private Vector2 chainFromPos;
  @NonNull
  private Vector2 skillStartPos;

  @NonNull
  private Float wallBounceCount = 0f;

  @NonNull
  private Boolean comingBack = false;

  @NonNull
  private SkillType skillType;

  @NonNull
  private Boolean markedAsShielded = false;

  private Float directionalAttachedAbilityRotationShift = null;

  private Float overrideScale;
  private Float overrideDuration;
  private Float overrideDamage;
  private Float overrideMaximumRange;
  private Float overrideSpeed;
  private Float overrideStunDuration;

  private RandomGenerator randomGenerator;

  private Float maximumRange;

  private Boolean noTexture = false;

  private Boolean dontOverridePos = false;

}
