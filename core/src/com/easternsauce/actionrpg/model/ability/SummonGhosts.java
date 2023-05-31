package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.util.Ability;
import com.easternsauce.actionrpg.model.ability.util.AbilityParams;
import com.easternsauce.actionrpg.model.ability.util.AbilityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SummonGhosts extends Ability {
    AbilityParams params;

    public static SummonGhosts of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        SummonGhosts ability = SummonGhosts.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(0f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

    }

    @Override
    protected void onCompleted(CoreGame game) {
        float baseAngle = getParams().getDirVector().angleDeg();
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this, AbilityType.PLAYFUL_GHOST, getParams().getPos(), params.getDirVector(), null, null, game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.PLAYFUL_GHOST,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle - 30f),
                                 null,
                                 null,
                                 game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.PLAYFUL_GHOST,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 30f),
                                 null,
                                 null,
                                 game);
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean usesEntityModel() {
        return false;
    }
}
