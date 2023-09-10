package com.easternsauce.actionrpg.model.ability.lightning;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.ability.util.PointTargetedAbilityUtils;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.NullCreature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class LightningSpark extends Ability {
  @Getter
  protected AbilityParams params;

  public static LightningSpark of(AbilityParams abilityParams, CoreGame game) {
    Creature creature = game.getCreature(abilityParams.getCreatureId());

    LightningSpark ability = LightningSpark.of();

    Vector2 pos = PointTargetedAbilityUtils.calculatePos(
      creature.getParams().getPos().add(abilityParams.getDirVector()),
      creature.getParams().getPos(), creature.getParams().getAreaId(), 7f, game);

    ability.params = abilityParams.setWidth(3f).setHeight(3f).setChannelTime(0f).setActiveTime(0.4f)
      .setTextureName("lightning").setBaseDamage(11f).setActiveAnimationLooping(true).setAttackWithoutMoving(true)
      .setSkipCreatingBody(true).setDelayedActionTime(0.001f).setPos(pos).setDontOverridePos(true);

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
    Creature creature = game.getCreature(getParams().getCreatureId());
    creature.applyEffect(CreatureEffect.SELF_STUN, 0.1f, game);
    creature.stopMoving();
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {

  }

  @Override
  public void onDelayedAction(CoreGame game) {
    // find the closest enemy, and if they are within distance, and haven't been hit yet, then put node over them
    Set<CreatureId> excluded = new HashSet<>(getParams().getCreaturesAlreadyHit().keySet());
    excluded.add(getParams().getCreatureId());

    Creature targetCreature = game.getCreature(
      game.getGameState().accessCreatures().getAliveCreatureIdClosestTo(getParams().getPos(), 13f, excluded, game));

    if (!(targetCreature instanceof NullCreature)) {
      Vector2 pos = getParams().getPos();
      Vector2 pos1 = targetCreature.getParams().getPos();


      if (
        !game.isLineBetweenPointsObstructedByTerrain(getParams().getAreaId(), pos,
          pos1)) {

        game.getGameState().accessAbilities()
          .onAbilityHitsCreature(getParams().getCreatureId(), targetCreature.getId(), getParams().getId(),
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
    ConcurrentSkipListMap<Integer, Float> scalings = new ConcurrentSkipListMap<>();
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
