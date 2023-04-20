package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SummonShield extends Ability {

    AbilityParams params;

    public static SummonShield of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        float flipValue = abilityParams.getDirVector().angleDeg();

        SummonShield ability = SummonShield.of();
        ability.params = abilityParams.setWidth(2f)
                                      .setHeight(2f)
                                      .setChannelTime(0f)
                                      .setActiveTime(1f)
                                      .setRange(1.2f)
                                      .setTextureName("shield")
                                      .setBaseDamage(0f)
                                      .setIsChannelAnimationLooping(false)
                                      .setIsActiveAnimationLooping(false)
                                      .setRotationShift(0f)
                                      .setIsFlip(SummonShield.calculateFlip(flipValue));
        return ability;
    }

    private static Boolean calculateFlip(Float rotationAngle) {
        return rotationAngle >= 90 && rotationAngle < 270;
    }

    @Override
    public void init(GameActionApplicable game) {

        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updatePosition(game);

    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    public Boolean isPositionChangedOnUpdate() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {

    }

    @Override
    public void updatePosition(AbilityUpdatable game) {
        Vector2 dirVector;
        if (getParams().getDirVector().len() <= 0) {
            dirVector = Vector2.of(1, 0);
        }
        else {
            dirVector = getParams().getDirVector();
        }

        Float theta = dirVector.angleDeg();

        float attackShiftX = dirVector.normalized().getX() * getParams().getRange();
        float attackShiftY = dirVector.normalized().getY() * getParams().getRange();

        Vector2 pos = game.getCreaturePos(getParams().getCreatureId());

        if (pos != null) {
            float attackRectX = attackShiftX + pos.getX();
            float attackRectY = attackShiftY + pos.getY();

            getParams().setPos(Vector2.of(attackRectX, attackRectY));
            getParams().setRotationAngle(theta);
        }
    }

    @Override
    void onChannelUpdate(AbilityUpdatable game) {
        updatePosition(game);

    }

    @Override
    void onActiveUpdate(AbilityUpdatable game) {
        updatePosition(game);

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
        Ability otherAbility = game.getAbility(otherAbilityId);
        if (otherAbility != null) {
            if (otherAbility.isRanged()) {
                otherAbility.deactivate();
            }
        }
    }
}
