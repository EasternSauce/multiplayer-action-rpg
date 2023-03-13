package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class FireballExplosion extends Ability {
    AbilityParams params;

    public static FireballExplosion of(AbilityParams abilityParams,
                                       @SuppressWarnings("unused") AbilityUpdatable game) {
        FireballExplosion ability = FireballExplosion.of();
        ability.params =
                abilityParams.width(9f)
                             .height(9f)
                             .channelTime(0f)
                             .activeTime(0.35f)
                             .textureName("explosion")
                             .baseDamage(35f)
                             .isChannelAnimationLooping(false)
                             .isActiveAnimationLooping(false)
                             .attackWithoutMoving(true)
                             .rotationShift(0f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

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

}
