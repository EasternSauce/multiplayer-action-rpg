package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.area.CheckpointId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CreatureLeavesCheckpointEvent implements PhysicsEvent {
  @Getter
  private CreatureId creatureId;
  @Getter
  private CheckpointId checkpointId;
}