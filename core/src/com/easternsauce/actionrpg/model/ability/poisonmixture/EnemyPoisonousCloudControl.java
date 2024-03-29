package com.easternsauce.actionrpg.model.ability.poisonmixture;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityContext;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.ability.AbilityType;
import com.easternsauce.actionrpg.model.ability.ChainAbilityParams;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyPoisonousCloudControl extends PoisonousCloudControlBase {
  int currentCloudSet = 0;

  public static EnemyPoisonousCloudControl of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    EnemyPoisonousCloudControl ability = EnemyPoisonousCloudControl.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(10f);

    ability.context = abilityContext;

    return ability;
  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {
    centerPositionOnPlayer(game);

    float[] cloudSetSpawnTimes = {0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.2f, 1.6f, 2.0f, 2.4f, 2.8f};

    float[] cloudSetSpawnCounts = {5, 6, 5, 6, 5, 6, 5, 6, 5, 6};

    float[] cloudSetDistances = {2f, 4f, 6f, 8f, 10f, 12f, 14f, 16f, 18f, 20f};

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
            .setOverrideDuration(totalDuration - cloudSetSpawnTimes[currentCloudSet]));
      }

      currentCloudSet += 1;
    }

    if (currentCloudSet >= cloudSetSpawnTimes.length) {
      deactivate();
    }
  }
}
