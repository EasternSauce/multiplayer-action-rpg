package com.easternsauce.actionrpg.model.ability.crossbowbolt;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.Projectile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class CrossbowBolt extends Projectile {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static CrossbowBolt of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    CrossbowBolt ability = CrossbowBolt.of();
    ability.params = abilityParams.setWidth(1.5f).setHeight(1.5f).setChannelTime(0f).setActiveTime(30f)
      .setStartingRange(1.5f).setTextureName("arrow").setChannelAnimationLooping(true)
      .setActiveAnimationLooping(true).setSpeed(30f);

    ability.context = abilityContext.setBaseDamage(10f);

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
    onProjectileTravelUpdate();
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
    return true;
  }

  @Override
  public Float getStunDuration() {
    return 0.15f;
  }
}
