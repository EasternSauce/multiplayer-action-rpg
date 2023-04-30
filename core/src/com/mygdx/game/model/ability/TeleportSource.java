package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class TeleportSource extends Ability {
    AbilityParams params;

    public static TeleportSource of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        Creature creature = game.getGameState().getCreature(abilityParams.getCreatureId());

        TeleportSource ability = TeleportSource.of();
        ability.params = abilityParams.setWidth(4.5f)
                .setHeight(4.5f)
                .setChannelTime(0f)
                .setActiveTime(0.5f)
                .setTextureName("blast")
                .setBaseDamage(0f)
                .setIsChannelAnimationLooping(false)
                .setIsActiveAnimationLooping(false)
                .setRotationShift(0f)
                .setPos(creature.getParams().getPos())
                .setDelayedActionTime(0.1f);


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
        game.chainAbility(this, AbilityType.TELEPORT_DESTINATION, getParams().getPos(), getParams().getDirVector());
    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {

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
