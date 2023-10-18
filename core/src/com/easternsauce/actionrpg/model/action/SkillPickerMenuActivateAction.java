package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SkillPickerMenuActivateAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();

  private Integer slotNum;

  public static SkillPickerMenuActivateAction of(EntityId<Creature> playerId, Integer slotNum) {
    SkillPickerMenuActivateAction action = SkillPickerMenuActivateAction.of();
    action.playerId = playerId;
    action.slotNum = slotNum;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    playerConfig.setSkillMenuPickerSlotBeingChanged(slotNum);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
