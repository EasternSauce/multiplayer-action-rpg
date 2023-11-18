package com.easternsauce.actionrpg.model.ability.meteor;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class MeteorCall extends AttachedAbility {
  @Getter
  protected AbilityParams params;
  @Getter
  protected AbilityContext context;

  public static MeteorCall of(AbilityParams abilityParams, AbilityContext abilityContext, @SuppressWarnings("unused") CoreGame game) {
    MeteorCall ability = MeteorCall.of();
    ability.params = abilityParams.setChannelTime(0f).setActiveTime(1f);

    ability.context = abilityContext;

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
    centerPositionOnPlayer(game);

    Creature creature = game.getCreature(getContext().getCreatureId());

    creature.stopMoving();

    updateAttachedAbilityPosition(game);
  }

  @Override
  public void onStarted(CoreGame game) {
    Creature creature = game.getCreature(getContext().getCreatureId());
    Vector2 creaturePos = creature.getParams().getPos();

    List<Vector2> positions = new LinkedList<>();

    for (int i = 0; i < 5; i++) {
      boolean found = false;

      while (!found) {
        float x = creaturePos.getX() + game.getGameState().getRandomGenerator().nextFloat() * 20f - 10f;
        float y = creaturePos.getY() + game.getGameState().getRandomGenerator().nextFloat() * 20f - 10f;

        Vector2 candidatePos = Vector2.of(x, y);

        Optional<Vector2> any = positions.stream().filter(pos -> pos.distance(candidatePos) < 4f).findAny();

        if (!any.isPresent()) {
          found = true;
          positions.add(candidatePos);
        }
      }
    }

    positions.forEach(pos -> {
      game.chainAnotherAbility(this, AbilityType.METEOR, getParams().getDirVector(),
        ChainAbilityParams.of().setChainToPos(pos)); // TODO: we cannot override damage here, otherwise comet tail does damage too, how do we pass this override?
    });
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }

  @Override
  public boolean isDamagingSkillNotAllowedWhenActive() {
    return true;
  }

}
