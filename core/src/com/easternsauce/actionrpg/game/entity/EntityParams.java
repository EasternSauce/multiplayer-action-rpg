package com.easternsauce.actionrpg.game.entity;

import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;

public interface EntityParams {
  Vector2 getPos();

  EntityId<Area> getAreaId();
}
