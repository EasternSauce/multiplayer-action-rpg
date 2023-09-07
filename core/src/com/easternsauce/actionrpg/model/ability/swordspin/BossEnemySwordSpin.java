package com.easternsauce.actionrpg.model.ability.swordspin;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class BossEnemySwordSpin extends SwordSpinBase {
  public static BossEnemySwordSpin of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
    BossEnemySwordSpin ability = BossEnemySwordSpin.of();
    ability.params = abilityParams.setWidth(6f).setHeight(6f).setChannelTime(0f).setActiveTime(4f).setStartingRange(4f).setTextureName("sword").setBaseDamage(42f).setChannelAnimationLooping(false).setActiveAnimationLooping(false).setDirVector(abilityParams.getDirVector().withRotatedDegAngle(90));
    return ability;
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    updateAttachedAbilityPosition(game);

    getParams().setDirVector(getParams().getDirVector().withRotatedDegAngle(-10));

    Set<CreatureId> creaturesHitRemove = new HashSet<>();

    getParams().getCreaturesAlreadyHit().forEach((creatureId, time) -> {
      if (time < getParams().getStateTimer().getTime() - 0.4f) {
        creaturesHitRemove.add(creatureId);
      }
    });

    creaturesHitRemove.forEach(creatureId -> getParams().getCreaturesAlreadyHit().remove(creatureId));
  }

  @Override
  public Float getStunDuration() {
    return 0.3f;
  }

}
