package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.util.Vector2;
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
    void onDelayedAction(MyGdxGame game) {

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
    public void onThisCreatureHit() {

    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {

    }

    public static FireballExplosion of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
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

}
