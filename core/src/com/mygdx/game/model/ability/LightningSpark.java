package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
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

    public static LightningSpark of(AbilityParams abilityParams, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(abilityParams.getCreatureId());

        //        Vector2 pos;
        //        if (abilityParams.getDirVector() != null) {
        //            pos = LightningSpark.calculatePos(creature.getParams().getPos().add(abilityParams.getDirVector()),
        //                                              creature.getParams().getPos());
        //        } else {
        //            pos = LightningSpark.calculatePos(creature.getParams().getPos().add(Vector2.of(0f, 0f)),
        //                                              creature.getParams().getPos());
        //        }

        LightningSpark ability = LightningSpark.of();
        ability.params = abilityParams.setWidth(3f)
                .setHeight(3f)
                .setChannelTime(0f)
                .setActiveTime(0.4f)
                .setTextureName("lightning")
                .setBaseDamage(25f)
                .setIsActiveAnimationLooping(true)
                .setAttackWithoutMoving(true)
                .setIsSkipCreatingBody(true)
                .setRotationShift(0f)
                .setDelayedActionTime(0.001f)
                .setPos(LightningSpark.calculatePos(creature.getParams()
                                .getPos()
                                .add(abilityParams.getDirVector()),
                        creature.getParams().getPos()));

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
    public void updatePosition(CoreGame game) {

    }

    @Override
    void onAbilityStarted(CoreGame game) {

    }

    @Override
    void onDelayedAction(CoreGame game) {
        // find the closest enemy, and if they are within distance, and haven't been hit yet, then put node over them
        Set<CreatureId> excluded = new HashSet<>(getParams().getCreaturesAlreadyHit().keySet());
        excluded.add(getParams().getCreatureId());

        Creature targetCreature = game.getGameState().accessCreatures().getCreature(game.getGameState()
                .accessCreatures().getAliveCreatureIdClosestTo(getParams().getPos(),
                        13f,
                        excluded));

        if (targetCreature != null &&
                game.isLineOfSight(getParams().getAreaId(), getParams().getPos(), targetCreature.getParams().getPos())) {

            game.getGameState().accessAbilities().onAbilityHitsCreature(getParams().getCreatureId(), targetCreature.getId(), this);

            getParams().getCreaturesAlreadyHit().put(targetCreature.getId(), getParams().getStateTimer().getTime());

            game.getGameState().accessAbilities().chainAnotherAbility(this, AbilityType.LIGHTNING_CHAIN, targetCreature.getParams().getPos(),
                    // this pos is later changed, TODO: move it to other param?
                    params.getDirVector(), game);

            game.getGameState().accessAbilities().chainAnotherAbility(this,
                    AbilityType.LIGHTNING_NODE,
                    targetCreature.getParams().getPos(),
                    params.getDirVector(), game);
        }
    }

    @Override
    void onAbilityCompleted(CoreGame game) {

    }

    @Override
    void onChannelUpdate(CoreGame game) {

    }

    @Override
    void onActiveUpdate(CoreGame game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

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
