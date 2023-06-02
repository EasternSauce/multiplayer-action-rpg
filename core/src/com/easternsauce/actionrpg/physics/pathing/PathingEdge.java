package com.easternsauce.actionrpg.physics.pathing;

import com.easternsauce.actionrpg.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PathingEdge {
    Float weight;
    Vector2Int neighborPos;
}
