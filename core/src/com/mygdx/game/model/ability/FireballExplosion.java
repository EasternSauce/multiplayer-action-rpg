package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityChainable;
import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    public static FireballExplosion of(AbilityInitialParams abilityInitialParams) {
        FireballExplosion ability = FireballExplosion.of();
        ability.params =
                AbilityParams.of(abilityInitialParams)
                             .width(9f)
                             .height(9f)
                             .channelTime(0f)
                             .activeTime(0.35f)
                             .textureName("explosion")
                             .damage(35f)
                             .isChannelAnimationLooping(false)
                             .isActiveAnimationLooping(false)
                             .attackWithoutMoving(true)
                             .rotationShift(0f);

        return ability;
    }

}
