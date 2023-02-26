package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.Enemy;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class MagicOrb extends Projectile {

    AbilityParams params;


    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public AbilityType type() {
        return AbilityType.MAGIC_ORB;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(AbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(AbilityChainable game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onTerrainHit() {
        if (params().stateTimer().time() > 0.1f) {
            deactivate();
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
    void onActiveUpdate(AbilityUpdateable game) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());

        Creature minCreature = null;
        float minDistance = Float.MAX_VALUE;

        Creature thisCreature = game.getCreature(params().creatureId());

        for (Creature creature : game.getCreatures()
                                     .stream()
                                     .filter(targetCreature -> !targetCreature.params()
                                                                              .id()
                                                                              .equals(params().creatureId()) &&
                                                               targetCreature.isAlive() &&
                                                               isTargetingAllowed(thisCreature, targetCreature) &&
                                                               targetCreature.params()
                                                                             .pos()
                                                                             .distance(params().pos()) <
                                                               20f)
                                     .collect(
                                             Collectors.toSet())) {
            if (creature.params().pos().distance(params().pos()) < minDistance) {
                minCreature = creature;
                minDistance = creature.params().pos().distance(params().pos());
            }
        }


        if (minCreature != null) {
            Vector2 vectorTowards = params().pos().vectorTowards(minCreature.params().pos());
            float targetAngleDeg = vectorTowards.angleDeg();
            float currentAngleDeg = params().dirVector().angleDeg();

            float alpha = targetAngleDeg - currentAngleDeg;
            float beta = targetAngleDeg - currentAngleDeg + 360;
            float gamma = targetAngleDeg - currentAngleDeg - 360;

            float result;
            if (Math.abs(alpha) < Math.abs(beta)) {
                if (Math.abs(alpha) < Math.abs(gamma)) {
                    result = alpha;
                }
                else {
                    result = gamma;
                }
            }
            else {
                if (Math.abs(beta) < Math.abs(gamma)) {
                    result = beta;
                }
                else {
                    result = gamma;
                }
            }

            float increment = 1.5f;

            if (params().stateTimer().time() > 0.5f && params().stateTimer().time() < 2f) {
                increment = 1.5f - (params().stateTimer().time() - 0.5f) / 1.5f * 1.5f;
            }
            else if (params().stateTimer().time() >= 2f) {
                increment = 0f;
            }

            if (result > increment) {
                params().dirVector(params().dirVector().setAngleDeg(params().dirVector().angleDeg() + increment));
            }
            else if (result < -increment) {
                params().dirVector(params().dirVector().setAngleDeg(params().dirVector().angleDeg() - increment));
            }
            else {
                params().dirVector(params().dirVector().setAngleDeg(targetAngleDeg));
            }

        }

    }

    public static MagicOrb of(AbilityId abilityId,
                              AreaId areaId,
                              CreatureId creatureId,
                              Vector2 pos,
                              Vector2 dirVector,
                              Set<CreatureId> creaturesAlreadyHit) {
        MagicOrb ability = MagicOrb.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1.5f)
                                      .height(1.5f)
                                      .channelTime(0f)
                                      .activeTime(30f)
                                      .textureName("magic_orb")
                                      .creatureId(creatureId)
                                      .damage(40f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(true)
                                      .creaturesAlreadyHit(new ConcurrentSkipListSet<>())
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f)
                                      .speed(12f);


        return ability;
    }
}
