package com.easternsauce.actionrpg.renderer.hud;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.hud.checkpointmenu.CheckpointMenuRenderer;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.InventoryWindowRenderer;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.ItemOnCursorRenderer;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.PotionMenuRenderer;
import com.easternsauce.actionrpg.renderer.hud.itempickupmenu.ItemPickupMenuRenderer;
import com.easternsauce.actionrpg.renderer.hud.skillmenu.SkillMenuRenderer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class HudRenderer {
  @Getter
  private final FpsCounterRenderer fpsCounterRenderer = FpsCounterRenderer.of();
  @Getter
  private final InventoryWindowRenderer inventoryWindowRenderer = InventoryWindowRenderer.of();
  @Getter
  private final PlayerStatBarsRenderer playerStatBarsRenderer = PlayerStatBarsRenderer.of();
  @Getter
  private final RespawnMessageRenderer respawnMessageRenderer = RespawnMessageRenderer.of();
  @Getter
  private final ServerRunningMessageRenderer serverRunningMessageRenderer = ServerRunningMessageRenderer.of();
  @Getter
  private final SkillMenuRenderer skillMenuRenderer = SkillMenuRenderer.of();
  @Getter
  private final ItemPickupMenuRenderer pickUpMenuRenderer = ItemPickupMenuRenderer.of();
  @Getter
  private final CheckpointMenuRenderer checkpointMenuRenderer = CheckpointMenuRenderer.of();
  @Getter
  private final PotionMenuRenderer potionMenuRenderer = PotionMenuRenderer.of();

  public void init(TextureAtlas atlas) {
    inventoryWindowRenderer.init(atlas);
  }

  public void render(CoreGame game) {
    RenderingLayer renderingLayer = game.getHudRenderingLayer();

    renderingLayer.begin();

    game.getChat().render(renderingLayer);

    fpsCounterRenderer.render(renderingLayer);

    if (!game.getGameState().getThisClientPlayerId().isEmpty()) {
      Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

      skillMenuRenderer.render(renderingLayer, game);

      respawnMessageRenderer.render(player, renderingLayer);

      playerStatBarsRenderer.render(player, renderingLayer);
    }

    pickUpMenuRenderer.render(renderingLayer, game);

    checkpointMenuRenderer.render(renderingLayer, game);

    potionMenuRenderer.render(renderingLayer, game);

    inventoryWindowRenderer.render(renderingLayer, game);

    ItemOnCursorRenderer.render(renderingLayer, game);

    renderingLayer.end();
  }

}
