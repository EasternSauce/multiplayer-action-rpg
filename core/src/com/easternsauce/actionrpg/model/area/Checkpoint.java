package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.game.entity.EntityParams;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Checkpoint implements Entity {
  @Getter
  private final Float width = 2f;
  @Getter
  private final Float height = 2f;
  @Getter
  private EntityId<Checkpoint> checkpointId;
  @Getter
  private EntityId<Area> areaId;
  @Getter
  private Vector2 pos;

  @Override
  public EntityParams getParams() {
    return null; // TODO: move params to here?
  }
}
