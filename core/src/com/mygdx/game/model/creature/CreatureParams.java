package com.mygdx.game.model.creature;

import com.mygdx.game.game.EnemySpawn;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.util.RandomHelper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureParams {
    CreatureId id;

    AreaId areaId;

    Vector2 pos;
    Vector2 previousPos;

    SimpleTimer animationTimer = SimpleTimer.getStartedTimer();

    Vector2 movingVector = Vector2.of(0, 0);

    Vector2 movementCommandTargetPos = Vector2.of(0, 0);

    Boolean reachedTargetPos = true;

    Boolean isMoving = false;

    Float speed = 10f;

    CreatureId targetCreatureId = null;

    Boolean forcePathCalculation = false;
    SimpleTimer pathCalculationCooldownTimer = SimpleTimer.getExpiredTimer();
    Float pathCalculationCooldown;

    List<Vector2> pathTowardsTarget = null;

    Float life = 100f;
    Float maxLife = 100f;
    Float stamina = 100f;
    Float maxStamina = 100f;
    Float mana = 100f;
    Float maxMana = 100f;
    Float armor = 0f;

    String textureName;

    SimpleTimer movementCommandsPerSecondLimitTimer = SimpleTimer.getExpiredTimer();
    Float attackCommandsPerSecondLimit = 0.2f;

    SimpleTimer isStillMovingTimer = SimpleTimer.getExpiredTimer();

    SimpleTimer respawnTimer = SimpleTimer.getExpiredTimer();
    Float respawnTime = 5f;

    Float actionCooldown = 0.7f;

    Boolean justDied = false;
    Boolean isDead = false;
    Boolean awaitingRespawn = false;

    SimpleTimer staminaRegenerationTimer = SimpleTimer.getStartedTimer();
    Float staminaRegenerationTickTime = 0.02f;
    Float staminaRegeneration = 0.35f;

    SimpleTimer aggroTimer = SimpleTimer.getExpiredTimer();
    Float loseAggroTime = 7f;
    CreatureId aggroedCreatureId = null;

    CreatureId attackedByCreatureId = null;

    CreatureId lastFoundTargetId = null;
    SimpleTimer findTargetTimer = SimpleTimer.getExpiredTimer();
    Float findTargetCooldown;

    Map<SkillType, Skill> skills = new HashMap<>();

    Boolean isPathMirrored = false;


    public static CreatureParams of(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn) {
        CreatureParams params = CreatureParams.of();
        params.id = creatureId;
        params.areaId = areaId;
        params.pos = enemySpawn.pos();
        params.textureName = enemySpawn.enemyType().textureName;
        params.findTargetCooldown = 0.5f + RandomHelper.seededRandomFloat(creatureId);
        //        params.pathCalculationFailurePenalty = 10f + 5f * RandomHelper.seededRandomFloat(creatureId);
        params.pathCalculationCooldown = 4f + 2f * RandomHelper.seededRandomFloat(creatureId);

        params.skills = // TODO: should we restrict which creature can perform which skill?
                Arrays.stream(SkillType.values())
                      .collect(Collectors.toMap(Function.identity(), skillType -> Skill.of(skillType, creatureId)));

        return params;
    }

    public static CreatureParams of(CreatureId creatureId, AreaId areaId, Vector2 pos, String textureName) {
        CreatureParams params = CreatureParams.of();
        params.id = creatureId;
        params.areaId = areaId;
        params.pos = pos;
        params.textureName = textureName;
        params.findTargetCooldown = 0.5f + RandomHelper.seededRandomFloat(creatureId);
        //        params.pathCalculationFailurePenalty = 10f + 5f * RandomHelper.seededRandomFloat(creatureId);
        params.pathCalculationCooldown = 1f + RandomHelper.seededRandomFloat(creatureId);

        params.skills = // TODO: should we restrict which creature can perform which skill?
                Arrays.stream(SkillType.values())
                      .collect(Collectors.toMap(Function.identity(), skillType -> Skill.of(skillType, creatureId)));

        return params;
    }

}
