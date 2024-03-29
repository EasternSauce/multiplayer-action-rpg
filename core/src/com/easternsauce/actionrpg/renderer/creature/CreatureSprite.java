package com.easternsauce.actionrpg.renderer.creature;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.game.CreatureModelAnimation;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureSprite {
  private final Sprite sprite = new Sprite();
  private EntityId<Creature> creatureId = NullCreatureId.of();

  public static CreatureSprite of(EntityId<Creature> creatureId) {
    CreatureSprite creatureSprite = CreatureSprite.of();
    creatureSprite.creatureId = creatureId;
    return creatureSprite;
  }

  public void updatePosition(Creature creature) {
    sprite.setCenter(creature.getParams().getPos().getX(), creature.getParams().getPos().getY());
  }

  public void updateSize(Creature creature) {
    sprite.setSize(creature.getAnimationConfig().getSpriteWidth(), creature.getAnimationConfig().getSpriteHeight());
  }

  public void updateForAliveCreature(Creature creature, CoreGame game) {
    CreatureModelAnimation creatureModelAnimation = game.getEntityManager().getGameEntityRenderer()
      .getCreatureModelAnimations().get(creature.getParams().getTextureName());

    TextureRegion texture;
    if (!creature.getAnimationConfig().getAlwaysLoop() &&
      (!creature.getParams().getMovementParams().getMoving() || creature.isStunned(game))) {
      texture = creatureModelAnimation.getFacingTexture(creature.getParams().getTextureName(),
        creature.getFacingDirection(game));
    } else {
      texture = creatureModelAnimation.getRunningAnimationFrame(creature.getParams().getTextureName(),
        creature.getFacingDirection(game), creature.getParams().getAnimationTimer().getTime());
    }

    sprite.setRotation(0f);
    sprite.setColor(1, 1, 1, 1);

    sprite.setRegion(texture);
  }

  public void updateForDeadCreature(CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    CreatureModelAnimation creatureModelAnimation = game.getEntityManager().getGameEntityRenderer()
      .getCreatureModelAnimations().get(creature.getParams().getTextureName());

    TextureRegion texture = creatureModelAnimation.getFacingTexture(creature.getParams().getTextureName(),
      WorldDirection.RIGHT);

    sprite.setOriginCenter();
    sprite.setRotation(90f);

    sprite.setRegion(texture);
  }

  public void render(RenderingLayer renderingLayer) {
    if (sprite.getTexture() != null) {
      sprite.draw(renderingLayer.getSpriteBatch());
    }
  }

  public float getWidth() {
    return sprite.getWidth();
  }
}
