package com.easternsauce.actionrpg.physics.event;

import com.easternsauce.actionrpg.model.id.CheckpointId;
import com.easternsauce.actionrpg.model.id.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class CreatureHitsCheckpointEvent implements PhysicsEvent {
  @Getter
  private CreatureId creatureId;
  @Getter
  private CheckpointId checkpointId;
}
