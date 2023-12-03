package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.targetprocessor.TargetProcessor;
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
  private TargetProcessor targetProcessor;

  public static AutoControls of(EntityId<Creature> enemyId) {
    AutoControls autoControls = AutoControls.of();

    autoControls.enemyId = enemyId;
    autoControls.stateProcessor = StateProcessor.of(enemyId);
    autoControls.pathfindingProcessor = PathfindingProcessor.of(enemyId);
    autoControls.targetProcessor = TargetProcessor.of(enemyId);

    return autoControls;
  }

  public void update(CoreGame game) {
    Creature enemy = game.getCreature(enemyId);

    if (enemy.isAlive()) {
      stateProcessor.process(game);
      targetProcessor.process(game);
      pathfindingProcessor.process(game);
    }
  }
}
