package com.easternsauce.actionrpg.renderer.creature;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.util.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CreatureRenderer {
  private CreatureId creatureId;
  @Getter
  private CreatureSprite creatureSprite;

  public static CreatureRenderer of(CreatureId creatureId) {
    CreatureRenderer creatureRenderer = new CreatureRenderer();

    creatureRenderer.creatureId = creatureId;
    creatureRenderer.creatureSprite = CreatureSprite.of(creatureId);
    return creatureRenderer;
  }

  public void update(CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    creatureSprite.updatePosition(creature);
    creatureSprite.updateSize(creature);

    if (creature.isAlive()) {
      creatureSprite.updateForAliveCreature(creature, game);
    } else {
      creatureSprite.updateForDeadCreature(game);
    }
  }

  public void render(RenderingLayer renderingLayer) {
    creatureSprite.render(renderingLayer);
  }

  public void renderLifeBar(RenderingLayer renderingLayer, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (creature != null) {
      float currentLifeBarWidth = LifeBarUtils.LIFE_BAR_WIDTH * creature.getParams().getStats().getLife() / creature.getParams().getStats().getMaxLife();
      float barPosX = LifeBarUtils.getLifeBarPosX(creature);
      float barPosY = LifeBarUtils.getLifeBarPosY(creature, creatureSprite.getWidth());

      LifeBarUtils.renderBar(renderingLayer, barPosX, barPosY, LifeBarUtils.LIFE_BAR_WIDTH, Color.ORANGE);
      LifeBarUtils.renderBar(renderingLayer, barPosX, barPosY, currentLifeBarWidth, Color.RED);
    }
  }

  public void renderCreatureName(RenderingLayer renderingLayer, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    String name = creature.getId().getValue();

    float namePosX = creature.getParams().getPos().getX() - name.length() * 0.16f;
    float namePosY = LifeBarUtils.getLifeBarPosY(creature, creatureSprite.getWidth()) + 1f;

    // world text viewport is not scaled down! so we scale the values every time
    Assets.renderMediumFont(renderingLayer, name, Vector2.of(namePosX * Constants.PPM, namePosY * Constants.PPM), Color.RED);
  }
}