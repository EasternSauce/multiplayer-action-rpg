package com.easternsauce.actionrpg.model.creature.effect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureEffectState {
    Float startTime = 0f;
    Float duration = 0f;
}
