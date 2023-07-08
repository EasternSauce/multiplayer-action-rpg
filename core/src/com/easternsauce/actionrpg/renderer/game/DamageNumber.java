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
    Vector2 pos;
    AreaId areaId;
    Float damageValue;
    Float damageTime;
    Float colorR;
    Float colorG;
    Float colorB;
}
