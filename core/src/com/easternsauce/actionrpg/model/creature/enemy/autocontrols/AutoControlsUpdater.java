package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class AutoControlsUpdater {
  private final AutoControlsStateProcessor stateProcessor = AutoControlsStateProcessor.of();
  private final AutoControlsPathfindingProcessor pathfindingProcessor = AutoControlsPathfindingProcessor.of();
  private final AutoControlsTargetProcessor targetProcessor = AutoControlsTargetProcessor.of();

  public void update(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.isAlive()) {
      stateProcessor.process(creatureId, game);
      targetProcessor.process(creatureId, game);
      pathfindingProcessor.process(creatureId, game);
    }
  }
}
