package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.CheckpointId;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class CheckpointRenderer {
  @Getter
  private CheckpointId checkpointId;

  @Getter
  private Sprite sprite;

  public static CheckpointRenderer of(CheckpointId checkpointId) {

    CheckpointRenderer checkpointRenderer = CheckpointRenderer.of();
    checkpointRenderer.checkpointId = checkpointId;
    return checkpointRenderer;

  }

  public void init(TextureAtlas atlas, CoreGame game) {
    sprite = new Sprite();
    sprite.setRegion(atlas.findRegion("goblet"));

    Checkpoint checkpoint = game.getGameState().getCheckpoint(checkpointId);

    sprite.setSize(checkpoint.getWidth(), checkpoint.getHeight());
    sprite.setCenter(checkpoint.getPos().getX(), checkpoint.getPos().getY());

  }

  public void render(RenderingLayer renderingLayer, CoreGame game) {
    AreaId currentAreaId = game.getCurrentAreaId();

    Checkpoint checkpoint = game.getGameState().getCheckpoint(checkpointId);

    if (currentAreaId.equals(checkpoint.getAreaId())) {
      sprite.draw(renderingLayer.getSpriteBatch());
    }
  }
}
