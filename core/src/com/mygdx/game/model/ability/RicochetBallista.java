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
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(0f);


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

        Vector2 leftSidePos = getParams().getPos().add(params.getDirVector().normalized().multiplyBy(1f).rotateDeg(90));
        Vector2 rightSidePos =
                getParams().getPos().add(params.getDirVector().normalized().multiplyBy(1f).rotateDeg(-90));

        game.chainAbility(this, AbilityType.RICOCHET_BULLET, getParams().getPos(), params.getDirVector());
        game.chainAbility(this, AbilityType.RICOCHET_BULLET, leftSidePos, params.getDirVector());
        game.chainAbility(this, AbilityType.RICOCHET_BULLET, rightSidePos, params.getDirVector());
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
