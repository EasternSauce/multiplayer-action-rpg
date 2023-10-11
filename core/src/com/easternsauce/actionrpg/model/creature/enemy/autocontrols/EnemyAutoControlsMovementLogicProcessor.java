package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsMovementLogicProcessor {
  public void process(CreatureId creatureId, Creature potentialTarget, Float distance, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    EnemyParams enemyParams = creature.getEnemyParams();
    if (enemyParams.getAutoControlsState() == EnemyAutoControlsState.AGGRESSIVE) {
      processAggressive(creatureId, potentialTarget, distance, game, creature, enemyParams);
    } else if (enemyParams.getAutoControlsState() == EnemyAutoControlsState.ALERTED) {
      processAlerted(creatureId, game, creature, enemyParams);
    } else if (enemyParams.getAutoControlsState() == EnemyAutoControlsState.KEEP_DISTANCE) {
      processKeepDistance(creatureId, game, creature, enemyParams);
    } else {
      creature.stopMoving();
    }
  }

  private void processKeepDistance(CreatureId creatureId, CoreGame game, Creature creature, EnemyParams enemyParams) {
    creature.getParams().getStats().setSpeed(creature.getParams().getStats().getBaseSpeed() / 2);
    if (enemyParams.getCurrentDefensivePos() != null) {
      goToPos(creatureId, enemyParams.getCurrentDefensivePos(), game);
    }
  }

  private void processAlerted(CreatureId creatureId, CoreGame game, Creature creature, EnemyParams enemyParams) {
    creature.getParams().getStats().setSpeed(creature.getParams().getStats().getBaseSpeed() / 3);
    if (enemyParams.getCurrentDefensivePos() != null) {
      goToPos(creatureId, enemyParams.getCurrentDefensivePos(), game);
    }
  }

  private void processAggressive(CreatureId creatureId, Creature potentialTarget, Float distance, CoreGame game, Creature creature, EnemyParams enemyParams) {
    if (distance > enemyParams.getWalkUpRange() - 1f) {
      creature.getParams().getStats().setSpeed(creature.getParams().getStats().getBaseSpeed());
      goToPos(creatureId, potentialTarget.getParams().getPos(), game);
    } else { // if no path or distance is small, then stop moving
      creature.stopMoving();
    }
  }

  public void goToPos(CreatureId creatureId, Vector2 pos, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creature.getParams().setLastTimeMoved(game.getGameState().getTime());

    if (!creature.isStunned(game)) {
      creature.moveTowards(pos);
    }
  }
}
