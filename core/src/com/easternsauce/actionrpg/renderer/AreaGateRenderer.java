package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaGateId;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
public class AreaGateRenderer {
  @Getter
  private EntityId<AreaGate> areaGateId = NullAreaGateId.of();

  @Getter
  private Sprite sprite;

  public static AreaGateRenderer of(EntityId<AreaGate> areaGateId) {
    AreaGateRenderer areaGateRenderer = AreaGateRenderer.of();
    areaGateRenderer.areaGateId = areaGateId;
    return areaGateRenderer;

  }

  public void init(TextureAtlas atlas, CoreGame game) {
    sprite = new Sprite();
    sprite.setRegion(atlas.findRegion("downarrow"));

    AreaGate areaGate = game.getGameState().getAreaGate(areaGateId);

    sprite.setSize(areaGate.getWidth(), areaGate.getHeight());
    sprite.setCenter(areaGate.getPos().getX(), areaGate.getPos().getY());

  }

  public void render(RenderingLayer renderingLayer, CoreGame game) {
    EntityId<Area> currentAreaId = game.getCurrentAreaId();

    AreaGate areaGate = game.getGameState().getAreaGate(areaGateId);

    if (areaGate != null && currentAreaId.equals(areaGate.getAreaId())) { // TODO: add NullAreaGate?
      sprite.draw(renderingLayer.getSpriteBatch());
    }
  }
}
