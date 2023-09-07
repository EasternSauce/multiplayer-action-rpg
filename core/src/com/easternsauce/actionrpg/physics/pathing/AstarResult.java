package com.easternsauce.actionrpg.physics.pathing;

import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AstarResult {
  private List<Vector2> path;
  private boolean gaveUp;
}