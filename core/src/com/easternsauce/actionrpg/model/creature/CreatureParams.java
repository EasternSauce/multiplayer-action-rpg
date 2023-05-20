package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffectState;
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
    private CreatureId id;

    private AreaId areaId;

    private Vector2 pos;
    private Vector2 initialPos;
    private AreaId initialAreaId;
    private Vector2 previousPos;

    private SimpleTimer animationTimer = SimpleTimer.getStartedTimer();

    private Vector2 movingVector = Vector2.of(0, 0);

    private Vector2 movementCommandTargetPos = Vector2.of(0, 0);

    private Boolean reachedTargetPos = true;

    private Boolean isMoving = false;

    private Float speed = 10f;
    private Float baseSpeed = 10f;

    private CreatureId targetCreatureId = null;

    private Boolean forcePathCalculation = false;
    private SimpleTimer pathCalculationCooldownTimer = SimpleTimer.getExpiredTimer();
    private Float pathCalculationCooldown;

    private List<Vector2> pathTowardsTarget = null;

    private Float life = 100f;
    private Float maxLife = 100f;
    private Float stamina = 100f;
    private Float maxStamina = 100f;
    private Float mana = 100f;
    private Float maxMana = 100f;
    private Float armor = 0f;

    private Float previousTickLife = life;

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

    private SimpleTimer aggroTimer = SimpleTimer.getExpiredTimer();
    private Float loseAggroTime = 3f;
    private CreatureId aggroedCreatureId = null;

    private CreatureId attackedByCreatureId = null;

    private CreatureId lastFoundTargetId = null;
    private SimpleTimer findTargetTimer = SimpleTimer.getExpiredTimer();
    private Float findTargetCooldown;

    private Map<SkillType, Skill> skills = new ConcurrentSkipListMap<>();

    private Boolean isPathMirrored = false;

    private EnemyAiState aiState = EnemyAiState.RESTING;

    private SimpleTimer aiStateTimer = SimpleTimer.getExpiredTimer();

    private Float aiStateTime;
    private Float aiStateSeed;

    private Vector2 defensivePosition;

    private SimpleTimer justAttackedFromRangeTimer = SimpleTimer.getExpiredTimer();

    private Float attackDistance = 3f;

    private SkillType mainAttackSkill;

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
        params.findTargetCooldown = 0.5f + (float) Math.random();
        params.pathCalculationCooldown = 4f + 2f * (float) Math.random();

        params.skills = // TODO: should we restrict which creature can perform which skill?
            new ConcurrentSkipListMap<>(Arrays
                                            .stream(SkillType.values())
                                            .collect(Collectors.toMap(Function.identity(),
                                                                      skillType -> Skill.of(skillType, creatureId))));

        params.effects = new ConcurrentSkipListMap<>(Arrays
                                                         .stream(CreatureEffect.values())
                                                         .collect(Collectors.toMap(effect -> effect,
                                                                                   effect -> CreatureEffectState.of())));

        params.aiStateSeed = (float) Math.random();
        params.aiStateTime = 0f;

        //        Map<SkillType, Integer> grantedSkills1 = new ConcurrentSkipListMap<>();
        //        grantedSkills1.put(SkillType.BOOMERANG, 1);
        //
        //        Map<SkillType, Integer> grantedSkills2 = new ConcurrentSkipListMap<>();
        //        grantedSkills2.put(SkillType.SUMMON_SHIELD, 1);

        //        params.inventoryItems.put(2,
        //                Item.of()
        //                        .setTemplate(ItemTemplate.templates.get("leatherArmor"))
        //                        .setQualityModifier(0.6f)
        //                        .setGrantedSkills(grantedSkills1));
        //        params.inventoryItems.put(3,
        //                Item.of()
        //                        .setTemplate(ItemTemplate.templates.get("leatherArmor"))
        //                        .setQualityModifier(1f));
        //        params.inventoryItems.put(10,
        //                Item.of()
        //                        .setTemplate(ItemTemplate.templates.get("hideGloves"))
        //                        .setQualityModifier(0.9f));
        //
        //        params.inventoryItems.put(11,
        //                Item.of()
        //                        .setTemplate(ItemTemplate.templates.get("woodenShield"))
        //                        .setQualityModifier(1.0f)
        //                        .setGrantedSkills(grantedSkills2));
        return params;
    }

}
