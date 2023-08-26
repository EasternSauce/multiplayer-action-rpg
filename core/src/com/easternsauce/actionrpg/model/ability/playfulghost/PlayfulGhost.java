package com.easternsauce.actionrpg.model.ability.playfulghost;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.creature.Creature;
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
public class PlayfulGhost extends Projectile {
    @Getter
    protected AbilityParams params;

    public static PlayfulGhost of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        PlayfulGhost ability = PlayfulGhost.of();
        ability.params = abilityParams
            .setWidth(1.5f)
            .setHeight(1.5f)
            .setChannelTime(0f)
            .setActiveTime(10f)
            .setTextureName("ghost")
            .setBaseDamage(15f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setSpeed(5f);

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
        getParams().setFlip(getParams().getRotationAngle() >= 90 && getParams().getRotationAngle() < 270);

        Creature minCreature = null;
        float minDistance = Float.MAX_VALUE;

        Creature thisCreature = game.getCreature(getParams().getCreatureId());

        for (Creature creature : game
            .getGameState()
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(targetCreature -> Objects.equals(targetCreature.getParams().getAreaId().getValue(),
                getParams().getAreaId().getValue()
            ) &&
                !targetCreature.getId().equals(getParams().getCreatureId()) &&
                targetCreature.isAlive() &&
                isTargetingAllowed(thisCreature, targetCreature) &&
                targetCreature.getParams().getPos().distance(getParams().getPos()) < 10f &&
                !getParams().getCreaturesAlreadyHit().containsKey(targetCreature.getId()))
            .collect(Collectors.toSet())) {
            if (creature.getParams().getPos().distance(getParams().getPos()) < minDistance) {
                minCreature = creature;
                minDistance = creature.getParams().getPos().distance(getParams().getPos());
            }

        }

        if (minCreature != null) {
            Vector2 vectorTowards = getParams().getPos().vectorTowards(minCreature.getParams().getPos());
            float targetAngleDeg = vectorTowards.angleDeg();
            float currentAngleDeg = getParams().getDirVector().angleDeg();

            float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentAngleDeg, targetAngleDeg);

            float incrementFactor = 50f;
            float increment = incrementFactor * delta;

            if (shortestAngleRotation > increment) {
                getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(increment));
            } else if (shortestAngleRotation < -increment) {
                getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(-increment));
            } else {
                getParams().setDirVector(getParams().getDirVector().withSetDegAngle(targetAngleDeg));
            }

        } else {
            if (getParams().getChangeDirectionTimer().getTime() > 1f) {
                getParams().getChangeDirectionTimer().restart();
                getParams().setDirVector(getParams()
                    .getDirVector()
                    .withRotatedDegAngle(game.getGameState().getRandomGenerator().nextFloat() * 20f));
            }
        }
    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
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
}
