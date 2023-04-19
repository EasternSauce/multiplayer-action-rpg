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
public class LightningNode extends Ability {
    AbilityParams params;

    public static LightningNode of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        LightningNode ability = LightningNode.of();
        ability.params = abilityParams
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
                .delayedActionTime(0.05f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(AbilityUpdatable game) {

    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {
        // find the closest enemy, and if they are within distance, and haven't been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit().keySet());
        excluded.add(params().creatureId());

        Creature targetCreature = game.getCreature(game.getAliveCreatureIdClosestTo(params().pos(), 13f, excluded));

        if (targetCreature != null &&
            params().creaturesAlreadyHit().size() <= 10 &&
            game.isLineOfSight(params().areaId(), params().pos(), targetCreature.params().pos())) {

            game.onAbilityHitsCreature(targetCreature.id(), params().creatureId(), this);

            params().creaturesAlreadyHit().put(targetCreature.id(), params().stateTimer().time());

            game.chainAbility(this,
                              AbilityType.LIGHTNING_CHAIN,
                              targetCreature.params().pos(),
                              // this pos is later changed, TODO: move it to other param?
                              params.dirVector());

            game.chainAbility(this, AbilityType.LIGHTNING_NODE, targetCreature.params().pos(), params.dirVector());
        }
    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {

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
