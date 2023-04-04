package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class RicochetBallista extends Ability {
    AbilityParams params;

    public static RicochetBallista of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        RicochetBallista ability = RicochetBallista.of();
        ability.params =
                abilityParams
                        .channelTime(0f)
                        .activeTime(0f);


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(AbilityUpdatable game) {

    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {

        Vector2 leftSidePos = params().pos().add(params.dirVector().normalized().multiplyBy(1f).rotateDeg(90));
        Vector2 rightSidePos = params().pos().add(params.dirVector().normalized().multiplyBy(1f).rotateDeg(-90));

        game.chainAbility(this,
                          AbilityType.RICOCHET_BULLET,
                          params().pos(),
                          params.dirVector());
        game.chainAbility(this, AbilityType.RICOCHET_BULLET, leftSidePos, params.dirVector());
        game.chainAbility(this, AbilityType.RICOCHET_BULLET, rightSidePos, params.dirVector());
    }


    @Override
    void onChannelUpdate(AbilityUpdatable game) {

    }

    @Override
    void onActiveUpdate(AbilityUpdatable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }
}
