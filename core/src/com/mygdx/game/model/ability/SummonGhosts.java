package com.mygdx.game.model.ability;

import com.mygdx.game.game.intrface.AbilityUpdatable;
import com.mygdx.game.game.intrface.GameUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SummonGhosts extends Ability {
    AbilityParams params;

    public static SummonGhosts of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
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
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {
        float baseAngle = params().dirVector().angleDeg();
        game.chainAbility(this, AbilityType.PLAYFUL_GHOST, params().pos(), params.dirVector());
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle - 30f));
        game.chainAbility(this,
                          AbilityType.PLAYFUL_GHOST,
                          params().pos(),
                          params.dirVector().setAngleDeg(baseAngle + 30f));
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
