package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class ChainAbilityParams {
    Vector2 chainToPos;
    Float overrideSize;
    Float overrideDuration;
    Float overrideDamage;
}
