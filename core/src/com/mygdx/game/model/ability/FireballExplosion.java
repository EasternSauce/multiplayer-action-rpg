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
public class FireballExplosion extends Ability {
    AbilityParams params;


    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public AbilityType type() {
        return AbilityType.FIREBALL_EXPLOSION;
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

    public static FireballExplosion of(AbilityId abilityId,
                                       AreaId areaId,
                                       CreatureId creatureId,
                                       Vector2 pos,
                                       Vector2 dirVector,
                                       Set<CreatureId> creaturesAlreadyHit) {
        FireballExplosion ability = FireballExplosion.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(9f)
                                      .height(9f)
                                      .channelTime(0f)
                                      .activeTime(0.35f)
                                      .textureName("explosion")
                                      .creatureId(creatureId)
                                      .damage(28f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(false)
                                      .attackWithoutMoving(true)
                                      .creaturesAlreadyHit(new ConcurrentSkipListSet<>())
                                      .rotationShift(0f);

        return ability;
    }

}
