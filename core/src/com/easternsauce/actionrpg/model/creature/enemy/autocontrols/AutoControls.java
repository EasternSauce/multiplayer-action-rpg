package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.autocontrols.targetprocessor.AutoControlsTargetProcessor;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class AutoControls extends EnemyRetriever {
  @Getter(value = AccessLevel.PROTECTED)
  private EntityId<Creature> enemyId;

  private AutoControlsStateProcessor stateProcessor;
  private AutoControlsPathfindingProcessor pathfindingProcessor;
  private AutoControlsTargetProcessor targetProcessor;

  public static AutoControls of(EntityId<Creature> enemyId) {
    AutoControls autoControls = AutoControls.of();

    autoControls.enemyId = enemyId;
    autoControls.stateProcessor = AutoControlsStateProcessor.of(enemyId);
    autoControls.pathfindingProcessor = AutoControlsPathfindingProcessor.of(enemyId);
    autoControls.targetProcessor = AutoControlsTargetProcessor.of(enemyId);

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
