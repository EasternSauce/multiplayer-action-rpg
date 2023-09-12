package com.easternsauce.actionrpg.renderer.hud.checkpointmenu;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.util.Rect;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(staticName = "of")
public class CheckpointMenuRenderer {
  public void render(RenderingLayer renderingLayer, CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

    if (playerConfig == null || playerConfig.getInventoryVisible()) {
      return;
    }

    float x = game.hudMousePos().getX();
    float y = game.hudMousePos().getY();

    Creature creature = game.getCreature(game.getGameState().getThisClientPlayerId());

    playerConfig.getCheckpointMenuCheckpoints().stream()
      .filter(checkpointId -> {
        creature.getParams();
        return !Objects.equals(checkpointId.getValue(),
          creature.getParams().getCurrentCheckpointId().getValue());
      })
      .findAny()
      .ifPresent(lootPileId -> renderMenu(renderingLayer, x, y));
  }

  private void renderMenu(RenderingLayer renderingLayer, float mouseX, float mouseY) {
    Rect rect = Rect.of(CheckpointMenuConsts.POS_X, CheckpointMenuConsts.POS_Y, CheckpointMenuConsts.WIDTH,
      CheckpointMenuConsts.HEIGHT);
    renderingLayer.getShapeDrawer().filledRectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(),
      Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
    if (rect.contains(mouseX, mouseY)) {
      renderingLayer.getShapeDrawer()
        .rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.ORANGE);
    }

    Assets.renderSmallFont(renderingLayer, "Light fire",
      Vector2.of(rect.getX() + 40f, rect.getY() + 17f), Color.CYAN);
  }

}
