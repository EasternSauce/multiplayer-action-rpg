package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.id.NullCheckpointId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.item.ItemTemplate;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.RandomGenerator;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  private EntityId<Creature> id = NullCreatureId.of();

  private Vector2 pos;
  @NonNull
  private EntityId<Area> areaId = NullAreaId.of();

  @NonNull
  private Vector2 initialPos;
  @NonNull
  private EntityId<Area> initialAreaId = NullAreaId.of();

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
  private Map<SkillType, Skill> skills = new OrderedMap<>();

  @NonNull
  private Set<DropTableEntry> dropTable = new ConcurrentSkipListSet<>();

  @NonNull
  private Map<Integer, Item> equipmentItems = new ConcurrentSkipListMap<>(); // TODO: use orderedmap, and introduce nullitem
  @NonNull
  private Map<Integer, Item> inventoryItems = new ConcurrentSkipListMap<>();
  @NonNull
  private Map<Integer, Item> potionMenuItems = new OrderedMap<>();

  @NonNull
  private Float dropRngSeed = (float) Math.random(); // TODO: use random generator

  @NonNull
  private SimpleTimer minimumSkillPerformCooldownTimer = SimpleTimer.getExpiredTimer();

  private EntityId<EnemyRallyPoint> enemyRallyPointId; // TODO: move to enemy params?

  private RandomGenerator randomGenerator;

  private OnDeathAction onDeathAction;

  @NonNull
  private Float lastTimeMoved = -Float.MAX_VALUE;
  @NonNull
  private Float lastTimeUsedSkill = -Float.MAX_VALUE;

  private EntityId<Checkpoint> currentCheckpointId = NullCheckpointId.of();

  private Integer stunResistance = 0;
  private SimpleTimer stunResistanceReductionTimer = SimpleTimer.getExpiredTimer();

  public static CreatureParams of(EntityId<Creature> creatureId, EntityId<Area> areaId, Vector2 pos, EnemyTemplate enemyTemplate, int rngSeed) {
    return produceCreatureParams(creatureId, areaId, pos, enemyTemplate.getEnemyType().textureName, rngSeed);
  }

  private static CreatureParams produceCreatureParams(EntityId<Creature> creatureId, EntityId<Area> areaId, Vector2 enemySpawn, String textureName, int rngSeed) {
    CreatureParams params = CreatureParams.of();
    params.id = creatureId;
    params.areaId = areaId;
    params.pos = enemySpawn;
    params.initialPos = enemySpawn;
    params.initialAreaId = areaId;
    params.textureName = textureName;

    Map<SkillType, Skill> allPossibleSkills = Arrays.stream(SkillType.values())
      .collect(Collectors.toMap(Function.identity(), skillType -> Skill.of(skillType, creatureId), (o1, o2) -> o1, OrderedMap::new));

    // TODO: should we restrict which creature can perform which skill?
    params.skills = new OrderedMap<>(allPossibleSkills);

    Map<CreatureEffect, CreatureEffectState> allPossibleEffects = Arrays.stream(CreatureEffect.values())
      .collect(Collectors.toMap(effect -> effect, effect -> CreatureEffectState.of(), (o1, o2) -> o1, OrderedMap::new));

    params.getEffectParams().setEffects(new OrderedMap<>(allPossibleEffects));

    params.setRandomGenerator(RandomGenerator.of(rngSeed));

    return params;
  }

  public static CreatureParams of(EntityId<Creature> creatureId, EntityId<Area> areaId, Vector2 pos, String textureName, int rngSeed) {
    // TODO remove later
    Map<Integer, Item> potionMenuItems = new OrderedMap<>();
    potionMenuItems.put(0, Item.of().setTemplate(ItemTemplate.templates.get("lifePotion")));
    potionMenuItems.put(1, Item.of().setTemplate(ItemTemplate.templates.get("lifePotion")));

    Map<Integer, Item> inventoryItems = new ConcurrentSkipListMap<>(); // TODO: fix later
    inventoryItems.put(2, Item.of().setTemplate(ItemTemplate.templates.get("lifePotion")));
    inventoryItems.put(4, Item.of().setTemplate(ItemTemplate.templates.get("topazRing")));
    inventoryItems.put(5, Item.of().setTemplate(ItemTemplate.templates.get("rubyRing")));
    inventoryItems.put(7, Item.of().setTemplate(ItemTemplate.templates.get("hideGloves")).setGrantedSkills(
      Stream.of(new AbstractMap.SimpleEntry<>(SkillType.FIST_SLAM_COMBO, 1),
          new AbstractMap.SimpleEntry<>(SkillType.MAGIC_ORB, 1),
          new AbstractMap.SimpleEntry<>(SkillType.VOLATILE_BUBBLE, 1),
          new AbstractMap.SimpleEntry<>(SkillType.SUMMON_GHOSTS, 1),
          new AbstractMap.SimpleEntry<>(SkillType.POISONOUS_MIXTURE, 1),
          new AbstractMap.SimpleEntry<>(SkillType.LIGHTNING, 1),
          new AbstractMap.SimpleEntry<>(SkillType.DASH, 1),
          new AbstractMap.SimpleEntry<>(SkillType.METEOR_CALL, 1),
          new AbstractMap.SimpleEntry<>(SkillType.SUMMON_METEOR, 1),
          new AbstractMap.SimpleEntry<>(SkillType.FIREBALL, 1))
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (o1, o2) -> o1, OrderedMap::new))));
    // TODO

    return produceCreatureParams(creatureId, areaId, pos, textureName, rngSeed).setPotionMenuItems(potionMenuItems)
      .setInventoryItems(inventoryItems);
  }
}
