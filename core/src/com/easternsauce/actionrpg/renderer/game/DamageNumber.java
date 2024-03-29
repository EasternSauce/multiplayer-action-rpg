package com.easternsauce.actionrpg.renderer.game;

import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class DamageNumber {
  private Vector2 pos;
  private EntityId<Area> areaId = NullAreaId.of();
  private Float damageValue;
  private Float damageTime;
  private Float colorR;
  private Float colorG;
  private Float colorB;
}
