package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.util.SimpleTimer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureEffectParams {
    private Map<CreatureEffect, CreatureEffectState> effects = new ConcurrentSkipListMap<>();
    private SimpleTimer staminaRegenerationTimer = SimpleTimer.getStartedTimer();
    private Float staminaRegenerationTickTime = 0.02f;
    private Float staminaRegeneration = 0.35f;
    private Float appliedSlowEffectiveness = 0f;
    private Float appliedPoisonDamage = 0f;
    private SimpleTimer damageOverTimeTimer = SimpleTimer.getExpiredTimer();
    private SimpleTimer lifeRegenerationOverTimeTimer = SimpleTimer.getExpiredTimer();
    private SimpleTimer manaRegenerationOverTimeTimer = SimpleTimer.getExpiredTimer();
    private Float currentDamageOverTimeTaken = 0f;
    private CreatureId currentDamageOverTimeDealerCreatureId = null;
    private Float currentSlowMagnitude = 0f;
}