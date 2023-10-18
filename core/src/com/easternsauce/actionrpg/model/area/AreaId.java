package com.easternsauce.actionrpg.model.area;

import com.easternsauce.actionrpg.model.id.EntityId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = false)
public class AreaId extends EntityId {
    @Getter
    private String value;
}
