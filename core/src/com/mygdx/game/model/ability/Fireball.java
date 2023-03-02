package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.creature.Creature;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Fireball extends Projectile {
    AbilityParams params;


    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onTerrainHit() {
        deactivate();
    }

    @Override
    protected void onActiveUpdate(AbilityUpdateable game) {
        //projectile speeds up over time
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());

        if (params().stateTimer().time() < 2f) {
            params().speed(5f + (params().stateTimer().time() / 2f) * 40f);
        }
        else {
            params().speed(45f);
        }
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
    protected void onAbilityCompleted(MyGdxGame game) {
        game.chainAbility(this,
                          AbilityType.FIREBALL_EXPLOSION,
                          params().pos(),
                          params.dirVector(),
                          game);
    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    public static Fireball of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        Creature creature = game.getCreature(abilityParams.creatureId());

        Fireball ability = Fireball.of();
        ability.params =
                abilityParams
                        .width(1.5f)
                        .height(1.5f)
                        .channelTime(0f)
                        .activeTime(30f)
                        .textureName("fireball")
                        .damage(15f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(true)
                        .rotationShift(0f)
                        .delayedActionTime(0.001f)
                        .pos(creature.params().pos());


        return ability;
    }
}
