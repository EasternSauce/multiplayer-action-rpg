package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.targetprocessor.TargetSearchProcessor;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class AutoControls extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  private StateProcessor stateProcessor;
  private PathfindingProcessor pathfindingProcessor;
  private TargetSearchProcessor targetSearchProcessor;

  public static AutoControls of(EntityId<Creature> enemyId) {
    AutoControls autoControls = AutoControls.of();

    autoControls.enemyId = enemyId;
    autoControls.stateProcessor = StateProcessor.of(enemyId);
    autoControls.pathfindingProcessor = PathfindingProcessor.of(enemyId);
    autoControls.targetSearchProcessor = TargetSearchProcessor.of(enemyId);

    return autoControls;
  }

  public void update(CoreGame game) {
    Creature enemy = game.getCreature(enemyId);

    if (enemy.isAlive()) {
      stateProcessor.process(game);
      targetSearchProcessor.process(game);
      pathfindingProcessor.process(game);
    }
  }
}
