package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAbilityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.util.CreaturesHitCounter;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityParams implements EntityParams {
  Vector2 chainToPos;
  @NonNull
  private EntityId<Ability> id = NullAbilityId.of();
  @NonNull
  private EntityId<Area> areaId = NullAreaId.of();
  @NonNull
  private AbilityState state = AbilityState.INACTIVE;
  private Vector2 pos;
  @NonNull
  private Float width;
  @NonNull
  private Float height;
  @NonNull
  private SimpleTimer stateTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Vector2 dirVector = Vector2.of(0f, 0f);
  @NonNull
  private String textureName;
  @NonNull
  private Float rotationAngle = 0f;
  @NonNull
  private Boolean rotationAllowed = true;
  @NonNull
  private Boolean channelAnimationLooping;
  @NonNull
  private Boolean activeAnimationLooping;
  @NonNull
  private Float startingRange = 0f;
  @NonNull
  private Map<EntityId<Creature>, Float> creaturesAlreadyHit = new OrderedMap<>();
  @NonNull
  private CreaturesHitCounter damagingHitCreaturesHitCounter = CreaturesHitCounter.of();
  @NonNull
  private Vector2 velocity = Vector2.of(0f, 0f);
  @NonNull
  private Float speed = 0f;
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
  private Boolean flipX = false;
  @NonNull
  private Boolean flipY = false;
  @NonNull
  private SimpleTimer changeDirectionTimer = SimpleTimer.getStartedTimer();
  @NonNull
  private Boolean foundTarget = false;
  @NonNull
  private Vector2 chainFromPos;

  @NonNull
  private Float wallBounceCount = 0f;

  @NonNull
  private Boolean comingBack = false;

  @NonNull
  private Boolean markedAsShielded = false;

  private Float directionalAttachedAbilityRotationShift = null;

  private Float overrideScale;
  private Float overrideActiveDuration;
  private Float overrideMaximumRange;
  private Float overrideSpeed;
  private Float overrideStunDuration;

  private RandomGenerator randomGenerator;

  private Float maximumRange;

  private Boolean noTexture = false;

  private Boolean dontOverridePos = false;


  private SimpleTimer tickActionTimer = SimpleTimer.getExpiredTimer();

}
