package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureEffectParams {
  @NonNull
  private Map<CreatureEffect, CreatureEffectState> effects = new OrderedMap<>();
  @NonNull
  private SimpleTimer staminaRegenerationTimer = SimpleTimer.getStartedTimer();
  @NonNull
  private Float staminaRegenerationTickTime = 0.02f;
  @NonNull
  private Float staminaRegeneration = 0.35f;
  @NonNull
  private SimpleTimer damageOverTimeTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private SimpleTimer lifeRegenerationOverTimeTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private SimpleTimer manaRegenerationOverTimeTimer = SimpleTimer.getExpiredTimer();
  @NonNull
  private Float currentDamageOverTimeTaken = 0f;
  private EntityId<Creature> currentDamageOverTimeDealerCreatureId = NullCreatureId.of();
  @NonNull
  private Float currentSlowMagnitude = 0f;
}