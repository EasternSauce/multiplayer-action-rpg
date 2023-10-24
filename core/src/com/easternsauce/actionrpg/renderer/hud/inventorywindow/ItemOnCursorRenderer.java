package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.icons.IconRetriever;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
public class ItemOnCursorRenderer {
  public static void render(RenderingLayer renderingLayer, CoreGame game) {
    IconRetriever iconRetriever = game.getEntityManager().getGameEntityRenderer().getIconRetriever();

    Creature player = game.getCreature(game.getGameState().getThisClientPlayerId());

    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

    if (!player.isNull()) {
      Map<Integer, Item> inventoryItems = player.getParams().getInventoryItems();
      Map<Integer, Item> equipmentItems = player.getParams().getEquipmentItems();
      Map<Integer, Item> potionMenuItems = player.getParams().getPotionMenuItems();

      float mouseX = game.hudMousePos().getX();
      float mouseY = game.hudMousePos().getY();

      if (playerConfig.getInventoryItemBeingMoved() != null &&
        inventoryItems.containsKey(playerConfig.getInventoryItemBeingMoved())) {

        renderInventoryItemBeingMovedOnCursor(mouseX, mouseY, inventoryItems, iconRetriever, playerConfig,
          renderingLayer);
      }
      if (playerConfig.getEquipmentItemBeingMoved() != null &&
        equipmentItems.containsKey(playerConfig.getEquipmentItemBeingMoved())) {
        renderEquipmentItemBeingMovedOnCursor(mouseX, mouseY, equipmentItems, iconRetriever, playerConfig,
          renderingLayer);
      }
      if (playerConfig.getPotionMenuItemBeingMoved() != null &&
        potionMenuItems.containsKey(playerConfig.getPotionMenuItemBeingMoved())) {
        renderPotionMenuItemBeingMovedOnCursor(mouseX, mouseY, potionMenuItems, iconRetriever, playerConfig,
          renderingLayer);
      }
    }
  }

  private static void renderInventoryItemBeingMovedOnCursor(float mouseX, float mouseY, Map<Integer, Item> inventoryItems, IconRetriever iconRetriever, PlayerConfig playerConfig, RenderingLayer renderingLayer) {
    if (inventoryItems.containsKey(playerConfig.getInventoryItemBeingMoved())) {
      Item item = inventoryItems.get(playerConfig.getInventoryItemBeingMoved());

      Vector2Int iconPos = item.getTemplate().getIconPos();

      renderingLayer.getSpriteBatch()
        .draw(iconRetriever.getIcon(iconPos.getX(), iconPos.getY()), mouseX - InventoryWindowConsts.SLOT_SIZE / 2f,
          mouseY - InventoryWindowConsts.SLOT_SIZE / 2f, InventoryWindowConsts.SLOT_SIZE,
          InventoryWindowConsts.SLOT_SIZE);
    }
  }

  private static void renderEquipmentItemBeingMovedOnCursor(float mouseX, float mouseY, Map<Integer, Item> equipmentItems, IconRetriever iconRetriever, PlayerConfig playerConfig, RenderingLayer renderingLayer) {
    Vector2Int iconPos = equipmentItems.get(playerConfig.getEquipmentItemBeingMoved()).getTemplate().getIconPos();

    renderingLayer.getSpriteBatch()
      .draw(iconRetriever.getIcon(iconPos.getX(), iconPos.getY()), mouseX - InventoryWindowConsts.SLOT_SIZE / 2f,
        mouseY - InventoryWindowConsts.SLOT_SIZE / 2f, InventoryWindowConsts.SLOT_SIZE,
        InventoryWindowConsts.SLOT_SIZE);
  }

  private static void renderPotionMenuItemBeingMovedOnCursor(float mouseX, float mouseY, Map<Integer, Item> potionMenuItems, IconRetriever iconRetriever, PlayerConfig playerConfig, RenderingLayer renderingLayer) {
    Vector2Int iconPos = potionMenuItems.get(playerConfig.getPotionMenuItemBeingMoved()).getTemplate().getIconPos();

    renderingLayer.getSpriteBatch()
      .draw(iconRetriever.getIcon(iconPos.getX(), iconPos.getY()), mouseX - InventoryWindowConsts.SLOT_SIZE / 2f,
        mouseY - InventoryWindowConsts.SLOT_SIZE / 2f, InventoryWindowConsts.SLOT_SIZE,
        InventoryWindowConsts.SLOT_SIZE);
  }
}
