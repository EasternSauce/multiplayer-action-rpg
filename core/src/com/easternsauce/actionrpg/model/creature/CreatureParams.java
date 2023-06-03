package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureParams implements EntityParams {
    private final CreatureStats stats = CreatureStats.of();
    private EnemyParams enemyParams = null;

    private CreatureId id;

    private AreaId areaId;

    private Vector2 pos;
    private Vector2 initialPos;
    private AreaId initialAreaId;
    private Vector2 previousPos;

    private SimpleTimer animationTimer = SimpleTimer.getStartedTimer();

    private Vector2 movingVector = Vector2.of(0, 0);
    private Vector2 dashingVector = Vector2.of(0, 0);

    private Vector2 movementCommandTargetPos = Vector2.of(0, 0);

    private Boolean reachedTargetPos = true;

    private Boolean isMoving = false;
    private Boolean isDashing = false;

    private List<Vector2> pathTowardsTarget = null;

    private Float previousTickLife = getStats().getLife();

    private String textureName;

    private SimpleTimer movementActionsPerSecondLimiterTimer = SimpleTimer.getExpiredTimer();
    private SimpleTimer changeAimDirectionActionsPerSecondLimiterTimer = SimpleTimer.getExpiredTimer(); // 10/10 field name

    private SimpleTimer isStillMovingCheckTimer = SimpleTimer.getExpiredTimer();

    private SimpleTimer respawnTimer = SimpleTimer.getExpiredTimer();
    private Float respawnTime = 5f;

    private Float actionCooldown = 0.7f;

    private Boolean isDead = false;
    private Boolean isAwaitingRespawn = false;

    private SimpleTimer staminaRegenerationTimer = SimpleTimer.getStartedTimer();
    private Float staminaRegenerationTickTime = 0.02f;
    private Float staminaRegeneration = 0.35f;

    private Map<SkillType, Skill> skills = new ConcurrentSkipListMap<>();

    private Boolean isPathMirrored = false;

    private Set<DropTableEntry> dropTable;

    private Boolean isStillInsideGateAfterTeleport = false;
    private AreaId areaWhenEnteredGate;

    private Map<Integer, Item> equipmentItems = new ConcurrentSkipListMap<>();
    private Map<Integer, Item> inventoryItems = new ConcurrentSkipListMap<>();

    private Float dropRngSeed = (float) Math.random();

    private Float appliedSlowEffectiveness = 0f;

    private Float appliedPoisonDamage = 0f;

    private Vector2 aimDirection = Vector2.of(0f, 0f);

    private Map<CreatureEffect, CreatureEffectState> effects = new ConcurrentSkipListMap<>();

    private SimpleTimer gateTeleportCooldownTimer = SimpleTimer.getExpiredTimer();

    private SimpleTimer generalSkillPerformCooldownTimer = SimpleTimer.getExpiredTimer();

    private SimpleTimer damageOverTimeTimer = SimpleTimer.getExpiredTimer();
    private SimpleTimer lifeRegenerationOverTimeTimer = SimpleTimer.getExpiredTimer();
    private SimpleTimer manaRegenerationOverTimeTimer = SimpleTimer.getExpiredTimer();

    private Float currentDamageOverTimeTaken = 0f;

    private CreatureId currentDamageOverTimeDealerCreatureId = null;

    private Float currentSlowMagnitude = 0f;

    private Vector2 facingVector = Vector2.of(0f, 0f);

    private Float dashingVelocity = 0f;

    public static CreatureParams of(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn) {
        return getCreatureParams(creatureId,
                                 areaId,
                                 enemySpawn.getPos(),
                                 enemySpawn.getEnemyTemplate().getEnemyType().textureName);
    }

    public static CreatureParams of(CreatureId creatureId, AreaId areaId, Vector2 pos, String textureName) {
        return getCreatureParams(creatureId, areaId, pos, textureName);
    }

    private static CreatureParams getCreatureParams(CreatureId creatureId, AreaId areaId, Vector2 enemySpawn,
                                                    String textureName) {
        CreatureParams params = CreatureParams.of();
        params.id = creatureId;
        params.areaId = areaId;
        params.pos = enemySpawn;
        params.initialPos = enemySpawn;
        params.initialAreaId = areaId;
        params.textureName = textureName;

        params.skills = // TODO: should we restrict which creature can perform which skill?
            new ConcurrentSkipListMap<>(Arrays
                                            .stream(SkillType.values())
                                            .collect(Collectors.toMap(Function.identity(),
                                                                      skillType -> Skill.of(skillType, creatureId))));

        params.effects = new ConcurrentSkipListMap<>(Arrays
                                                         .stream(CreatureEffect.values())
                                                         .collect(Collectors.toMap(effect -> effect,
                                                                                   effect -> CreatureEffectState.of())));

        return params;
    }
}
