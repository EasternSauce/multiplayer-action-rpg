package com.easternsauce.actionrpg.physics.pathing;

import com.easternsauce.actionrpg.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AstarState {
    private Map<Vector2Int, AstarNode> astarGraph;
    private Set<Vector2Int> openSet;
    private Set<Vector2Int> closedSet;
    private Vector2Int finishPos;
    private Boolean foundPath;
    private Boolean gaveUp;
}
