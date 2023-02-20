package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
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
        deactivate();
    }

    @Override
    void onActiveUpdate(AbilityUpdateable game) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());

        Creature minCreature = null;
        float minDistance = Float.MAX_VALUE;
        for (Creature creature : game.getCreatures()
                                     .stream()
                                     .filter(creature -> !creature.params().id().equals(params().creatureId()) &&
                                                         creature.isAlive() &&
                                                         creature.params()
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

            float increment = 1f;
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
                                      .creaturesAlreadyHit(new HashSet<>())
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f)
                                      .speed(12f);


        return ability;
    }
}
