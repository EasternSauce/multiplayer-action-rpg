package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Meteor extends Projectile {
    @Getter
    private AbilityParams params;

    private Vector2 startingPos;
    private Vector2 destinationPos;

    public static Meteor of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getCreature(abilityParams.getCreatureId());

        Meteor ability = Meteor.of();

        ability.destinationPos = Meteor.calculatePos(creature.getParams().getPos().add(abilityParams.getDirVector()),
            creature.getParams().getPos(),
            creature.getParams().getAreaId(),
            game
        );
        ability.startingPos = Vector2.of(ability.destinationPos.getX() + 12f, ability.destinationPos.getY() + 12f);

        ability.params = abilityParams
            .setWidth(2.474f)
            .setHeight(2f)
            .setChannelTime(0f)
            .setActiveTime(5f)
            .setTextureName("meteor")
            .setBaseDamage(0f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false)
            .setPos(ability.startingPos)
            .setDontOverridePos(true)
            .setDirVector(Vector2.of(-1, -1));

        return ability;
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 creaturePos, AreaId areaId, CoreGame game) {
        Vector2 vectorTowards = creaturePos.vectorTowards(pos);

        Vector2 destinationPos;

        float maxRange = 20f;
        if (vectorTowards.len() > maxRange) {
            destinationPos = creaturePos.add(vectorTowards.normalized().multiplyBy(maxRange));
        } else {
            destinationPos = pos;
        }

        if (!game.isLineBetweenPointsUnobstructedByTerrain(areaId, creaturePos, destinationPos)) {
            destinationPos = creaturePos;
        }

        return destinationPos;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {

    }

    @Override
    public void onStarted(CoreGame game) {
        Creature creature = game.getCreature(getParams().getCreatureId());
        creature.applyEffect(CreatureEffect.SELF_STUN, 0.2f, game);
        creature.stopMoving();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        onProjectileTravelUpdate();

        getParams().setRotationAngle(0f);

        if (getParams().getStateTimer().getTime() < 0.5f) {
            getParams().setSpeed(20f + (getParams().getStateTimer().getTime() / 0.5f) * 10f);
        } else {
            getParams().setSpeed(30f);
        }

        if (startingPos.distance(getParams().getPos()) > ((float) Math.sqrt(2) * 12f)) {
            deactivate();
        }
    }

    @Override
    protected void onCompleted(CoreGame game) {
        game.chainAnotherAbility(this,
            AbilityType.FIREBALL_EXPLOSION,
            getParams().getDirVector(),
            ChainAbilityParams
                .of()
                .setChainToPos(getParams().getPos())
                .setOverrideStunDuration(0.05f)
                .setOverrideScale(1f)
        );
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @Override
    public boolean canBeDeactivated() {
        return true;
    }

    @Override
    public boolean canStun() {
        return false;
    }

}
