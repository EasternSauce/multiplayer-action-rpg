package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class RicochetShot extends Ability {
    AbilityParams params;

    public static RicochetShot of(AbilityInitialParams abilityInitialParams) {
        RicochetShot ability = RicochetShot.of();
        ability.params =
                AbilityParams.of(abilityInitialParams)
                             .channelTime(0f)
                             .activeTime(0f);


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

        Vector2 leftSidePos = params().pos().add(params.dirVector().normalized().multiplyBy(2f).rotateDeg(90));
        Vector2 rightSidePos = params().pos().add(params.dirVector().normalized().multiplyBy(2f).rotateDeg(-90));

        game.chainAbility(this,
                          AbilityType.RICOCHET_BULLET,
                          params().pos(),
                          null,
                          null,
                          0f,
                          params.dirVector(),
                          null);
        game.chainAbility(this, AbilityType.RICOCHET_BULLET, leftSidePos, null, null, null, params.dirVector(), null);
        game.chainAbility(this, AbilityType.RICOCHET_BULLET, rightSidePos, null, null, null, params.dirVector(), null);
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
