package com.mygdx.game.game.gamestate;

import com.mygdx.game.Constants;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.ability.AbilityActivateAction;
import com.mygdx.game.model.action.ability.AbilityAddAction;
import com.mygdx.game.model.action.creature.CreatureHitAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
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
public class GameStateAbilityAccessor {
    private GameState gameState;

    public Ability getAbility(AbilityId abilityId) {
        if (abilityId == null || !gameState.data.getAbilities().containsKey(abilityId)) {
            return null;
        }
        return gameState.data.getAbilities().get(abilityId);
    }

    public Ability getAbilityBySkillType(CreatureId creatureId, SkillType skillType) {
        Optional<Ability> first = gameState.data.getAbilities()
                .values()
                .stream()
                .filter(ability -> ability.getParams().getCreatureId().equals(creatureId) &&
                                   ability.getParams().getSkillType() == skillType)
                .findFirst();

        return first.orElse(null);
    }


    public Map<AbilityId, Ability> getAbilities() {
        return gameState.data.getAbilities();
    }


    public Set<AbilityId> getAbilitiesWithinRange(Creature player) {
        return getAbilities().keySet().stream().filter(abilityId -> {
            Ability ability = getAbilities().get(abilityId);
            if (ability != null) {
                return ability.getParams().getPos().distance(player.getParams().getPos()) < Constants.ClientGameUpdateRange;
            }
            return false;
        }).collect(Collectors.toSet());
    }


    public void spawnAbility(AbilityType abilityType, AbilityParams abilityParams, CoreGame game) {
        Creature creature = gameState.accessCreatures().getCreature(abilityParams.getCreatureId());

        if (creature != null) {
            Ability ability = AbilityFactory.produceAbility(abilityType, abilityParams, game);

            AbilityAddAction action = AbilityAddAction.of(ability);

            gameState.scheduleServerSideAction(action);
        }
    }

    public void chainAnotherAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 chainToPos, Vector2 dirVector,
                                    CoreGame game) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        Map<CreatureId, Float> creaturesAlreadyHit = new ConcurrentSkipListMap<>(chainFromAbility.getParams()
                                                                                         .getCreaturesAlreadyHit());

        Vector2 chainFromPos = chainFromAbility.getParams().getPos();

        AbilityParams abilityParams = AbilityParams.of()
                .setId(abilityId)
                .setAreaId(chainFromAbility.getParams().getAreaId())
                .setCreatureId(chainFromAbility.getParams().getCreatureId())
                .setCreaturesAlreadyHit(creaturesAlreadyHit)
                .setChainFromPos(chainFromPos)
                .setChainToPos(chainToPos)
                .setDirVector(dirVector)
                .setSkillType(chainFromAbility.getParams().getSkillType());

        spawnAbility(abilityType, abilityParams, game);
    }

    public void activateAbility(Ability ability) {
        AbilityActivateAction action = AbilityActivateAction.of(ability);

        gameState.scheduleServerSideAction(action);
    }


    public void onAbilityHitsCreature(CreatureId attackerId, CreatureId targetId, Ability ability) {
        ability.onCreatureHit();

        ability.getParams().getCreaturesAlreadyHit().put(targetId, ability.getParams().getStateTimer().getTime());

        CreatureHitAction action = CreatureHitAction.of(attackerId, targetId, ability);
        gameState.scheduleServerSideAction(action);
    }

}
