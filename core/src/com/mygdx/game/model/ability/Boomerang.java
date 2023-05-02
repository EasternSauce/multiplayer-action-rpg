package com.mygdx.game.model.ability;


import com.mygdx.game.game.CoreGame;
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

    public static Boomerang of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Boomerang ability = Boomerang.of();
        ability.params = abilityParams.setWidth(1.3f)
                .setHeight(1.3f)
                .setChannelTime(0f)
                .setActiveTime(10f)
                .setTextureName("boomerang")
                .setBaseDamage(22f)
                .setIsChannelAnimationLooping(true)
                .setIsActiveAnimationLooping(true)
                .setRotationShift(0f)
                .setSpeed(15f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(CoreGame game) {

    }

    @Override
    void onAbilityStarted(CoreGame game) {

    }

    @Override
    void onDelayedAction(CoreGame game) {

    }

    @Override
    void onAbilityCompleted(CoreGame game) {

    }


    @Override
    public void onCreatureHit() {
        getParams().setIsComingBack(true);
        getParams().setSpeed(20f);
    }

    @Override
    public void onThisCreatureHit(CoreGame game) {
        if (getParams().getIsComingBack()) {

            Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());
            Skill skill = creature.getParams().getSkills().get(getParams().getSkillType());

            skill.resetCooldown();

            deactivate();
        }
    }

    @Override
    void onActiveUpdate(CoreGame game) {
        if (getParams().getSpeed() != null) {
            getParams().setVelocity(getParams().getDirVector().normalized().multiplyBy(getParams().getSpeed()));
        }
        getParams().setRotationAngle(getParams().getDirVector().angleDeg());

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null) {
            if (!getParams().getIsComingBack() && getParams().getStateTimer().getTime() > 1f) {
                getParams().setIsComingBack(true);
                getParams().setSpeed(20f);
            }

            // TODO: duplicate code

            if (getParams().getIsComingBack()) {
                Vector2 vectorTowards = getParams().getPos().vectorTowards(creature.getParams().getPos());
                float targetAngleDeg = vectorTowards.angleDeg();
                float currentAngleDeg = getParams().getDirVector().angleDeg();

                float alpha = targetAngleDeg - currentAngleDeg;
                float beta = targetAngleDeg - currentAngleDeg + 360;
                float gamma = targetAngleDeg - currentAngleDeg - 360;

                float result;
                if (Math.abs(alpha) < Math.abs(beta)) {
                    if (Math.abs(alpha) < Math.abs(gamma)) {
                        result = alpha;
                    } else {
                        result = gamma;
                    }
                } else {
                    if (Math.abs(beta) < Math.abs(gamma)) {
                        result = beta;
                    } else {
                        result = gamma;
                    }
                }

                float increment = 10f;

                if (result > increment || result < -increment) {
                    getParams().setDirVector(getParams().getDirVector().rotateDeg(increment));
                } else {
                    getParams().setDirVector(getParams().getDirVector().setAngleDeg(targetAngleDeg));
                }
            }

        }

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        getParams().setIsComingBack(true);
        getParams().setSpeed(20f);
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }
}
