package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
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
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {
        // find closest enemy, and if they are within distance, and havent been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit());
        excluded.add(params().creatureId());

        Creature targetCreature = game.getCreature(game.aliveCreatureClosestTo(params().pos(), 13f, excluded));

        if (targetCreature != null &&
            game.getWorld(params().areaId()).isLineOfSight(params().pos(), targetCreature.params().pos())) {
            targetCreature.handleBeingAttacked(true, params().currentDamage(), params().creatureId());

            params().creaturesAlreadyHit().add(targetCreature.params().id());

            game.chainAbility(this,
                              AbilityType.LIGHTNING_CHAIN,
                              targetCreature.params().pos(), // this pos is later changed, TODO: move it to other param?
                              params.dirVector(),
                              game);

            game.chainAbility(this,
                              AbilityType.LIGHTNING_NODE,
                              targetCreature.params().pos(),
                              params.dirVector(),
                              game);
        }
    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    void onChannelUpdate(CreaturePosRetrievable game) {

    }

    @Override
    void onActiveUpdate(AbilityUpdateable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {

    }

    public static LightningSpark of(AbilityParams abilityParams, MyGdxGame game) {
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
