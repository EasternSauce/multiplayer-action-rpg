package com.easternsauce.actionrpg.model.ability.shockwave;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Shockwave extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static Shockwave of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    Shockwave ability = Shockwave.of();

    ability.params = abilityParams.setWidth(10f).setHeight(10f).setChannelTime(0.17f).setActiveTime(0.306f)
      .setTextureName("holy_explosion").setChannelAnimationLooping(false)
      .setActiveAnimationLooping(false).setAttackWithoutMoving(true)
      .setCreaturesAlreadyHit(new OrderedMap<>());

    ability.context = abilityContext.setBaseDamage(45f);

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
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Float getStunDuration() {
    return 0.3f;
  }

  @Override
  public boolean isBlockable() {
    return false;
  }
}
