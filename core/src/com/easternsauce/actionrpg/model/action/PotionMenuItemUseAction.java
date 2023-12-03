package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class PotionMenuItemUseAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();

  private Integer slotIndex;

  public static PotionMenuItemUseAction of(EntityId<Creature> playerId, Integer slotIndex) {
    PotionMenuItemUseAction action = PotionMenuItemUseAction.of();
    action.playerId = playerId;
    action.slotIndex = slotIndex;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    if (playerConfig != null && slotIndex != null) {
      Creature creature = game.getCreature(playerId);

      if (!creature.isEmpty() && creature.getParams().getPotionMenuItems().containsKey(slotIndex)) {
        Item item = creature.getParams().getPotionMenuItems().get(slotIndex);

        if (item != null && item.getTemplate().getConsumable()) {
          processUseItem(creature, item, game);

          if (item.getQuantity() == 1) {
            creature.getParams().getPotionMenuItems().remove(slotIndex);
          } else {
            item.setQuantity(item.getQuantity() - 1);
          }
        }
      }
    }
  }

  // TODO: make common method
  private void processUseItem(Creature creature, Item item, CoreGame game) {
    if (item.getTemplate().getId().equals("lifePotion")) {
      creature.applyEffect(CreatureEffect.LIFE_REGENERATION, 3f, game);
    } else if (item.getTemplate().getId().equals("manaPotion")) {
      creature.applyEffect(CreatureEffect.MANA_REGENERATION, 3f, game);
    }
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
