package com.easternsauce.actionrpg.model.ability.swordslash;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class BossSwordSlash extends SwordSlashBase {
    public static BossSwordSlash of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        BossSwordSlash ability = BossSwordSlash.of();
        ability.params = abilityParams
            .setWidth(4.5f)
            .setHeight(4.5f)
            .setChannelTime(0.15f)
            .setActiveTime(0.3f)
            .setStartingRange(4f)
            .setTextureName("slash")
            .setBaseDamage(35f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false);
        return ability;
    }

    @Override
    public Float getStunDuration() {
        return 1.2f;
    }
}
