package com.easternsauce.actionrpg.model.creature.enemy.autocontrols.targetprocessor;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.AutoControlsState;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.EnemyRetriever;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class TargetProcessor extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  private TargetPickedProcessor targetFoundProcessor;

  public static TargetProcessor of(EntityId<Creature> enemyId) {
    TargetProcessor targetProcessor = TargetProcessor.of();

    targetProcessor.enemyId = enemyId;
    targetProcessor.targetFoundProcessor = TargetPickedProcessor.of(enemyId);

    return targetProcessor;
  }

  public void process(CoreGame game) {
    lookForPotentialTarget(game);

    targetFoundProcessor.process(game);
  }

  private void lookForPotentialTarget(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    EntityId<Creature> justAttackedByCreatureId = enemyParams.getJustAttackedByCreatureId();

    boolean justAttackedByPlayer = wasEnemyJustAttackedByPlayer(game);

    if (justAttackedByPlayer) {
      enemyParams.setAggroedCreatureId(justAttackedByCreatureId);
    } else {
      searchAroundForTargets(game, enemyParams);
    }
  }

  public boolean wasEnemyJustAttackedByPlayer(CoreGame game) {
    Creature enemy = getEnemy(game);

    EnemyParams enemyParams = enemy.getEnemyParams();

    EntityId<Creature> justAttackedByCreatureId = enemyParams.getJustAttackedByCreatureId();

    return !justAttackedByCreatureId.isEmpty() && game.getCreature(justAttackedByCreatureId) instanceof Player;
  }

  private void searchAroundForTargets(CoreGame game, EnemyParams enemyParams) {
    EntityId<Creature> foundTargetId;

    if (enemyParams.getFindTargetTimer().getTime() > enemyParams.getFindTargetCooldown()) {
      enemyParams.getFindTargetTimer().restart();

      foundTargetId = findTarget(game);
    } else {
      foundTargetId = enemyParams.getLastFoundTargetId();
    }

    if (!foundTargetId.isEmpty()) {
      if (enemyParams.getLastFoundTargetId().isEmpty() || !enemyParams.getLastFoundTargetId().equals(foundTargetId)) {
        if (enemyParams.isBossEnemy()) {
          enemyParams.setAutoControlsState(AutoControlsState.AGGRESSIVE);
        } else {
          enemyParams.setAutoControlsState(AutoControlsState.ALERTED);
        }

        enemyParams.setAggroedCreatureId(foundTargetId);
        enemyParams.setLastFoundTargetId(foundTargetId);
      }

    }
  }




  public EntityId<Creature> findTarget(CoreGame game) {
    Creature enemy = getEnemy(game);

    if (enemy.isEmpty()) {
      return NullCreatureId.of();
    } else {
      Float minDistance = Float.MAX_VALUE;
      EntityId<Creature> minCreatureId = NullCreatureId.of();
      for (Creature otherCreature : game.getActiveCreatures().values()) {
        boolean condition = otherCreature.isAlive() && otherCreature.getParams().getAreaId().getValue().equals(enemy.getParams().getAreaId().getValue()) && otherCreature instanceof Player && otherCreature.getParams().getPos().distance(enemy.getParams().getPos()) < Constants.ENEMY_SEARCH_DISTANCE && !game.isLineBetweenPointsObstructedByTerrain(enemy.getParams().getAreaId(), enemy.getParams().getPos(), otherCreature.getParams().getPos());

        if (condition && enemy.getParams().getPos().distance(otherCreature.getParams().getPos()) < minDistance) {
          minCreatureId = otherCreature.getId();
          minDistance = enemy.getParams().getPos().distance(otherCreature.getParams().getPos());
        }
      }
      return minCreatureId;
    }
  }

}
