package com.easternsauce.actionrpg.renderer.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.AnimationRenderer;
import com.easternsauce.actionrpg.renderer.AnimationSpec;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureStunnedAnimationRenderer {
  @Getter
  private final AnimationRenderer animationRenderer = AnimationRenderer.of(
    AnimationSpec.of(60, 30, 3f, 1.5f, 0.045f, 8, "stunned", true));

  public void render(EntityId<Creature> creatureId, float spriteWidth, RenderingLayer renderingLayer, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature.isEffectActive(CreatureEffect.STUN, game)) {
      float posX = creature.getParams().getPos().getX() - 1.5f;
      float posY = LifeBarUtils.getLifeBarPosY(creature, spriteWidth) - 1f;

      Vector2 pos = Vector2.of(posX, posY);

      float stunTimeSinceStarted = creature.getTimeSinceStarted(CreatureEffect.STUN, game);

      animationRenderer.render(pos, stunTimeSinceStarted, renderingLayer);
    }
  }

}
