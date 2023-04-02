package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningSpark extends Ability {

    AbilityParams params;

    public static LightningSpark of(AbilityParams abilityParams, AbilityUpdatable game) {
        Creature creature = game.getCreature(abilityParams.creatureId());

        LightningSpark ability = LightningSpark.of();
        ability.params =
                abilityParams
                        .width(3f)
                        .height(3f)
                        .channelTime(0f)
                        .activeTime(0.4f)
                        .textureName("lightning")
                        .baseDamage(30f)
                        .isActiveAnimationLooping(true)
                        .attackWithoutMoving(true)
                        .isSkipCreatingBody(true)
                        .rotationShift(0f)
                        .delayedActionTime(0.001f)
                        .pos(LightningSpark.calculatePos(creature.params().pos().add(abilityParams.dirVector()),
                                                         creature.params().pos()));

        return ability;
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 creaturePos) {
        Vector2 vectorTowards = creaturePos.vectorTowards(pos);

        float maxRange = 5f;
        if (vectorTowards.len() > maxRange) {
            return creaturePos.add(vectorTowards.normalized().multiplyBy(maxRange));
        }
        return pos;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public Boolean isPositionCalculated() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {
        // find the closest enemy, and if they are within distance, and haven't been hit yet, then put node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit().keySet());
        excluded.add(params().creatureId());

        Creature targetCreature = game.getCreature(game.getAliveCreatureIdClosestTo(params().pos(), 13f, excluded));

        if (targetCreature != null &&
            game.isLineOfSight(params().areaId(), params().pos(), targetCreature.params().pos())) {

            game.onCreatureHit(targetCreature.params().id(),
                               params().creatureId(),
                               true,
                               params().dirVector(),
                               getDamage(game),
                               game);

            params().creaturesAlreadyHit().put(targetCreature.params().id(), params().stateTimer().time());

            game.chainAbility(this,
                              AbilityType.LIGHTNING_CHAIN,
                              targetCreature.params().pos(), // this pos is later changed, TODO: move it to other param?
                              params.dirVector());

            game.chainAbility(this,
                              AbilityType.LIGHTNING_NODE,
                              targetCreature.params().pos(),
                              params.dirVector());
        }
    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {

    }

    @Override
    void onUpdatePosition(AbilityUpdatable game) {

    }

    @Override
    void onChannelUpdate(AbilityUpdatable game) {

    }

    @Override
    void onActiveUpdate(AbilityUpdatable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }

    @Override
    public Map<Integer, Float> levelScalings() {
        ConcurrentSkipListMap<Integer, Float> scalings = new ConcurrentSkipListMap<>();
        scalings.put(1, 1.0f);
        scalings.put(2, 1.1f);
        scalings.put(3, 1.2f);
        return scalings;
    }
}
