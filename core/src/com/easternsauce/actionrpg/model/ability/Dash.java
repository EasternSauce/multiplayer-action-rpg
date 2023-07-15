package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Dash extends AttachedAbility {
    AbilityParams params;

    public static Dash of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getCreature(abilityParams.getCreatureId());

        float flipValue = abilityParams.getDirVector().angleDeg();

        Dash ability = Dash.of();
        ability.params = abilityParams
            .setWidth(5.5f)
            .setHeight(5.5f)
            .setChannelTime(0f)
            .setActiveTime(0.14f)
            .setTextureName("smoke")
            .setBaseDamage(0f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(false)
            .setPos(creature.getParams().getPos())
            .setStartingRange(0.8f)
            .setDirectionalAttachedAbilityRotationShift(180f)
            .setIsFlip(Dash.calculateFlip(flipValue))
            .setRotationShift(180f);

        return ability;
    }

    private static Boolean calculateFlip(Float rotationAngle) {
        return rotationAngle >= 90 && rotationAngle < 270;
    }

    @Override
    public Boolean isPositionChangedOnUpdate() {
        return true;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {
        updateAttachedAbilityPosition(game);
    }

    @Override
    public void onStarted(CoreGame game) {
        Creature creature = game.getCreature(getParams().getCreatureId());

        creature.getParams().getMovementParams().setIsDashing(true);
        creature.getParams().getMovementParams().setDashingVector(getParams().getDirVector());
        creature.getParams().getMovementParams().setDashingVelocity(30f);
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updateAttachedAbilityPosition(game);
    }

    @Override
    protected void onCompleted(CoreGame game) {
        Creature creature = game.getCreature(getParams().getCreatureId());

        creature.getParams().getMovementParams().setIsDashing(false);
    }

    @Override
    public void init(CoreGame game) {
        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updateAttachedAbilityPosition(game);
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
