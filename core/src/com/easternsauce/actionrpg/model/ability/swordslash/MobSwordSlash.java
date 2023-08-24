package com.easternsauce.actionrpg.model.ability.swordslash;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MobSwordSlash extends SwordSlashBase {
    @Getter
    protected AbilityParams params;

    public static MobSwordSlash of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        MobSwordSlash ability = MobSwordSlash.of();
        ability.params = abilityParams
            .setWidth(2f)
            .setHeight(2f)
            .setChannelTime(0.15f)
            .setActiveTime(0.3f)
            .setStartingRange(1.8f)
            .setTextureName("slash")
            .setBaseDamage(35f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false);
        return ability;
    }

}
