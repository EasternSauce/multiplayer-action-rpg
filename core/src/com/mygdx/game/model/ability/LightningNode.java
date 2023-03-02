package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningNode extends Ability {
    AbilityParams params;

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {
        // find the closest enemy, and if they are within distance, and haven't been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit());
        excluded.add(params().creatureId());

        Creature targetCreature = game.getCreature(game.aliveCreatureClosestTo(params().pos(), 13f, excluded));

        if (targetCreature != null &&
            params().creaturesAlreadyHit().size() <= 10 &&
            game.getWorld(params().areaId()).isLineOfSight(params().pos(), targetCreature.params().pos())) {
            targetCreature.handleBeingAttacked(true,
                                               params().damage(),
                                               params().creatureId()); // TODO: can we do this in main update loop instead? introduce events etc.

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
    public void onTerrainHit() {

    }

    public static LightningNode of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        LightningNode ability = LightningNode.of();
        ability.params = abilityParams

                .width(3f)
                .height(3f)
                .channelTime(0f)
                .activeTime(0.4f)
                .textureName("lightning")
                .damage(30f)
                .isActiveAnimationLooping(true)
                .attackWithoutMoving(true)
                .inactiveBody(true)
                .rotationShift(0f)
                .delayedActionTime(0.001f);

        return ability;
    }
}
