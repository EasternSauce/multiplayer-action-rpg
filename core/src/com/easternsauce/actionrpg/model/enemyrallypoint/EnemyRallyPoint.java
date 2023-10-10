package com.easternsauce.actionrpg.model.enemyrallypoint;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.EnemySpawnAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class EnemyRallyPoint {
  @Getter
  private final SimpleTimer respawnTimer = SimpleTimer.getExpiredTimer();
  @Getter
  private EnemyRallyPointId id;
  @Getter
  private EnemyRallyPointInfo rallyPointInfo;

  public static EnemyRallyPoint of(EnemyRallyPointId id, EnemyRallyPointInfo rallyPointInfo) {
    EnemyRallyPoint enemyRallyPoint = EnemyRallyPoint.of();

    enemyRallyPoint.id = id;
    enemyRallyPoint.rallyPointInfo = rallyPointInfo;

    return enemyRallyPoint;
  }

  public void update(float delta, CoreGame game) {
    respawnTimer.update(delta);

    long enemiesAliveCount = game.getGameState().accessCreatures().getCreatures().values().stream().filter(
      creature -> creature.getParams().getEnemyRallyPointId() != null &&
        creature.getParams().getEnemyRallyPointId().getValue().equals(id.getValue()) && creature.isAlive()).count();

    if (respawnTimer.getTime() > Constants.ENEMY_RESPAWN_TIME) {
      long enemiesToSpawn = rallyPointInfo.getEnemiesTotal() - enemiesAliveCount;

      Set<Creature> playersNearby = game.getActiveCreatures().values().stream().filter(
        otherCreature -> otherCreature instanceof Player &&
          otherCreature.getParams().getAreaId().getValue().equals(rallyPointInfo.getAreaId().getValue()) &&
          otherCreature.getParams().getPos().distance(rallyPointInfo.getPos()) <
            Constants.PREVENT_ENEMY_RESPAWN_DISTANCE).collect(Collectors.toSet());

      if (playersNearby.isEmpty()) {
        if (enemiesToSpawn > 0) {
          for (int i = 0; i < enemiesToSpawn; i++) {
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));

            EnemyTemplate randomEnemyTemplate = getRandomEnemyTemplate(game);

            EnemySpawnAction action = EnemySpawnAction.of(enemyId, rallyPointInfo.getAreaId(), id, randomEnemyTemplate);

            game.getGameState().scheduleServerSideAction(action);
          }
        }
      }
      respawnTimer.restart();
    }
  }

  private EnemyTemplate getRandomEnemyTemplate(CoreGame game) {
    AtomicReference<EnemyTemplate> randomEnemyTemplate = new AtomicReference<>(null);

    AtomicReference<Float> totalWeight = new AtomicReference<>((float) 0);

    rallyPointInfo.getEnemyTemplateWeights()
      .forEach((enemyTemplate, weight) -> totalWeight.set(totalWeight.get() + weight));

    AtomicReference<Float> randValue = new AtomicReference<>(
      Math.abs(game.getGameState().getRandomGenerator().nextFloat()) * totalWeight.get());

    rallyPointInfo.getEnemyTemplateWeights().forEach((enemyTemplate, weight) -> {
      if (randomEnemyTemplate.get() == null && randValue.get() < weight) {
        randomEnemyTemplate.set(enemyTemplate);
      }
      randValue.updateAndGet(value -> value - weight);
    });
    return randomEnemyTemplate.get();
  }
}
