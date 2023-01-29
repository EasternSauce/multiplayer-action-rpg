package com.mygdx.game.ability;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class FireballExplosion extends Ability {
    AbilityParams params;

    public static FireballExplosion of(AbilityParams params) {
        FireballExplosion ability = FireballExplosion.of();
        ability.params = params;
        return ability;
    }

}
