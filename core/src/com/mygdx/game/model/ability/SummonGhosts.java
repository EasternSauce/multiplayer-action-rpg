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
public class SummonGhosts extends Ability {
    AbilityParams params;

    public static SummonGhosts of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        SummonGhosts ability = SummonGhosts.of();
        ability.params = abilityParams.setChannelTime(0f).setActiveTime(0f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(AbilityUpdatable game) {

    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {
        float baseAngle = getParams().getDirVector().angleDeg();
        game.chainAbility(this, AbilityType.PLAYFUL_GHOST, getParams().getPos(), params.getDirVector());
        game.chainAbility(this,
                AbilityType.PLAYFUL_GHOST,
                getParams().getPos(),
                params.getDirVector().setAngleDeg(baseAngle - 30f));
        game.chainAbility(this,
                AbilityType.PLAYFUL_GHOST,
                getParams().getPos(),
                params.getDirVector().setAngleDeg(baseAngle + 30f));
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
