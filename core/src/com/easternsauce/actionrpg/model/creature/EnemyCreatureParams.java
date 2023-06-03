package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class EnemyCreatureParams {
    private CreatureId targetCreatureId = null;
    private Boolean forcePathCalculation = false;
    private SimpleTimer pathCalculationCooldownTimer = SimpleTimer.getExpiredTimer();
    private Float pathCalculationCooldown;
    private SimpleTimer aggroTimer = SimpleTimer.getExpiredTimer();
    private Float loseAggroTime = 3f;
    @SuppressWarnings("SpellCheckingInspection")
    private CreatureId aggroedCreatureId = null;
    private CreatureId attackedByCreatureId = null;
    private CreatureId lastFoundTargetId = null;
    private SimpleTimer findTargetTimer = SimpleTimer.getExpiredTimer();
    private Float findTargetCooldown;
    private EnemyAiState aiState;
    private SimpleTimer aiStateTimer = SimpleTimer.getExpiredTimer();
    private Float aiStateTime;
    private Float aiStateRngSeed;
    private Vector2 defensivePosition;
    private SimpleTimer justAttackedFromRangeTimer = SimpleTimer.getExpiredTimer();
    private Float attackDistance = 3f;
    private Boolean enemySkillUseReadyToPick = true;
    private SkillType enemySkillUsePickedSkillType = null;
    private SimpleTimer enemyAttackCooldownTimer = SimpleTimer.getExpiredTimer();
}