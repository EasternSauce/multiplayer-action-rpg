package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AutoControlsMovementProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  public void process( Vector2 potentialTargetPos, Float distance, CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();
    if (enemyParams.getAutoControlsState() == AutoControlsState.AGGRESSIVE) {
      processAggressive(potentialTargetPos, distance, game);
    } else if (enemyParams.getAutoControlsState() == AutoControlsState.ALERTED) {
      processAlerted(game);
    } else if (enemyParams.getAutoControlsState() == AutoControlsState.KEEP_DISTANCE) {
      processKeepDistance(game);
    } else {
      enemy.stopMoving();
    }
  }

  private void processKeepDistance( CoreGame game) {
    Creature enemy = getEnemy(game);

    enemy.getParams().getStats().setSpeed(enemy.getParams().getStats().getBaseSpeed() / 2);
    if (enemy.getEnemyParams().getCurrentDefensivePos() != null) {
      goToPos(enemy.getEnemyParams().getCurrentDefensivePos(), game);
    }
  }

  private void processAlerted( CoreGame game) {
    Creature enemy = getEnemy(game);

    enemy.getParams().getStats().setSpeed(enemy.getParams().getStats().getBaseSpeed() / 3);
    if (enemy.getEnemyParams().getCurrentDefensivePos() != null) {
      goToPos(enemy.getEnemyParams().getCurrentDefensivePos(), game);
    }
  }

  private void processAggressive( Vector2 potentialTargetPos, Float distance, CoreGame game) {
    Creature enemy = getEnemy(game);

    if (distance > enemy.getEnemyParams().getWalkUpRange() - 1f) {
      enemy.getParams().getStats().setSpeed(enemy.getParams().getStats().getBaseSpeed());
      goToPos(potentialTargetPos, game);
    } else { // if no path or distance is small, then stop moving
      enemy.stopMoving();
    }
  }

  public void goToPos( Vector2 pos, CoreGame game) {
    Creature enemy = getEnemy(game);

    enemy.getParams().setLastTimeMoved(game.getGameState().getTime());

    if (!enemy.isStunned(game)) {
      enemy.moveTowards(pos);
    }
  }
}
