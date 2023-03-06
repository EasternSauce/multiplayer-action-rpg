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
public class SummonGhosts extends Ability {
    AbilityParams params;

    public static SummonGhosts of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        SummonGhosts ability = SummonGhosts.of();
        ability.params = abilityParams

                .channelTime(0f).activeTime(0f);


        return ability;
    }

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
        float baseAngle = params().dirVector().angleDeg();
        game.chainAbility(this, AbilityType.PLAYFUL_GHOST, params().pos(), params.dirVector(), game);
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle - 30f),

                          game);
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 30f),

                          game);
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
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 tileCenter, MyGdxGame game) {

    }
}
