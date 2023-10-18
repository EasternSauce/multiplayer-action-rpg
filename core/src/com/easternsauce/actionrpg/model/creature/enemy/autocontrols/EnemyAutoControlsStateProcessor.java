package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsStateProcessor {
  public void process(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.getEnemyParams().getAutoControlsStateProcessorTimer().getTime() >
      creature.getEnemyParams().getAutoControlsStateProcessorTime()) {

      creature.getEnemyParams().getAutoControlsStateProcessorTimer().restart();

      if (creature.getEnemyParams().getTargetCreatureId() != null) {
        if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.ALERTED) {
          handleAlerted(game, creature);
        } else if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE) {
          handleAggressive(creature);
        } else if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.KEEP_DISTANCE) {
          handleKeepDistance(game, creature);
        }
      }

      float randomTime = 1f + Math.abs(creature.getParams().getRandomGenerator().nextFloat());
      creature.getEnemyParams().setAutoControlsStateProcessorTime(randomTime);
    }
  }

  private void handleAggressive(Creature creature) {
    if (!creature.getEnemyParams().isBossEnemy() &&
      Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.35f) {
      creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.KEEP_DISTANCE);

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
        creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
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
      creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
    }
  }

}
