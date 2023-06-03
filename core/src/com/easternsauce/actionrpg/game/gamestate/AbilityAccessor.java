package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.action.CreatureHitByAbilityAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AbilityAccessor {
    private GameState gameState;
    private GameStateDataHolder dataHolder;

    private GameStateData getData() {
        return dataHolder.getData();
    }

    public Ability getAbility(AbilityId abilityId) {
        if (abilityId == null || !getData().getAbilities().containsKey(abilityId)) {
            return null;
        }
        return getData().getAbilities().get(abilityId);
    }

    public Ability getAbilityBySkillType(CreatureId creatureId, SkillType skillType) {
        Optional<Ability> first = getData()
            .getAbilities()
            .values()
            .stream()
            .filter(ability -> ability.getParams().getCreatureId().equals(creatureId) &&
                               ability.getParams().getSkillType() == skillType)
            .findFirst();

        return first.orElse(null);
    }

    public Map<AbilityId, Ability> getAbilities() {
        return getData().getAbilities();
    }

    public Set<AbilityId> getAbilitiesWithinRange(Creature player) {
        return getAbilities().keySet().stream().filter(abilityId -> {
            Ability ability = getAbilities().get(abilityId);
            if (ability != null && ability.getParams().getPos() != null) {
                return ability.getParams().getPos().distance(player.getParams().getPos()) < Constants.CLIENT_GAME_UPDATE_RANGE;
            }
            return false;
        }).collect(Collectors.toSet());
    }

    public void spawnAbility(AbilityType abilityType, AbilityParams abilityParams, CoreGame game) {
        Creature creature = gameState.accessCreatures().getCreature(abilityParams.getCreatureId());

        if (creature != null) {
            Ability ability = AbilityFactory.produceAbility(abilityType, abilityParams, game);

            initializeAbility(creature, ability, game);
        }
    }

    private void initializeAbility(Creature creature, Ability ability, CoreGame game) {
        game.getGameState().accessAbilities().getAbilities().put(ability.getParams().getId(), ability);

        game.getEventProcessor().getAbilityModelsToBeCreated().add(ability.getParams().getId());

        ability.init(game);

        creature.onAbilityPerformed(ability);
    }

    public void chainAnotherAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 chainToPos, Vector2 dirVector,
                                    Float overrideSize, Float overrideDuration, CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(chainFromAbility.getParams().getCreatureId());

        if (creature != null && (creature.isAlive() || chainFromAbility.isAbleToChainAfterCreatureDeath())) {
            AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

            Map<CreatureId, Float> creaturesAlreadyHit = new ConcurrentSkipListMap<>(chainFromAbility
                                                                                         .getParams()
                                                                                         .getCreaturesAlreadyHit());

            Vector2 chainFromPos = chainFromAbility.getParams().getPos();

            AbilityParams abilityParams = AbilityParams
                .of()
                .setId(abilityId)
                .setAreaId(chainFromAbility.getParams().getAreaId())
                .setCreatureId(chainFromAbility.getParams().getCreatureId())
                .setCreaturesAlreadyHit(creaturesAlreadyHit)
                .setChainFromPos(chainFromPos)
                .setChainToPos(chainToPos)
                .setDirVector(dirVector)
                .setVectorTowardsTarget(dirVector)
                .setOverrideSize(overrideSize)
                .setOverrideDuration(overrideDuration)
                .setSkillType(chainFromAbility.getParams().getSkillType())
                .setSkillStartPos(chainFromPos);

            spawnAbility(abilityType, abilityParams, game);
        }
    }

    public void onAbilityHitsCreature(CreatureId attackerId, CreatureId targetId, AbilityId abilityId, CoreGame game) {
        Ability ability = game.getGameState().accessAbilities().getAbility(abilityId);

        ability.onCreatureHit(targetId, game);

        ability.getParams().getCreaturesAlreadyHit().put(targetId, ability.getParams().getStateTimer().getTime());

        CreatureHitByAbilityAction action = CreatureHitByAbilityAction.of(attackerId, targetId, ability);
        gameState.scheduleServerSideAction(action);
    }

}