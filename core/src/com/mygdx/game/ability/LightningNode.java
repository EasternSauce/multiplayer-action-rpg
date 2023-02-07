package com.mygdx.game.ability;

import com.mygdx.game.game.CreatureAbilityChainable;
import com.mygdx.game.game.CreatureAbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
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
    public AbilityType type() {
        return AbilityType.LIGHTNING_NODE;
    }

    @Override
    void onAbilityStarted(CreatureAbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(CreatureAbilityChainable game) {
        // find closest enemy, and if they are within distance, and havent been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit());
        excluded.add(params().creatureId());

        Creature creature = game.getCreature(game.aliveCreatureClosestTo(params().pos(), 9f, excluded));

        if (creature != null && params().creaturesAlreadyHit().size() <= 10) {
            creature.handleBeingAttacked(params().damage(),
                                         params().creatureId()); // TODO: can we do this in main update loop instead? introduce events etc.

            game.chainAbility(this, AbilityType.LIGHTNING_CHAIN, creature.params().pos(), null);

            game.chainAbility(this, AbilityType.LIGHTNING_NODE, creature.params().pos(), creature.params().id());
        }
    }

    @Override
    void onAbilityCompleted(CreatureAbilityChainable game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    void onChannelUpdate(CreaturePosRetrievable game) {

    }

    @Override
    void onActiveUpdate(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }

    public static LightningNode of(AbilityId abilityId,
                                   AreaId areaId,
                                   CreatureId creatureId,
                                   Vector2 pos,
                                   Vector2 dirVector,
                                   Set<CreatureId> creaturesAlreadyHit) {
        LightningNode ability = LightningNode.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(3f)
                                      .height(3f)
                                      .channelTime(0f)
                                      .activeTime(0.4f)
                                      .textureName("lightning")
                                      .creatureId(creatureId)
                                      .damage(30f)
                                      .isActiveAnimationLooping(true)
                                      .attackWithoutMoving(true)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(0f)
                                      .staminaCost(0f)
                                      .cooldown(1.0f)
                                      .performableByCreature(false)
                                      .inactiveBody(true)
                                      .dirVector(dirVector)
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f);

        return ability;
    }
}
