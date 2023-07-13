package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.Enemy;
import com.easternsauce.actionrpg.model.creature.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class ShieldGuard extends AttachedAbility {

    AbilityParams params;

    public static ShieldGuard of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        float flipValue = abilityParams.getDirVector().angleDeg();

        ShieldGuard ability = ShieldGuard.of();
        ability.params = abilityParams
            .setWidth(2f)
            .setHeight(2f)
            .setChannelTime(0f)
            .setActiveTime(3f)
            .setStartingRange(1.2f)
            .setTextureName("shield")
            .setBaseDamage(0f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(false)
            .setIsFlip(ShieldGuard.calculateFlip(flipValue));
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
    protected void onActiveUpdate(float delta, CoreGame game) {
        updateAttachedAbilityPosition(game);

    }

    @Override
    public void init(CoreGame game) {

        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        updateAttachedAbilityPosition(game);

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {
        Ability otherAbility = game.getGameState().accessAbilities().getAbility(otherAbilityId);

        if (otherAbility != null) {
            Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());
            Creature abilityOwner = game.getGameState().accessCreatures().getCreature(otherAbility
                .getParams()
                .getCreatureId());

            if ((creature instanceof Player && abilityOwner instanceof Enemy ||
                creature instanceof Enemy && abilityOwner instanceof Player) && otherAbility.isRanged()) {
                if (otherAbility.isBlockable()) {
                    otherAbility.getParams().setIsMarkedAsShielded(true);
                }

                if (otherAbility instanceof RicochetBullet) {
                    otherAbility.onTerrainHit(otherAbility.getParams().getPos(), getParams().getPos());

                } else if (otherAbility instanceof Boomerang) {
                    otherAbility.onCreatureHit(getParams().getCreatureId(), game);
                } else {
                    if (!(abilityOwner instanceof Player)) {
                        otherAbility.deactivate();
                    }
                }

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
