package com.mygdx.game.pathing;

import com.mygdx.game.physics.TilePos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AstarState {
    Map<TilePos, AstarNode> astarGraph;
    Set<TilePos> openSet;
    Set<TilePos> closedSet;
    TilePos finishPos;
    Boolean foundPath;
    Boolean gaveUp;
}
