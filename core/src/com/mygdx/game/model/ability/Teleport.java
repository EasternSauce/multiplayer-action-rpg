package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Teleport extends Ability {
    AbilityParams params;


    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {
        game.chainAbility(this, AbilityType.TELEPORT_DESTINATION, params().pos(), params().dirVector(), game);
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
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos, MyGdxGame game) {

    }

    @Override
    public void onAbilityHit(AbilityId otherAbilityId, MyGdxGame game) {

    }

    public static Teleport of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        Creature creature = game.getCreature(abilityParams.creatureId());

        Teleport ability = Teleport.of();
        ability.params =
                abilityParams
                        .width(4.5f)
                        .height(4.5f)
                        .channelTime(0f)
                        .activeTime(0.5f)
                        .textureName("blast")
                        .baseDamage(0f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(false)
                        .rotationShift(0f)
                        .pos(creature.params().pos())
                        .delayedActionTime(0.1f);


        return ability;
    }
}
