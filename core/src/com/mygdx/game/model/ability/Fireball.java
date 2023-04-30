package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Fireball extends Projectile {
    AbilityParams params;

    public static Fireball of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        Creature creature = game.getGameState().getCreature(abilityParams.getCreatureId());

        Fireball ability = Fireball.of();
        ability.params = abilityParams.setWidth(1.5f)
                .setHeight(1.5f)
                .setChannelTime(0f)
                .setActiveTime(30f)
                .setTextureName("fireball")
                .setBaseDamage(20f)
                .setIsChannelAnimationLooping(false)
                .setIsActiveAnimationLooping(true)
                .setRotationShift(0f)
                .setDelayedActionTime(0.001f)
                .setPos(creature.getParams().getPos());


        return ability;
    }

    @Override
    public void onCreatureHit() {
        deactivate();
    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    protected void onActiveUpdate(AbilityUpdatable game) {
        //projectile speeds up over time
        if (getParams().getSpeed() != null) {
            getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        }
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());

        if (getParams().getStateTimer().getTime() < 2f) {
            getParams().setSpeed(5f + (getParams().getStateTimer().getTime() / 2f) * 40f);
        } else {
            getParams().setSpeed(45f);
        }
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
    protected void onAbilityCompleted(AbilityUpdatable game) {
        game.chainAbility(this, AbilityType.FIREBALL_EXPLOSION, getParams().getPos(), params.getDirVector());
    }


    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }

    @Override
    public Map<Integer, Float> levelScalings() {
        ConcurrentSkipListMap<Integer, Float> scalings = new ConcurrentSkipListMap<>();
        scalings.put(1, 1.0f);
        scalings.put(2, 1.1f);
        scalings.put(3, 1.2f);
        return scalings;
    }
}
