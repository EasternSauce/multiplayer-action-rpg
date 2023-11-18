package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.util.Vector2;

public abstract class AttachedAbility extends Ability {
  public void updateAttachedAbilityPosition(CoreGame game) {
    Vector2 dirVector;
    if (getParams().getDirVector().len() <= 0) {
      dirVector = Vector2.of(1, 0);
    } else {
      if (getParams().getDirectionalAttachedAbilityRotationShift() != null) {
        dirVector = getParams().getDirVector().rotateDeg(getParams().getDirectionalAttachedAbilityRotationShift());
      } else {
        dirVector = getParams().getDirVector();
      }
    }

    System.out.println("is context null?" + (getContext() == null));
    System.out.println("is null?" + getContext().getCreatureId().isNull());

    Vector2 creaturePos = game.getCreaturePos(getContext().getCreatureId());

    if (creaturePos != null) {
      getParams().setPos(Ability.calculatePosition(creaturePos, dirVector, getParams().getStartingRange()));
    }

    Float theta = dirVector.angleDeg();

    getParams().setRotationAngle(theta);

  }
}
