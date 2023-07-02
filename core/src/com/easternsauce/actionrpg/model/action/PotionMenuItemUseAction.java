package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class PotionMenuItemUseAction extends GameStateAction {
    private CreatureId playerId;

    private Integer slotIndex;

    public static PotionMenuItemUseAction of(CreatureId creatureId, Integer slotIndex) {
        PotionMenuItemUseAction action = PotionMenuItemUseAction.of();
        action.playerId = creatureId;
        action.slotIndex = slotIndex;
        return action;
    }

    @Override
    public void applyToGame(CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

        if (playerConfig != null && slotIndex != null) {
            Creature creature = game.getGameState().accessCreatures().getCreature(playerId);
            Item item = creature.getParams().getPotionMenuItems().get(slotIndex);

            if (item != null && item.getTemplate().getIsConsumable()) {
                processUseItem(creature, item, game);

                if (item.getQuantity() == 1) {
                    creature.getParams().getPotionMenuItems().remove(slotIndex);
                } else {
                    item.setQuantity(item.getQuantity() - 1);
                }
            }
        }
    }

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(playerId);
    }

    // TODO: make common method
    private void processUseItem(Creature creature, Item item, CoreGame game) {
        if (item.getTemplate().getId().equals("lifePotion")) {
            creature.applyEffect(CreatureEffect.LIFE_REGENERATION, 3f, game);
        } else if (item.getTemplate().getId().equals("manaPotion")) {
            creature.applyEffect(CreatureEffect.MANA_REGENERATION, 3f, game);
        }
    }
}
