package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.util.Vector2;

// actions are sent to clients immediately once they happen on server side to be applied to client game state
public abstract class GameStateAction {
  public abstract void applyToGame(CoreGame game);

  public final Vector2 getActionObjectPos(CoreGame game) {
    if (getOverridePos() != null) {
      return getOverridePos();
    }
    Entity entity = getEntity(game);
    if (entity == null) {
      throw new RuntimeException("calling action on non-existent object");
    }
    return getEntity(game).getParams().getPos();
  }

  protected Vector2 getOverridePos() {
    return null;
  }

  public abstract Entity getEntity(CoreGame game);

  public final EntityId<Area> getActionObjectAreaId(CoreGame game) {
    if (!getOverrideAreaId().isEmpty()) {
      return getOverrideAreaId();
    }
    Entity entity = getEntity(game);
    if (entity == null) {
      throw new RuntimeException("calling action on non-existent object");
    }
    return getEntity(game).getParams().getAreaId();
  }

  protected EntityId<Area> getOverrideAreaId() {
    return NullAreaId.of();
  }

  public boolean isActionObjectValid(CoreGame game) {
    return (getEntity(game) != null) || (getOverridePos() != null && !getOverrideAreaId().isEmpty());
  }
}
