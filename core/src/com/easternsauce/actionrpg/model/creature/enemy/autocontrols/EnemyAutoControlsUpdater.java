package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.CreatureId;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class EnemyAutoControlsUpdater {
  private final EnemyAutoControlsStateProcessor stateProcessor = EnemyAutoControlsStateProcessor.of();
  private final EnemyAutoControlsPathfindingProcessor pathfindingProcessor = EnemyAutoControlsPathfindingProcessor.of();
  private final EnemyAutoControlsTargetProcessor targetProcessor = EnemyAutoControlsTargetProcessor.of();

  public void update(CreatureId creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.isAlive()) {
      stateProcessor.process(creatureId, game);
      targetProcessor.process(creatureId, game);
      pathfindingProcessor.process(creatureId, game);
    }
  }
}
