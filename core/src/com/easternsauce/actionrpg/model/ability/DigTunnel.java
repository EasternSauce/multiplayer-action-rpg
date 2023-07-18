package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Enemy;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.model.util.MathHelper;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class DigTunnel extends Projectile {
    @Getter
    private AbilityParams params;

    private int currentSplash = 0;

    public static DigTunnel of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        DigTunnel ability = DigTunnel.of();
        ability.params = abilityParams
            .setNoTexture(true)
            .setWidth(1.5f)
            .setHeight(1.5f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setBaseDamage(0f)
            .setStartingRange(0.5f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setSpeed(9f)
            .setMaximumRange(30f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {
        onProjectileTravelUpdate();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        onProjectileTravelUpdate();

        Creature minimumDistanceCreature = null;
        float minimumDistance = Float.MAX_VALUE;

        Creature thisCreature = game.getCreature(getParams().getCreatureId());

        for (Creature creature : game.getCreatures().values().stream().filter(targetCreature -> Objects.equals(targetCreature.getParams().getAreaId().getValue(),
            getParams().getAreaId().getValue()
        ) &&
            !targetCreature.getId().equals(getParams().getCreatureId()) &&
            targetCreature.isAlive() &&
            isTargetingAllowed(thisCreature, targetCreature) &&
            targetCreature.getParams().getPos().distance(getParams().getPos()) < 20f).collect(Collectors.toSet())) {
            if (creature.getParams().getPos().distance(getParams().getPos()) < minimumDistance) {
                minimumDistanceCreature = creature;
                minimumDistance = creature.getParams().getPos().distance(getParams().getPos());
            }
        }

        if (minimumDistanceCreature != null) {
            Vector2 vectorTowards = getParams().getPos().vectorTowards(minimumDistanceCreature.getParams().getPos());
            float targetAngleDeg = vectorTowards.angleDeg();
            float currentAngleDeg = getParams().getDirVector().angleDeg();

            float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentAngleDeg, targetAngleDeg);

            float incrementFactor = 120f;
            float baseIncrement = incrementFactor;

            if (getParams().getStateTimer().getTime() > 0.5f && getParams().getStateTimer().getTime() < 6f) {
                baseIncrement = incrementFactor -
                    (getParams().getStateTimer().getTime() - 0.5f) / 5.5f * incrementFactor;
            } else if (getParams().getStateTimer().getTime() >= 6f) {
                baseIncrement = 0f;
            }

            float increment = baseIncrement * delta;

            if (shortestAngleRotation > increment) {
                getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(increment));
            } else if (shortestAngleRotation < -increment) {
                getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(-increment));
            } else {
                getParams().setDirVector(getParams().getDirVector().withSetDegAngle(targetAngleDeg));
            }

        }

        if (getParams().getStateTimer().getTime() > currentSplash * 0.2f) {
            game.chainAnotherAbility(this,
                AbilityType.DIG_TUNNEL_SPLASH,
                thisCreature.getParams().getMovementParams().getFacingVector(),
                ChainAbilityParams.of().setChainToPos(getParams().getPos())
            );
            currentSplash += 1;
        }

    }

    private boolean isTargetingAllowed(Creature thisCreature, Creature targetCreature) {
        if (thisCreature instanceof Enemy) {
            return targetCreature instanceof Player;
        }
        //noinspection RedundantIfStatement
        if (thisCreature instanceof Player) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCompleted(CoreGame game) {
        Creature creature = game.getCreature(getParams().getCreatureId());

        game.chainAnotherAbility(this,
            AbilityType.DIG_TUNNEL_EXPLOSION,
            creature.getParams().getMovementParams().getFacingVector(),
            ChainAbilityParams.of().setChainToPos(getParams().getPos())
        );
    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        deactivate();
    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        if (getParams().getStateTimer().getTime() > 0.1f) {
            deactivate();
        }
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean canStun() {
        return false;
    }
}
