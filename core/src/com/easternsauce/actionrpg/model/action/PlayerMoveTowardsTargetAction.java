package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PlayerMoveTowardsTargetAction extends GameStateAction {
  private EntityId<Creature> playerId;

  private Vector2 mousePos;

  public static PlayerMoveTowardsTargetAction of(EntityId<Creature> playerId, Vector2 mousePos) {
    PlayerMoveTowardsTargetAction action = PlayerMoveTowardsTargetAction.of();
    action.playerId = playerId;
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
    Map<EntityId<Creature>, Creature> allCreatures = game.getAllCreatures();
    Creature creature = game.getCreature(playerId);
    return creature;
  }
}
