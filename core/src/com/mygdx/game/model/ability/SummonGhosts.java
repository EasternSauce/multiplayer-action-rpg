package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SummonGhosts extends Ability {
    AbilityParams params;

    public static SummonGhosts of(AbilityInitialParams abilityInitialParams) {
        SummonGhosts ability = SummonGhosts.of();
        ability.params = AbilityParams.of(abilityInitialParams)

                                      .channelTime(0f).activeTime(0f);


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(AbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(AbilityChainable game) {
        float baseAngle = params().dirVector().angleDeg();
        game.chainAbility(this, AbilityType.PLAYFUL_GHOST, params().pos(), null, null, null, params.dirVector(), null);
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          null,
                          null,
                          0f,
                          params.dirVector().setAngleDeg(baseAngle - 30f),
                          null);
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          null,
                          null,
                          0f,
                          params.dirVector().setAngleDeg(baseAngle + 30f),
                          null);
    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    void onChannelUpdate(CreaturePosRetrievable game) {

    }

    @Override
    void onActiveUpdate(AbilityUpdateable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }
}
