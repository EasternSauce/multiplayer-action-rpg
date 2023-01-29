package com.mygdx.game.ability;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Projectile extends Ability {
    AbilityParams params;

}
