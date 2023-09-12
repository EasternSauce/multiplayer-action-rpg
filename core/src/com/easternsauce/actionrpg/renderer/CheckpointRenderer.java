package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.CheckpointId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(staticName = "of")
public class CheckpointRenderer {
  @Getter
  private CheckpointId checkpointId;

  @Getter
  private Sprite regularSprite;

  @Getter
  private Sprite litSprite;

  @Setter
  private float lastCheckpointSetTime;

  public static CheckpointRenderer of(CheckpointId checkpointId) {
    CheckpointRenderer checkpointRenderer = CheckpointRenderer.of();
    checkpointRenderer.checkpointId = checkpointId;
    return checkpointRenderer;

  }

  public void init(TextureAtlas atlas, CoreGame game) {
    regularSprite = new Sprite();
    regularSprite.setRegion(atlas.findRegion("goblet"));

    litSprite = new Sprite();
    litSprite.setRegion(atlas.findRegion("goblet_lit"));

    Checkpoint checkpoint = game.getGameState().getCheckpoint(checkpointId);

    regularSprite.setSize(checkpoint.getWidth(), checkpoint.getHeight());
    regularSprite.setCenter(checkpoint.getPos().getX(), checkpoint.getPos().getY());

    litSprite.setSize(checkpoint.getWidth(), checkpoint.getHeight());
    litSprite.setCenter(checkpoint.getPos().getX(), checkpoint.getPos().getY());

  }

  public void render(RenderingLayer renderingLayer, CoreGame game) {
    AreaId currentAreaId = game.getCurrentAreaId();

    Checkpoint checkpoint = game.getGameState().getCheckpoint(checkpointId);


    if (currentAreaId.equals(checkpoint.getAreaId())) {
      if (lastCheckpointSetTime + 5f > game.getGameState().getTime()) {
        litSprite.draw(renderingLayer.getSpriteBatch());
      } else {
        regularSprite.draw(renderingLayer.getSpriteBatch());
      }
    }
  }
}
