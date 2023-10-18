package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class SkillPickerMenuSlotChangeAction extends GameStateAction {
  private EntityId<Creature> playerId = NullCreatureId.of();

  private SkillType skillType;

  public static SkillPickerMenuSlotChangeAction of(EntityId<Creature> playerId, SkillType skillType) {
    SkillPickerMenuSlotChangeAction action = SkillPickerMenuSlotChangeAction.of();
    action.playerId = playerId;
    action.skillType = skillType;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    PlayerConfig playerConfig = game.getGameState().getPlayerConfig(playerId);

    playerConfig.getSkillMenuSlots().put(playerConfig.getSkillMenuPickerSlotBeingChanged(), skillType);
    playerConfig.setSkillMenuPickerSlotBeingChanged(null);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(playerId);
  }
}
