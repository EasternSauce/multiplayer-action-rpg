package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.action.CreatureHitByDamageOverTimeAction;
import com.easternsauce.actionrpg.model.action.CreatureMovingVectorSetAction;
import com.easternsauce.actionrpg.model.action.SkillTryPerformAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.NullCreature;
import com.easternsauce.actionrpg.model.creature.enemy.EnemySkillUseEntry;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import com.easternsauce.actionrpg.util.MapUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CreatureAccessor {
  @Getter
  private GameState gameState;
  @Getter
  private GameStateDataHolder dataHolder;

  private static SkillType pickRandomSkillToUse(Set<EnemySkillUseEntry> skillUseEntries, Float distanceToTarget, CoreGame game) {
    Set<EnemySkillUseEntry> filteredSkillUseEntries = skillUseEntries.stream()
      .filter(enemySkillUseEntry -> enemySkillUseEntry.getSkillUseRange() > distanceToTarget)
      .collect(Collectors.toSet());

    Map<EnemySkillUseEntry, Integer> map = filteredSkillUseEntries.stream().collect(Collectors.toMap(e -> e, EnemySkillUseEntry::getWeight));

    return MapUtils.getRandomElementOfWeightedMap(map, game.getGameState().getRandomGenerator().nextFloat()).getSkillType();
  }

  public Set<EntityId<Creature>> getActiveCreatureIds() {
    return getData().getActiveCreatureIds();
  }

  private GameStateData getData() {
    return dataHolder.getData();
  }

  public Vector2 getCreaturePos(EntityId<Creature> creatureId) {
    if (!getData().getCreatures().containsKey(creatureId)) {
      return null;
    }
    return getData().getCreatures().get(creatureId).getParams().getPos();
  }

  public Set<EntityId<Creature>> getCreaturesToUpdateForPlayerCreatureId(EntityId<Creature> playerCreatureId, CoreGame game) {
    Creature player = getData().getCreatures().get(playerCreatureId);

    if (player.isNull()) {
      return new HashSet<>();
    } else {
      return getData().getCreatures().keySet().stream().filter(creatureId -> {
        Creature creature = getCreature(creatureId);
        if (creature.isCurrentlyActive(game)) {
          return player.getParams().getAreaId().equals(creature.getParams().getAreaId()) &&
                  creature.getParams().getPos().distance(player.getParams().getPos()) < Constants.CLIENT_GAME_UPDATE_RANGE;
        } else {
          return false;
        }
      }).collect(Collectors.toSet());
    }


  }

  public void setCreatureMovingVector(EntityId<Creature> creatureId, Vector2 dirVector) {
    CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of(creatureId, dirVector);

    gameState.scheduleServerSideAction(action);
  }

  public void forEachAliveCreature(Consumer<Creature> creatureAction) {
    gameState.accessCreatures().getCreatures().values().stream().filter(Creature::isAlive).forEach(creatureAction);
  }

  public Map<EntityId<Creature>, Creature> getCreatures() {
    return getData().getCreatures();
  }

  public void forEachDeadCreature(Consumer<Creature> creatureAction) {
    gameState.accessCreatures().getCreatures().values().stream().filter(creature -> !creature.isAlive())
      .forEach(creatureAction);
  }

  // TODO: move to enemy?
  public void handleCreatureUseRandomSkillAtTarget(EntityId<Creature> creatureId, Vector2 vectorTowardsTarget, Float distanceToTarget, CoreGame game) {
    Creature creature = gameState.accessCreatures().getCreature(creatureId);

    SkillType pickedSkillType;

    pickedSkillType = pickRandomSkillToUse(creature.getEnemyParams().getSkillUses(), distanceToTarget,
      game);

    if (pickedSkillType != null) {
      SkillTryPerformAction action = SkillTryPerformAction.of(creatureId, pickedSkillType,
        creature.getParams().getPos(), vectorTowardsTarget);

      gameState.scheduleServerSideAction(action);
    }
  }

  public Creature getCreature(EntityId<Creature> creatureId) {
    if (creatureId.isNull() || !getData().getCreatures().containsKey(creatureId)) {
      return NullCreature.of();
    }
    return getData().getCreatures().get(creatureId);
  }

  public EntityId<Creature> getAliveCreatureIdClosestTo(Vector2 pos, float maxRange, Set<EntityId<Creature>> excluded, CoreGame game) {
    AtomicReference<EntityId<Creature>> minCreatureId = new AtomicReference<>(null);
    AtomicReference<Float> minDistance = new AtomicReference<>(Float.MAX_VALUE);

    gameState.getCreaturesToUpdate(game).forEach(creatureId -> {
      Creature creature = gameState.accessCreatures().getCreature(creatureId);
      float distance = pos.distance(creature.getParams().getPos());
      if (creature.isAlive() && distance < minDistance.get() && distance < maxRange && !excluded.contains(creatureId)) {
        minDistance.set(distance);
        minCreatureId.set(creatureId);
      }
    });

    return minCreatureId.get();
  }

  public void creatureTakeDamageOverTime(EntityId<Creature> attackerId, EntityId<Creature> targetId, Float damage) {
    CreatureHitByDamageOverTimeAction action = CreatureHitByDamageOverTimeAction.of(attackerId, targetId, damage);
    gameState.scheduleServerSideAction(action);
  }
}
