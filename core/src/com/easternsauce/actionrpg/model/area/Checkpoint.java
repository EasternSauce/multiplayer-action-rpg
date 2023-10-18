package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.id.CheckpointId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class Checkpoint {
  @Getter
  private final Float width = 2f;
  @Getter
  private final Float height = 2f;
  @Getter
  private CheckpointId checkpointId;
  @Getter
  private AreaId areaId;
  @Getter
  private Vector2 pos;
}
