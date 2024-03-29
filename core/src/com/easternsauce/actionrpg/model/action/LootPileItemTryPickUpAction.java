package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.InventoryWindowConsts;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class LootPileItemTryPickUpAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();

  private Item item;

  public static LootPileItemTryPickUpAction of(EntityId<Creature> playerId, Item item) {
    LootPileItemTryPickUpAction action = LootPileItemTryPickUpAction.of();
    action.playerId = playerId;
    action.item = item;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    Creature creature = game.getCreature(playerId);

    if (creature.isAlive()) {
      Map<Integer, Item> inventoryItems = creature.getParams().getInventoryItems();

      Integer existingStackableSlot = null;

      if (item != null && item.getTemplate().getStackable()) {
        for (int i = 0; i < InventoryWindowConsts.INVENTORY_TOTAL_SLOTS; i++) {
          if (inventoryItems.containsKey(i) &&
            inventoryItems.get(i).getTemplate().getId().equals(item.getTemplate().getId())) {
            existingStackableSlot = i;
            break;
          }
        }
      }

      if (existingStackableSlot != null) {
        LootPile lootPile = game.getGameState().getLootPile(item.getLootPileId());

        if (lootPile != null) {
          inventoryItems.get(existingStackableSlot)
            .setQuantity(inventoryItems.get(existingStackableSlot).getQuantity() + item.getQuantity());

          lootPile.getParams().getItems().remove(item);
          if (lootPile.getParams().getItems().isEmpty()) {
            lootPile.getParams().setFullyLooted(true);
          }
        }
      } else {
        Integer freeSlot = null;
        for (int i = 0; i < InventoryWindowConsts.INVENTORY_TOTAL_SLOTS; i++) {
          if (!inventoryItems.containsKey(i)) {
            freeSlot = i;
            break;
          }
        }

        LootPile lootPile = game.getGameState().getLootPile(item.getLootPileId());

        if (freeSlot != null && lootPile != null) {
          inventoryItems.put(freeSlot, Item.of().setTemplate(item.getTemplate()).setQuantity(item.getQuantity())
            .setQualityModifier(item.getQualityModifier()).setGrantedSkills(item.getGrantedSkills()).setLootPileId(null));

          lootPile.getParams().getItems().remove(item);
          if (lootPile.getParams().getItems().isEmpty()) {
            lootPile.getParams().setFullyLooted(true);
          }
        }
      }
    }

  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getGameState().getLootPiles().get(item.getLootPileId());
  }
}
