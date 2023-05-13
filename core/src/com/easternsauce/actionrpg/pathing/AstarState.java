package com.easternsauce.actionrpg.pathing;

import com.easternsauce.actionrpg.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AstarState {
    Map<Vector2Int, AstarNode> astarGraph;
    Set<Vector2Int> openSet;
    Set<Vector2Int> closedSet;
    Vector2Int finishPos;
    Boolean foundPath;
    Boolean isGaveUp;
}
