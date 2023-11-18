package com.easternsauce.actionrpg.model.ability.fireball;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Fireball extends Projectile {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static Fireball of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Creature creature = game.getCreature(abilityContext.getCreatureId());

    Fireball ability = Fireball.of();
    ability.params = abilityParams.setWidth(2.5f).setHeight(2.5f).setChannelTime(0f).setActiveTime(30f)
      .setStartingRange(2.5f).setTextureName("fireball").setBaseDamage(20f).setChannelAnimationLooping(false)
      .setActiveAnimationLooping(true).setDelayedActionTime(0.001f).setPos(creature.getParams().getPos());

    ability.context = abilityContext;

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

    if (getParams().getStateTimer().getTime() < 2f) {
      getParams().setSpeed(10f + (getParams().getStateTimer().getTime() / 2f) * 50f);
    } else {
      getParams().setSpeed(50f);
    }
  }

  @Override
  public void onCompleted(CoreGame game) {
    game.chainAnotherAbility(this, AbilityType.EXPLOSION, params.getDirVector(),
      ChainAbilityParams.of().setChainToPos(getParams().getPos()));
  }

  @Override
  public void onCreatureHit(EntityId<Creature> creatureId, CoreGame game) {
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
    return 0.4f;
  }
}
