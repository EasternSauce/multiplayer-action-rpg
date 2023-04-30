package com.mygdx.game.model.creature;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.creature.effect.CreatureEffectState;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.item.ItemTemplate;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
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
    Float baseSpeed = 10f;

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

    Float previousTickLife = life;

    String textureName;

    SimpleTimer movementCommandsPerSecondLimitTimer = SimpleTimer.getExpiredTimer();
    Float attackCommandsPerSecondLimit = 0.2f;

    SimpleTimer isStillMovingCheckTimer = SimpleTimer.getExpiredTimer();

    SimpleTimer respawnTimer = SimpleTimer.getExpiredTimer();
    Float respawnTime = 5f;

    Float actionCooldown = 0.7f;

    Boolean isDead = false;
    Boolean isAwaitingRespawn = false;

    SimpleTimer staminaRegenerationTimer = SimpleTimer.getStartedTimer();
    Float staminaRegenerationTickTime = 0.02f;
    Float staminaRegeneration = 0.35f;

    SimpleTimer aggroTimer = SimpleTimer.getExpiredTimer();
    Float loseAggroTime = 3f;
    CreatureId aggroedCreatureId = null;

    CreatureId attackedByCreatureId = null;

    CreatureId lastFoundTargetId = null;
    SimpleTimer findTargetTimer = SimpleTimer.getExpiredTimer();
    Float findTargetCooldown;

    Map<SkillType, Skill> skills = new ConcurrentSkipListMap<>();

    Boolean isPathMirrored = false;

    EnemyAiState aiState = EnemyAiState.RESTING;

    SimpleTimer aiStateTimer = SimpleTimer.getExpiredTimer();

    Float aiStateTime;
    Float aiStateSeed;

    Vector2 defensivePosition;

    SimpleTimer justAttackedFromRangeTimer = SimpleTimer.getExpiredTimer();

    Float attackDistance = 3f;

    SkillType mainAttackSkill;

    Set<DropTableEntry> dropTable;

    Boolean justTeleportedToGate = false;
    AreaId areaWhenEnteredGate;

    Map<Integer, Item> equipmentItems = new ConcurrentSkipListMap<>();
    Map<Integer, Item> inventoryItems = new ConcurrentSkipListMap<>();

    Float dropRngSeed = (float) Math.random();

    Float appliedSlowEffectiveness = 0f;

    Float appliedPoisonDamage = 0f;

    Map<CreatureEffect, CreatureEffectState> effects = new ConcurrentSkipListMap<>();

    public static CreatureParams of(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn) {
        return getCreatureParams(creatureId,
                areaId,
                enemySpawn.getPos(),
                enemySpawn.getEnemyTemplate().getEnemyType().textureName);
    }


    public static CreatureParams of(CreatureId creatureId, AreaId areaId, Vector2 pos, String textureName) {
        return getCreatureParams(creatureId, areaId, pos, textureName);
    }

    private static CreatureParams getCreatureParams(CreatureId creatureId,
                                                    AreaId areaId,
                                                    Vector2 enemySpawn,
                                                    String textureName) {
        CreatureParams params = CreatureParams.of();
        params.id = creatureId;
        params.areaId = areaId;
        params.pos = enemySpawn;
        params.textureName = textureName;
        params.findTargetCooldown = 0.5f + (float) Math.random();
        params.pathCalculationCooldown = 4f + 2f * (float) Math.random();

        params.skills = // TODO: should we restrict which creature can perform which skill?
                new ConcurrentSkipListMap<>(Arrays.stream(SkillType.values())
                        .collect(Collectors.toMap(Function.identity(),
                                skillType -> Skill.of(skillType,
                                        creatureId))));

        params.effects = new ConcurrentSkipListMap<>(Arrays.stream(CreatureEffect.values())
                .collect(Collectors.toMap(effect -> effect,
                        effect -> CreatureEffectState.of())));

        params.aiStateSeed = (float) Math.random();
        params.aiStateTime = 0f;


        Map<SkillType, Integer> grantedSkills1 = new ConcurrentSkipListMap<>();
        grantedSkills1.put(SkillType.BOOMERANG, 1);

        Map<SkillType, Integer> grantedSkills2 = new ConcurrentSkipListMap<>();
        grantedSkills2.put(SkillType.SUMMON_SHIELD, 1);

        params.inventoryItems.put(2,
                Item.of()
                        .setTemplate(ItemTemplate.templates.get("leatherArmor"))
                        .setQualityModifier(0.6f)
                        .setGrantedSkills(grantedSkills1));
        params.inventoryItems.put(3,
                Item.of()
                        .setTemplate(ItemTemplate.templates.get("leatherArmor"))
                        .setQualityModifier(1f));
        params.inventoryItems.put(10,
                Item.of()
                        .setTemplate(ItemTemplate.templates.get("hideGloves"))
                        .setQualityModifier(0.9f));

        params.inventoryItems.put(11,
                Item.of()
                        .setTemplate(ItemTemplate.templates.get("woodenShield"))
                        .setQualityModifier(1.0f)
                        .setGrantedSkills(grantedSkills2));
        return params;
    }

}
