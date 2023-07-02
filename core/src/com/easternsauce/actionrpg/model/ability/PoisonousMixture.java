package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class PoisonousMixture extends Projectile {
    AbilityParams params;

    public static PoisonousMixture of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(abilityParams.getCreatureId());

        PoisonousMixture ability = PoisonousMixture.of();
        ability.params = abilityParams
            .setWidth(1.5f)
            .setHeight(1.5f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setTextureName("green_potion_throw")
            .setBaseDamage(0f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(true)
            .setDelayedActionTime(0.001f)
            .setPos(creature.getParams().getPos());

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    protected void onChannelUpdate(CoreGame game) {
        onProjectileTravelUpdate();
    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {
        //projectile speeds up over time
        onProjectileTravelUpdate();

        if (getParams().getStateTimer().getTime() < 1.5f) {
            getParams().setSpeed(2f + (getParams().getStateTimer().getTime() / 2f) * 28f);
        } else {
            getParams().setSpeed(30f);
        }

        if (getParams().getPos().distance(getParams().getSkillStartPos()) > 13f) {
            deactivate();
        }
    }

    @Override
    protected void onCompleted(CoreGame game) {
        game.getGameState().accessAbilities().chainAnotherAbility(this,
            AbilityType.SPREADING_POISONOUS_CLOUD,
            getParams().getPos(),
            params.getDirVector(),
            null,
            null,
            game
        );
    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        deactivate();
    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public Map<Integer, Float> levelScalings() {
        ConcurrentSkipListMap<Integer, Float> scalings = new ConcurrentSkipListMap<>();
        scalings.put(1, 1.0f);
        scalings.put(2, 1.1f);
        scalings.put(3, 1.2f);
        return scalings;
    }

    @Override
    public Float getStunDuration() {
        return 0f;
    }
}
