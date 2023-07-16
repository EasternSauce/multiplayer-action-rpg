package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPointId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureParams implements EntityParams {
    @NonNull
    private final CreatureStats stats = CreatureStats.of();
    @NonNull
    private final CreatureMovementParams movementParams = CreatureMovementParams.of();
    @NonNull
    private final CreatureEffectParams effectParams = CreatureEffectParams.of();
    private EnemyParams enemyParams;

    @NonNull
    private CreatureId id;

    private Vector2 pos;
    @NonNull
    private AreaId areaId;

    @NonNull
    private Vector2 initialPos;
    @NonNull
    private AreaId initialAreaId;

    @NonNull
    private SimpleTimer animationTimer = SimpleTimer.getStartedTimer();

    @NonNull
    private String textureName;

    @NonNull
    private SimpleTimer timeSinceDeathTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private Float respawnTime = 5f;

    @NonNull
    private Float actionCooldown = 0.7f;

    @NonNull
    private Boolean dead = false;
    @NonNull
    private Boolean awaitingRespawn = false;

    @NonNull
    private Map<SkillType, Skill> skills = new ConcurrentSkipListMap<>();

    @NonNull
    private Set<DropTableEntry> dropTable = new ConcurrentSkipListSet<>();

    @NonNull
    private Map<Integer, Item> equipmentItems = new ConcurrentSkipListMap<>();
    @NonNull
    private Map<Integer, Item> inventoryItems = new ConcurrentSkipListMap<>();
    @NonNull
    private Map<Integer, Item> potionMenuItems = new ConcurrentSkipListMap<>();

    @NonNull
    private Float dropRngSeed = (float) Math.random(); // TODO: use random generator

    @NonNull
    private SimpleTimer generalSkillPerformCooldownTimer = SimpleTimer.getExpiredTimer();

    private EnemyRallyPointId enemyRallyPointId; // TODO: move to enemy params?

    private RandomGenerator randomGenerator;

    public static CreatureParams of(CreatureId creatureId,
                                    AreaId areaId,
                                    Vector2 pos,
                                    EnemyTemplate enemyTemplate,
                                    int rngSeed) {
        return produceCreatureParams(creatureId, areaId, pos, enemyTemplate.getEnemyType().textureName, rngSeed);
    }

    private static CreatureParams produceCreatureParams(CreatureId creatureId,
                                                        AreaId areaId,
                                                        Vector2 enemySpawn,
                                                        String textureName,
                                                        int rngSeed) {
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

        Map<CreatureEffect, CreatureEffectState> allPossibleEffects = Arrays.stream(CreatureEffect.values()).collect(
            Collectors.toMap(effect -> effect, effect -> CreatureEffectState.of()));

        params.getEffectParams().setEffects(new ConcurrentSkipListMap<>(allPossibleEffects));

        params.setRandomGenerator(RandomGenerator.of(rngSeed));

        return params;
    }

    public static CreatureParams of(CreatureId creatureId,
                                    AreaId areaId,
                                    Vector2 pos,
                                    String textureName,
                                    int rngSeed) {

        // TODO remove later
        Map<Integer, Item> potionMenuItems = new ConcurrentSkipListMap<>();
        potionMenuItems.put(0, Item.of().setTemplate(ItemTemplate.templates.get("lifePotion")));
        potionMenuItems.put(1, Item.of().setTemplate(ItemTemplate.templates.get("lifePotion")));

        Map<Integer, Item> inventoryItems = new ConcurrentSkipListMap<>();
        inventoryItems.put(2, Item.of().setTemplate(ItemTemplate.templates.get("lifePotion")));
        // TODO

        return produceCreatureParams(creatureId, areaId, pos, textureName, rngSeed)
            .setPotionMenuItems(potionMenuItems)
            .setInventoryItems(inventoryItems);
    }
}
