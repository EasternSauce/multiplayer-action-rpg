package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
public class EnemyParams {
    private CreatureId targetCreatureId;
    @NonNull
    private Boolean forcePathCalculation = false;
    @NonNull
    private SimpleTimer pathCalculationCooldownTimer = SimpleTimer.getExpiredTimer();
    private Float pathCalculationCooldown;
    @NonNull
    private SimpleTimer aggroTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private Float loseAggroTime = 3f;
    @SuppressWarnings("SpellCheckingInspection")
    private CreatureId aggroedCreatureId;
    private CreatureId attackedByCreatureId;
    private CreatureId lastFoundTargetId;
    @NonNull
    private SimpleTimer findTargetTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private Float findTargetCooldown;
    @NonNull
    private EnemyAutoControlState autoControlState;
    @NonNull
    private SimpleTimer autoControlStateTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private Float autoControlStateTime;
    @NonNull
    private Float autoControlStateRngSeed;
    private Vector2 currentDefensivePos;
    @NonNull
    private SimpleTimer justAttackedFromRangeTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private Float attackDistance;
    @NonNull
    private Boolean skillUseReadyToPick = true;
    private SkillType skillUsePickedSkillType = null;
    @NonNull
    private SimpleTimer attackCooldownTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private Float skillUseRngSeed = (float) Math.random();
    @NonNull
    private Set<EnemySkillUseEntry> skillUses;
    @NonNull
    private Boolean isPathMirrored = false;
    private List<Vector2> pathTowardsTarget = null;
}