package com.easternsauce.actionrpg.model.ability.lightning;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.ability.util.PointTargetedAbilityUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
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
public class LightningSpark extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static LightningSpark of(AbilityParams abilityParams, AbilityContext abilityContext, CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    LightningSpark ability = LightningSpark.of();

    Vector2 pos = PointTargetedAbilityUtils.calculatePos(
      creature.getParams().getPos().add(abilityParams.getDirVector()),
      creature.getParams().getPos(), creature.getParams().getAreaId(), 7f, game);

    ability.params = abilityParams.setWidth(3f).setHeight(3f).setChannelTime(0f).setActiveTime(0.4f)
      .setTextureName("lightning").setActiveAnimationLooping(true).setAttackWithoutMoving(true)
      .setSkipCreatingBody(true).setDelayedActionTime(0.001f).setPos(pos).setDontOverridePos(true);

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
  public void onStarted(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.1f, game);
    creature.stopMoving();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {

  }

  @Override
  public void onDelayedAction(CoreGame game) {
    // find the closest enemy, and if they are within distance, and haven't been hit yet, then put node over them
    Set<EntityId<Creature>> excluded = new ConcurrentSkipListSet<>(getParams().getCreaturesAlreadyHit().keySet());
    excluded.add(getContext().getCreatureId());

    Creature targetCreature = game.getCreature(
      game.getGameState().accessCreatures().getAliveCreatureIdClosestTo(getParams().getPos(), 13f, excluded, game));

    if (!(targetCreature.isEmpty())) {
      Vector2 pos = getParams().getPos();
      Vector2 pos1 = targetCreature.getParams().getPos();


      if (
        !game.isLineBetweenPointsObstructedByTerrain(getParams().getAreaId(), pos,
          pos1)) {

        game.getGameState().accessAbilities()
          .onAbilityHitsCreature(getContext().getCreatureId(), targetCreature.getId(), getParams().getId(),
            targetCreature.getParams().getPos(), game);

        getParams().getCreaturesAlreadyHit().put(targetCreature.getId(), getParams().getStateTimer().getTime());

        game.chainAnotherAbility(this, AbilityType.LIGHTNING_CHAIN, params.getDirVector(),
          ChainAbilityParams.of().setChainToPos(targetCreature.getParams().getPos())
          // this pos is later changed, TODO: move it to other param?
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
