package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
public class EnemyParams {
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
    private EnemyAutoControlState autoControlState;
    private SimpleTimer autoControlStateTimer = SimpleTimer.getExpiredTimer();
    private Float autoControlStateTime;
    private Float autoControlStateRngSeed;
    private Vector2 defensivePosition;
    private SimpleTimer justAttackedFromRangeTimer = SimpleTimer.getExpiredTimer();
    private Float attackDistance = 3f;
    private Boolean skillUseReadyToPick = true;
    private SkillType skillUsePickedSkillType = null;
    private SimpleTimer attackCooldownTimer = SimpleTimer.getExpiredTimer();
    private Float skillUseRngSeed = (float) Math.random();
    private Set<EnemySkillUseEntry> skillUses;
    private Boolean isPathMirrored = false;
}