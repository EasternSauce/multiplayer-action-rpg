package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Dash extends Ability {
    AbilityParams params;

    public static Dash of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(abilityParams.getCreatureId());

        Dash ability = Dash.of();
        ability.params = abilityParams
            .setWidth(2.5f)
            .setHeight(2.5f)
            .setChannelTime(0f)
            .setActiveTime(0.14f)
            .setTextureName("warp")
            .setBaseDamage(0f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(false)
            .setRotationShift(0f)
            .setPos(creature.getParams().getPos())
            .setRange(1.8f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    public void onStarted(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        creature.getParams().setIsDashing(true);
        creature.getParams().setDashingVector(getParams().getDirVector());
        creature.getParams().setDashingVelocity(30f);
    }

    @Override
    protected void onCompleted(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        creature.getParams().setIsDashing(false);
    }

    public void updatePosition(CoreGame game) {
        Vector2 dirVector;
        if (getParams().getDirVector().len() <= 0) {
            dirVector = Vector2.of(1, 0);
        }
        else {
            dirVector = getParams().getDirVector().withSetDegAngle(getParams().getDirVector().angleDeg() + 180);
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
    public void init(CoreGame game) {
        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updatePosition(game);
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

    @Override
    public Boolean isPositionChangedOnUpdate() {
        return true;
    }
}
