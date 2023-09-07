package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CreatureRespawnAction extends GameStateAction {
  private CreatureId creatureId;
  private Vector2 pos;
  private AreaId areaId;

  public static CreatureRespawnAction of(CreatureId creatureId, Vector2 pos, AreaId areaId) {
    CreatureRespawnAction action = CreatureRespawnAction.of();
    action.creatureId = creatureId;
    action.pos = pos;
    action.areaId = areaId;
    return action;
  }

  public void applyToGame(CoreGame game) {

    Creature creature = game.getCreature(creatureId);


    creature.getParams().setAwaitingRespawn(false);
    creature.getParams().setDead(false);
    creature.getParams().getStats().setLife(creature.getParams().getStats().getMaxLife());
    creature.getParams().getStats().setStamina(creature.getParams().getStats().getMaxStamina());
    creature.getParams().getStats().setMana(creature.getParams().getStats().getMaxMana());

    creature.getParams().getEffectParams().getEffects()
      .forEach((creatureEffect, creatureEffectState) -> creatureEffectState.terminateEffect());

    creature.getParams().setPos(pos);
    game.addTeleportEvent(TeleportEvent.of(creatureId, pos, creature.getParams().getAreaId(), areaId, false));


  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(creatureId);
  }
}
