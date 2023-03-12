package com.mygdx.game.model.ability;

import com.mygdx.game.game.intrface.AbilityUpdatable;
import com.mygdx.game.game.intrface.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningSpark extends Ability {

    AbilityParams params;


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
        // find closest enemy, and if they are within distance, and havent been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit().keySet());
        excluded.add(params().creatureId());

        Creature targetCreature = game.getCreature(game.getAliveCreatureIdClosestTo(params().pos(), 13f, excluded));

        if (targetCreature != null &&
            game.isLineOfSight(params().areaId(), params().pos(), targetCreature.params().pos())) {
            targetCreature.handleBeingAttacked(true,
                                               params().dirVector(),
                                               params().currentDamage(),
                                               params().creatureId(), game);

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
                        .inactiveBody(true)
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

}
