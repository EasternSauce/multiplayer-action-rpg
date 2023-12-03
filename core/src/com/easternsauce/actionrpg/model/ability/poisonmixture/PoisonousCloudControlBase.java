package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Getter;

public class PoisonousCloudControlBase extends Ability {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;
  int currentCloudSet = 0;

  @Override
  public Boolean isRanged() {
    return true;
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    float[] cloudSetSpawnTimes = {0f, 0.4f, 1.2f};

    float[] cloudSetSpawnCounts = {3, 7, 11};

    float[] cloudSetDistances = {1f, 3f, 5f};

    float totalDuration = 3.2f;

    if (currentCloudSet < cloudSetSpawnTimes.length &&
      getParams().getStateTimer().getTime() > cloudSetSpawnTimes[currentCloudSet]) {
      for (int i = 0; i < cloudSetSpawnCounts[currentCloudSet]; i++) {
        float x = getParams().getPos().getX() +
          (float) Math.sin(2 * Math.PI / cloudSetSpawnCounts[currentCloudSet] * i) * cloudSetDistances[currentCloudSet];
        float y = getParams().getPos().getY() +
          (float) Math.cos(2 * Math.PI / cloudSetSpawnCounts[currentCloudSet] * i) * cloudSetDistances[currentCloudSet];

        game.chainAnotherAbility(this, AbilityType.POISONOUS_CLOUD, getParams().getDirVector(),
          ChainAbilityParams.of().setChainToPos(Vector2.of(x, y))
            .setOverrideDuration(totalDuration - cloudSetSpawnTimes[currentCloudSet]).setOverrideScale(0.8f));
      }

      currentCloudSet += 1;
    }

    if (currentCloudSet >= cloudSetSpawnTimes.length) {
      deactivate();
    }
  }

  @Override
  protected boolean isWeaponAttack() {
    return true;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }
}
