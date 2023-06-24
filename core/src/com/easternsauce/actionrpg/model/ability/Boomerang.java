package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.util.MathHelper;
import com.easternsauce.actionrpg.model.util.Vector2;
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
        ability.params = abilityParams
            .setWidth(1.3f)
            .setHeight(1.3f)
            .setChannelTime(0f)
            .setActiveTime(10f)
            .setTextureName("boomerang")
            .setBaseDamage(22f)
            .setIsChannelAnimationLooping(true)
            .setIsActiveAnimationLooping(true)
            .setSpeed(15f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void onChannelUpdate(CoreGame game) {
        onProjectileTravelUpdate();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        onProjectileTravelUpdate();

        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());

        if (creature != null) {
            if (!getParams().getIsComingBack() && getParams().getStateTimer().getTime() > 1f) {
                getParams().setIsComingBack(true);
                getParams().setSpeed(20f);
            }

            if (getParams().getIsComingBack()) {
                Vector2 vectorTowards = getParams().getPos().vectorTowards(creature.getParams().getPos());
                float targetAngleDeg = vectorTowards.angleDeg();
                float currentAngleDeg = getParams().getDirVector().angleDeg();

                float shortestAngleRotation = MathHelper.findShortestDegAngleRotation(currentAngleDeg, targetAngleDeg);

                float incrementFactor = 330f;
                float increment = incrementFactor * delta;

                if (shortestAngleRotation > increment || shortestAngleRotation < -increment) {
                    getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(increment));
                }
                else {
                    getParams().setDirVector(getParams().getDirVector().withSetDegAngle(targetAngleDeg));
                }
            }

        }

    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        getParams().setIsComingBack(true);
        getParams().setSpeed(20f);
    }

    @Override
    public void onSelfCreatureHit(CoreGame game) {
        if (getParams().getIsComingBack()) {
            Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());
            Skill skill = creature.getParams().getSkills().get(getParams().getSkillType());

            skill.resetCooldown();

            deactivate();
        }
    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        getParams().setIsComingBack(true);
        getParams().setSpeed(20f);
    }

    @Override
    protected boolean isWeaponAttack() {
        return true;
    }

    @Override
    public Float getStunDuration() {
        return 0.65f;
    }
}
