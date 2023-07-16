package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class ChainAbilityParams {
    private Vector2 chainToPos;
    private Float overrideSize;
    private Float overrideDuration;
    private Float overrideDamage;
}
