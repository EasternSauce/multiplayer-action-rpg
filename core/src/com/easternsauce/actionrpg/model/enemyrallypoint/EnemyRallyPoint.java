package com.easternsauce.actionrpg.model.enemyrallypoint;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.action.EnemySpawnAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.util.Constants;
import com.easternsauce.actionrpg.util.MapUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class EnemyRallyPoint implements Entity {
  @Getter
  private final SimpleTimer respawnTimer = SimpleTimer.getExpiredTimer();
  @Getter
  private EntityId<EnemyRallyPoint> id;
  @Getter
  private EnemyRallyPointInfo rallyPointInfo;

  public static EnemyRallyPoint of(EntityId<EnemyRallyPoint> id, EnemyRallyPointInfo rallyPointInfo) {
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
            EntityId<Creature> enemyId = EntityId.of("Enemy_" + (int) (Math.random() * 10000000));

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
    return MapUtils.getRandomElementOfWeightedMap(rallyPointInfo.getEnemyTemplateWeights(), game.getGameState().getRandomGenerator().nextFloat());
  }

  @Override
  public EntityParams getParams() {
    return null; // TODO: move params to here?
  }
}
