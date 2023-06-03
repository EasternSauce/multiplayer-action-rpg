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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureParams implements EntityParams {
    private final CreatureStats stats = CreatureStats.of();
    private final CreatureMovementParams movementParams = CreatureMovementParams.of();
    private final CreatureEffectParams effectParams = CreatureEffectParams.of();
    private EnemyParams enemyParams;

    private CreatureId id;

    private Vector2 pos;
    private AreaId areaId;

    private Vector2 initialPos;
    private AreaId initialAreaId;

    private SimpleTimer animationTimer = SimpleTimer.getStartedTimer();

    private String textureName;

    private SimpleTimer respawnTimer = SimpleTimer.getExpiredTimer();
    private Float respawnTime = 5f;

    private Float actionCooldown = 0.7f;

    private Boolean isDead = false;
    private Boolean isAwaitingRespawn = false;

    private Map<SkillType, Skill> skills = new ConcurrentSkipListMap<>();

    private Set<DropTableEntry> dropTable;

    private Map<Integer, Item> equipmentItems = new ConcurrentSkipListMap<>();
    private Map<Integer, Item> inventoryItems = new ConcurrentSkipListMap<>();

    private Float dropRngSeed = (float) Math.random();

    private SimpleTimer generalSkillPerformCooldownTimer = SimpleTimer.getExpiredTimer();

    public static CreatureParams of(CreatureId creatureId, AreaId areaId, EnemySpawn enemySpawn) {
        return produceCreatureParams(creatureId,
                                     areaId,
                                     enemySpawn.getPos(),
                                     enemySpawn.getEnemyTemplate().getEnemyType().textureName);
    }

    public static CreatureParams of(CreatureId creatureId, AreaId areaId, Vector2 pos, String textureName) {
        return produceCreatureParams(creatureId, areaId, pos, textureName);
    }

    private static CreatureParams produceCreatureParams(CreatureId creatureId, AreaId areaId, Vector2 enemySpawn,
                                                        String textureName) {
        CreatureParams params = CreatureParams.of();
        params.id = creatureId;
        params.areaId = areaId;
        params.pos = enemySpawn;
        params.initialPos = enemySpawn;
        params.initialAreaId = areaId;
        params.textureName = textureName;

        Map<SkillType, Skill> allPossibleSkills = Arrays
            .stream(SkillType.values())
            .collect(Collectors.toMap(Function.identity(), skillType -> Skill.of(skillType, creatureId)));

        // TODO: should we restrict which creature can perform which skill?
        params.skills = new ConcurrentSkipListMap<>(allPossibleSkills);

        Map<CreatureEffect, CreatureEffectState> allPossibleEffects = Arrays
            .stream(CreatureEffect.values())
            .collect(Collectors.toMap(effect -> effect, effect -> CreatureEffectState.of()));

        params.getEffectParams().setEffects(new ConcurrentSkipListMap<>(allPossibleEffects));

        return params;
    }
}
