package com.easternsauce.actionrpg.renderer.game;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class DamageNumber {
    private Vector2 pos;
    private AreaId areaId;
    private Float damageValue;
    private Float damageTime;
    private Float colorR;
    private Float colorG;
    private Float colorB;
}
