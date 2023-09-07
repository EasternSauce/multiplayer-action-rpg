package com.easternsauce.actionrpg.game.entity;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.Vector2;

public interface EntityParams {
  Vector2 getPos();

  AreaId getAreaId();
}
