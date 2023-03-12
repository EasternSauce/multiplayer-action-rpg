package com.mygdx.game.model.ability;


import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.skill.Skill;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Boomerang extends Projectile {

    AbilityParams params;

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
    public void onCreatureHit() {
        //        deactivate();
        params().comingBack(true);
        params().speed(20f);
    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {
        if (params().comingBack()) {

            Creature creature = game.getCreature(params().creatureId());
            Skill skill = creature.params().skills().get(params().skillType());

            skill.resetCooldown();

            deactivate();
        }
    }

    @Override
    void onActiveUpdate(AbilityUpdatable game) {
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());

        Creature creature = game.getCreature(params().creatureId());

        if (creature != null) {
            if (!params().comingBack() && params().stateTimer().time() > 1f) {
                params().comingBack(true);
                params().speed(20f);
            }

            // TODO: duplicate code

            if (params().comingBack()) {
                Vector2 vectorTowards = params().pos().vectorTowards(creature.params().pos());
                float targetAngleDeg = vectorTowards.angleDeg();
                float currentAngleDeg = params().dirVector().angleDeg();

                float alpha = targetAngleDeg - currentAngleDeg;
                float beta = targetAngleDeg - currentAngleDeg + 360;
                float gamma = targetAngleDeg - currentAngleDeg - 360;

                float result;
                if (Math.abs(alpha) < Math.abs(beta)) {
                    if (Math.abs(alpha) < Math.abs(gamma)) {
                        result = alpha;
                    }
                    else {
                        result = gamma;
                    }
                }
                else {
                    if (Math.abs(beta) < Math.abs(gamma)) {
                        result = beta;
                    }
                    else {
                        result = gamma;
                    }
                }

                float increment = 8f;

                if (result > increment || result < -increment) {
                    params().dirVector(params().dirVector().rotateDeg(increment));
                }
                else {
                    params().dirVector(params().dirVector().setAngleDeg(targetAngleDeg));
                }
            }

        }

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        params().comingBack(true);
        params().speed(20f);
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }

    public static Boomerang of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        Boomerang ability = Boomerang.of();
        ability.params =
                abilityParams.width(1.8f)
                             .height(1.8f)
                             .channelTime(0f)
                             .activeTime(10f)
                             .textureName("boomerang")
                             .baseDamage(25f)
                             .isChannelAnimationLooping(true)
                             .isActiveAnimationLooping(true)
                             .rotationShift(0f)
                             .speed(15f);

        return ability;
    }
}
