package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.util.Ability;
import com.easternsauce.actionrpg.model.ability.util.AbilityId;
import com.easternsauce.actionrpg.model.ability.util.AbilityParams;
import com.easternsauce.actionrpg.model.ability.util.AbilityType;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningNode extends Ability {
    AbilityParams params;

    public static LightningNode of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        LightningNode ability = LightningNode.of();
        ability.params = abilityParams
            .setWidth(3f)
            .setHeight(3f)
            .setChannelTime(0f)
            .setActiveTime(0.4f)
            .setTextureName("lightning")
            .setBaseDamage(25f)
            .setIsActiveAnimationLooping(true)
            .setAttackWithoutMoving(true)
            .setIsSkipCreatingBody(true)
            .setRotationShift(0f)
            .setDelayedActionTime(0.05f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void onAbilityStarted(CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {
        // find the closest enemy, and if they are within distance, and haven't been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(getParams().getCreaturesAlreadyHit().keySet());
        excluded.add(getParams().getCreatureId());

        Creature targetCreature = game
            .getGameState()
            .accessCreatures()
            .getCreature(game.getGameState().accessCreatures().getAliveCreatureIdClosestTo(getParams().getPos(), 13f, excluded));

        if (targetCreature != null && getParams().getCreaturesAlreadyHit().size() <= 10 &&
            game.isLineBetweenPointsUnobstructedByTerrain(getParams().getAreaId(),
                                                          getParams().getPos(),
                                                          targetCreature.getParams().getPos())) {

            game
                .getGameState()
                .accessAbilities()
                .onAbilityHitsCreature(getParams().getCreatureId(), targetCreature.getId(), getParams().getId(), game);

            getParams().getCreaturesAlreadyHit().put(targetCreature.getId(), getParams().getStateTimer().getTime());

            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this, AbilityType.LIGHTNING_CHAIN, targetCreature.getParams().getPos(),
                                     // this pos is later changed, TODO: move it to other param?
                                     params.getDirVector(), null, null, game);

            game
                .getGameState()
                .accessAbilities()
                .chainAnotherAbility(this,
                                     AbilityType.LIGHTNING_NODE,
                                     targetCreature.getParams().getPos(),
                                     params.getDirVector(),
                                     null,
                                     null,
                                     game);
        }
    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {

    }

    @Override
    public void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {

    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    @Override
    public Map<Integer, Float> levelScalings() {
        ConcurrentSkipListMap<Integer, Float> scalings = new ConcurrentSkipListMap<>();
        scalings.put(1, 1.0f);
        scalings.put(2, 1.1f);
        scalings.put(3, 1.2f);
        return scalings;
    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }
}
