package com.mygdx.game.ability;


import com.mygdx.game.game.CreatureAbilityChainable;
import com.mygdx.game.game.CreatureAbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
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
public class CrossbowBolt extends Projectile {

    AbilityParams params;

    @Override
    public AbilityType type() {
        return AbilityType.CROSSBOW_BOLT;
    }

    @Override
    void onAbilityStarted(CreatureAbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(CreatureAbilityChainable game) {
        // TODO: chain multiple shots
    }

    @Override
    void onAbilityCompleted(CreatureAbilityChainable game) {

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

    public static CrossbowBolt of(AbilityId abilityId,
                                  AreaId areaId,
                                  CreatureId creatureId,
                                  Vector2 pos,
                                  Vector2 dirVector,
                                  Set<CreatureId> creaturesAlreadyHit) {
        CrossbowBolt ability = CrossbowBolt.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1.5f)
                                      .height(1.5f)
                                      .channelTime(0f)
                                      .activeTime(30f)
                                      .textureName("arrow")
                                      .creatureId(creatureId)
                                      .damage(25f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(0f)
                                      .staminaCost(10f)
                                      .cooldown(0.35f)
                                      .performableByCreature(true)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(true)
                                      .isActiveAnimationLooping(true)
                                      .creaturesAlreadyHit(new HashSet<>())
                                      .rotationShift(0f)
                                      .speed(30f);


        return ability;
    }
}
