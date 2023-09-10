package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayerMoveTowardsTargetAction extends GameStateAction {
  private CreatureId playerId;

  private Vector2 mousePos;

  public static PlayerMoveTowardsTargetAction of(CreatureId creatureId, Vector2 mousePos) {
    PlayerMoveTowardsTargetAction action = PlayerMoveTowardsTargetAction.of();
    action.playerId = creatureId;
    action.mousePos = mousePos;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature creature = game.getCreature(playerId);

    creature.getParams().setLastTimeMoved(game.getGameState().getTime());

    if (creature.isAlive() && !creature.isStunned(game)) {
      Vector2 pos = creature.getParams().getPos();

      creature.moveTowards(pos.add(mousePos));

      creature.getParams().getMovementParams().setPreviousPos(creature.getParams().getPos());
      creature.getParams().getMovementParams().getStillMovingCheckTimer().restart();
    }
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
