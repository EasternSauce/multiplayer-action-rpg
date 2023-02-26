package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SummonGhosts extends Ability {
    AbilityParams params;

    public static SummonGhosts of(AbilityId abilityId,
                                  AreaId areaId,
                                  CreatureId creatureId,
                                  Vector2 pos,
                                  Vector2 dirVector,
                                  Set<CreatureId> creaturesAlreadyHit) {
        SummonGhosts ability = SummonGhosts.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1.5f)
                                      .height(1.5f)
                                      .channelTime(0f)
                                      .activeTime(0f)
                                      .textureName("ghost")
                                      .creatureId(creatureId)
                                      .damage(15f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(true)
                                      .creaturesAlreadyHit(new ConcurrentSkipListSet<>())
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f);


        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public AbilityType type() {
        return AbilityType.SUMMON_GHOSTS;
    }

    @Override
    void onAbilityStarted(AbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(AbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(AbilityChainable game) {
        float baseAngle = params().dirVector().angleDeg();
        game.chainAbility(this, AbilityType.PLAYFUL_GHOST, params().pos(), params.dirVector(), null);
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle - 15f),
                          null);
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 15f),
                          null);
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
}
