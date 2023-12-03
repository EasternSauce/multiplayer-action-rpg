package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AutoControlsStateProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  public void process(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    if (enemyParams.getAutoControlsStateProcessorTimer().getTime() > enemyParams.getAutoControlsStateProcessorTime()) {
      enemyParams.getAutoControlsStateProcessorTimer().restart();

      if (!enemyParams.getTargetCreatureId().isEmpty()) {
        if (enemyParams.getAutoControlsState() == AutoControlsState.ALERTED) {
          handleAlerted(game, enemy);
        } else if (enemyParams.getAutoControlsState() == AutoControlsState.AGGRESSIVE) {
          handleAggressive(enemy);
        } else if (enemyParams.getAutoControlsState() == AutoControlsState.KEEP_DISTANCE) {
          handleKeepDistance(game, enemy);
        }
      }

      float randomTime = 1f + Math.abs(enemy.getParams().getRandomGenerator().nextFloat());
      enemyParams.setAutoControlsStateProcessorTime(randomTime);
    }
  }

  private void handleAggressive(Creature creature) {
    if (!creature.getEnemyParams().isBossEnemy() &&
      Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.35f) {
      creature.getEnemyParams().setAutoControlsState(AutoControlsState.KEEP_DISTANCE);

    }
  }

  private void handleKeepDistance(CoreGame game, Creature creature) {
    Vector2 targetPos = game.getCreaturePos(creature.getEnemyParams().getTargetCreatureId());

    if (targetPos != null) {
      Vector2 vectorTowards = targetPos.vectorTowards(creature.getParams().getPos());

      Vector2 backUpPos = targetPos.add(vectorTowards.normalized()
        .multiplyBy(creature.getEnemyParams().getWalkUpRange() + Constants.BACK_UP_DISTANCE));

      creature.getEnemyParams().setCurrentDefensivePos(
        Vector2.of(backUpPos.getX() + creature.getParams().getRandomGenerator().nextFloat(),
          backUpPos.getY() + creature.getParams().getRandomGenerator().nextFloat()));

      if (Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.7f) {
        creature.getEnemyParams().setAutoControlsState(AutoControlsState.AGGRESSIVE);
      }
    }
  }

  private void handleAlerted(CoreGame game, Creature creature) {
    Vector2 targetPos = game.getCreaturePos(creature.getEnemyParams().getTargetCreatureId());

    if (targetPos != null) {
      Vector2 vectorTowards = targetPos.vectorTowards(creature.getParams().getPos());

      Vector2 defensivePos = targetPos.add(vectorTowards.normalized().multiplyBy(Constants.DEFENSIVE_POS_DISTANCE));

      creature.getEnemyParams().setCurrentDefensivePos(
        Vector2.of(defensivePos.getX() + 4f * creature.getParams().getRandomGenerator().nextFloat(),
          defensivePos.getY() + 4f * creature.getParams().getRandomGenerator().nextFloat()));
    }

    if (Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.5f) {
      creature.getEnemyParams().setAutoControlsState(AutoControlsState.AGGRESSIVE);
    }
  }

}
