package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SwordSlash extends Ability {

    AbilityParams params;

    public static SwordSlash of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        SwordSlash ability = SwordSlash.of();
        ability.params = abilityParams.setWidth(2f)
                .setHeight(2f)
                .setChannelTime(0.15f)
                .setActiveTime(0.3f)
                .setRange(1.8f)
                .setTextureName("slash")
                .setBaseDamage(22f)
                .setIsChannelAnimationLooping(false)
                .setIsActiveAnimationLooping(false)
                .setRotationShift(0f);
        return ability;
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
    void onAbilityStarted(CoreGame game) {

    }

    @Override
    void onDelayedAction(CoreGame game) {

    }

    @Override
    void onAbilityCompleted(CoreGame game) {

    }

    @Override
    public void updatePosition(CoreGame game) {
        Vector2 dirVector;
        if (getParams().getDirVector().len() <= 0) {
            dirVector = Vector2.of(1, 0);
        } else {
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
    void onChannelUpdate(CoreGame game) {
        updatePosition(game);


    }

    @Override
    void onActiveUpdate(CoreGame game) {

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

    }

    @Override
    public Float getStunDuration() {
        return 0.35f;
    }
}
