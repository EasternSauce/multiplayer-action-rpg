package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SummonShield extends Ability {

    AbilityParams params;

    public static SummonShield of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        float flipValue = abilityParams.getDirVector().angleDeg();

        SummonShield ability = SummonShield.of();
        ability.params = abilityParams
            .setWidth(2f)
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
    public void init(CoreGame game) {

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
    public void onAbilityStarted(CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {

    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {

    }

    @Override
    public void updatePosition(CoreGame game) {
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

        Vector2 pos = game.getGameState().accessCreatures().getCreaturePos(getParams().getCreatureId());

        if (pos != null) {
            float attackRectX = attackShiftX + pos.getX();
            float attackRectY = attackShiftY + pos.getY();

            getParams().setPos(Vector2.of(attackRectX, attackRectY));
            getParams().setRotationAngle(theta);
        }
    }

    @Override
    public void onChannelUpdate(CoreGame game) {
        updatePosition(game);

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updatePosition(game);

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {
        Ability otherAbility = game.getGameState().accessAbilities().getAbility(otherAbilityId);
        if (otherAbility != null) {
            if (otherAbility.isRanged()) {
                otherAbility.deactivate();
            }
        }
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean isCanBeDeactivated() {
        return true;
    }

    @Override
    public boolean isCanStun() {
        return false;
    }
}
