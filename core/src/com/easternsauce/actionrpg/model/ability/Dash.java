package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Dash extends DirectionalAttachedAbility {
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
            .setPos(creature.getParams().getPos())
            .setRange(1.8f)
            .setDirectionalAttachedAbilityRotationShift(180f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    public void onStarted(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        creature.getParams().getMovementParams().setIsDashing(true);
        creature.getParams().getMovementParams().setDashingVector(getParams().getDirVector());
        creature.getParams().getMovementParams().setDashingVelocity(30f);
    }

    @Override
    protected void onCompleted(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        creature.getParams().getMovementParams().setIsDashing(false);
    }

    @Override
    public void init(CoreGame game) {
        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updateDirectionalAttachedAbilityPosition(game);
    }

    @Override
    public void onChannelUpdate(CoreGame game) {
        updateDirectionalAttachedAbilityPosition(game);
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        updateDirectionalAttachedAbilityPosition(game);
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
