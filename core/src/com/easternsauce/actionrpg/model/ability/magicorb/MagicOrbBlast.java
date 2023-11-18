package com.easternsauce.actionrpg.model.ability.magicorb;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MagicOrbBlast extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static MagicOrbBlast of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    MagicOrbBlast ability = MagicOrbBlast.of();
    ability.params = abilityParams.setWidth(4f).setHeight(4f).setChannelTime(0f).setActiveTime(0.21f)
      .setTextureName("magic_blast").setChannelAnimationLooping(false).setActiveAnimationLooping(false)
      .setAttackWithoutMoving(true).setCreaturesAlreadyHit(new OrderedMap<>());

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
    return 0.8f;
  }
}
