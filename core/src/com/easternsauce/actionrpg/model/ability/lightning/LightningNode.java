package com.easternsauce.actionrpg.model.ability.lightning;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class LightningNode extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static LightningNode of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    LightningNode ability = LightningNode.of();
    ability.params = abilityParams.setWidth(3f).setHeight(3f).setChannelTime(0f).setActiveTime(0.4f)
      .setTextureName("lightning").setActiveAnimationLooping(true).setAttackWithoutMoving(true)
      .setSkipCreatingBody(true).setDelayedActionTime(0.05f);

    ability.context = abilityContext.setBaseDamage(11f);

    return ability;
  }

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {

  }

  @Override
  public void onDelayedAction(CoreGame game) {
    // find the closest enemy, and if they are within distance, and haven't been hit yet, then start node over them
    Set<EntityId<Creature>> excluded = new ConcurrentSkipListSet<>(getParams().getCreaturesAlreadyHit().keySet());
    excluded.add(getContext().getCreatureId());

    Creature targetCreature = game.getCreature(
      game.getGameState().accessCreatures().getAliveCreatureIdClosestTo(getParams().getPos(), 13f, excluded, game));

    if (!(targetCreature.isNull())) {

      if (getParams().getCreaturesAlreadyHit().size() <= 10 &&
        !game.isLineBetweenPointsObstructedByTerrain(getParams().getAreaId(), getParams().getPos(),
          targetCreature.getParams().getPos())) {

        game.getGameState().accessAbilities()
          .onAbilityHitsCreature(getContext().getCreatureId(), targetCreature.getId(), getParams().getId(),
            targetCreature.getParams().getPos(), game);

        getParams().getCreaturesAlreadyHit().put(targetCreature.getId(), getParams().getStateTimer().getTime());

        game.chainAnotherAbility(this, AbilityType.LIGHTNING_CHAIN, params.getDirVector(),
          ChainAbilityParams.of().setChainToPos(targetCreature.getParams().getPos())
          // TODO: this pos is later changed, move it to other param?
        );

        game.chainAnotherAbility(this, AbilityType.LIGHTNING_NODE, params.getDirVector(),
          ChainAbilityParams.of().setChainToPos(targetCreature.getParams().getPos()));
      }
    }
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Map<Integer, Float> levelScalings() {
    OrderedMap<Integer, Float> scalings = new OrderedMap<>();
    scalings.put(1, 1.0f);
    scalings.put(2, 1.1f);
    scalings.put(3, 1.2f);
    return scalings;
  }

  @Override
  public Float getStunDuration() {
    return 0.05f;
  }
}
