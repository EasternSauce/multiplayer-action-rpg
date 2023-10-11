package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsStateLogicProcessor {
  public void processStateLogic(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.getEnemyParams().getTargetCreatureId() == null) {
      return;
    }
    if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.ALERTED) {
      processAlerted(game, creature);
    } else if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE) {
      processAggressive(creature);
    } else if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.KEEP_DISTANCE) {
      processKeepDistance(game, creature);
    }

  }

  private void processAggressive(Creature creature) {
    if (!creature.getEnemyParams().isBossEnemy() &&
      Math.abs(creature.getParams().getRandomGenerator().nextFloat()) < 0.35f) {
      creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.KEEP_DISTANCE);

    }
  }

  private void processKeepDistance(CoreGame game, Creature creature) {
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

  private void processAlerted(CoreGame game, Creature creature) {
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

  public void processDistanceLogic(CreatureId creatureId, Creature potentialTarget, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    Float distanceToTarget = creature.getParams().getPos().distance(potentialTarget.getParams().getPos());

    if (creature.getEnemyParams().getJustAttackedFromRangeTimer().getTime() >=
      Constants.JUST_ATTACKED_FROM_RANGE_AGGRESSION_TIME) {
      if ((creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE ||
        creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.KEEP_DISTANCE) &&
        distanceToTarget > Constants.TURN_ALERTED_DISTANCE) {
        if (creature.getEnemyParams().isBossEnemy()) {
          creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
        } else {
          creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.ALERTED);
        }

      } else if (creature.getEnemyParams().getAutoControlsState() == EnemyAutoControlsState.ALERTED &&
        distanceToTarget < Constants.TURN_AGGRESSIVE_DISTANCE) {
        creature.getEnemyParams().setAutoControlsState(EnemyAutoControlsState.AGGRESSIVE);
      }
    }
  }
}
