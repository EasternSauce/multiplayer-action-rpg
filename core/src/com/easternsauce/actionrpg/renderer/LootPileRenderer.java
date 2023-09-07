package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.area.LootPileId;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class LootPileRenderer {
  @Getter
  private LootPileId lootPileId;
  @Getter
  private Sprite sprite;

  public static LootPileRenderer of(LootPileId lootPileId) {
    LootPileRenderer lootPileRenderer = new LootPileRenderer();
    lootPileRenderer.lootPileId = lootPileId;
    return lootPileRenderer;
  }

  public void init(TextureAtlas atlas, CoreGame game) {

    LootPile lootPile = game.getGameState().getLootPiles().get(lootPileId);

    sprite = new Sprite();
    sprite.setRegion(atlas.findRegion("bag"));
    sprite.setSize(lootPile.getWidth(), lootPile.getHeight());
    sprite.setCenter(lootPile.getParams().getPos().getX(), lootPile.getParams().getPos().getY());

  }

  public void render(RenderingLayer renderingLayer, CoreGame game) {
    AreaId currentAreaId = game.getCurrentAreaId();
    LootPile lootPile = game.getGameState().getLootPile(lootPileId);

    if (lootPile != null && currentAreaId.equals(lootPile.getParams().getAreaId())) {
      sprite.draw(renderingLayer.getSpriteBatch());
    }

  }

}
