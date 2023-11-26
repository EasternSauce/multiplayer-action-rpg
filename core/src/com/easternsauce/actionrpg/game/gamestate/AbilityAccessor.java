package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.action.CreatureHitByAbilityAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.Constants;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AbilityAccessor {
  @Getter
  private GameState gameState;
  @Getter
  private GameStateDataHolder dataHolder;

  public Ability getAbilityBySkillType(EntityId<Creature> creatureId, SkillType skillType) {
    Optional<Ability> first = getData().getAbilities().values().stream().filter(
      ability -> ability.getContext().getCreatureId().equals(creatureId) &&
        ability.getContext().getSkillType() == skillType).findFirst();

    return first.orElse(null);
  }

  private GameStateData getData() {
    return dataHolder.getData();
  }

  public Set<EntityId<Ability>> getAbilitiesWithinRange(Creature player) {
    return getAbilities().keySet().stream().filter(abilityId -> {
      Ability ability = getAbility(abilityId);
      if (player.getParams().getPos() != null && ability.getParams().getPos() != null) {
        return ability.getParams().getPos().distance(player.getParams().getPos()) < Constants.CLIENT_GAME_UPDATE_RANGE;
      }
      return false;
    }).collect(Collectors.toSet());
  }

  public Map<EntityId<Ability>, Ability> getAbilities() {
    return getData().getAbilities();
  }

  public void chainAnotherAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 dirVector, ChainAbilityParams chainAbilityParams, CoreGame game) {
    Creature creature = game.getCreature(chainFromAbility.getContext().getCreatureId());

    if ((creature.isAlive() || chainFromAbility.isAbleToChainAfterCreatureDeath())) {
      EntityId<Ability> abilityId = EntityId.of("Ability_" + (int) (Math.random() * 10000000));

      Map<EntityId<Creature>, Float> creaturesAlreadyHit = new OrderedMap<>(
        chainFromAbility.getParams().getCreaturesAlreadyHit());

      Vector2 chainFromPos = chainFromAbility.getParams().getPos();

      AbilityParams abilityParams = AbilityParams.of().setId(abilityId)
        .setAreaId(chainFromAbility.getParams().getAreaId())
        .setCreaturesAlreadyHit(creaturesAlreadyHit).setChainFromPos(chainFromPos)
        .setChainToPos(chainAbilityParams.getChainToPos()).setDirVector(dirVector)
        .setOverrideScale(chainAbilityParams.getOverrideScale())
        .setOverrideActiveDuration(chainAbilityParams.getOverrideDuration())
        .setOverrideMaximumRange(chainAbilityParams.getOverrideMaximumRange())
        .setOverrideSpeed(chainAbilityParams.getOverrideSpeed())
        .setOverrideStunDuration(chainAbilityParams.getOverrideStunDuration())
        .setDamagingHitCreaturesHitCounter(chainFromAbility.getParams().getDamagingHitCreaturesHitCounter());

      spawnAbility(abilityType, abilityParams, chainFromAbility.getContext(), game);
    }
  }

  public void spawnAbility(AbilityType abilityType, AbilityParams abilityParams, AbilityContext abilityContext, CoreGame game) {
    Creature creature = gameState.accessCreatures().getCreature(abilityContext.getCreatureId());

    Ability ability = abilityType.getFactoryMapping().apply(abilityParams, abilityContext, game);

    initializeAbility(creature, ability, game);

  }

  private void initializeAbility(Creature creature, Ability ability, CoreGame game) {
    game.getAbilities().put(ability.getParams().getId(), ability);

    game.getEventProcessor().getAbilityModelsToBeCreated().add(ability.getParams().getId());

    ability.init(game);

    creature.onAbilityPerformed(ability);
  }

  public void onAbilityHitsCreature(EntityId<Creature> attackerId, EntityId<Creature> targetId, EntityId<Ability> abilityId, Vector2 contactPoint, CoreGame game) {
    Ability ability = game.getAbility(abilityId);

    ability.onCreatureHit(targetId, game);

    ability.getParams().getCreaturesAlreadyHit().put(targetId, ability.getParams().getStateTimer().getTime());

    ability.getParams().getDamagingHitCreaturesHitCounter().incrementForCreature(targetId);

    CreatureHitByAbilityAction action = CreatureHitByAbilityAction.of(attackerId, targetId, ability,
      ability.getParams().getDamagingHitCreaturesHitCounter().getCount(targetId), contactPoint);

    gameState.scheduleServerSideAction(action);
  }

  public Ability getAbility(EntityId<Ability> abilityId) {
    if (abilityId.isEmpty() || !getData().getAbilities().containsKey(abilityId)) {
      return NullAbility.of();
    } else {
      return getData().getAbilities().get(abilityId);
    }
  }

}
