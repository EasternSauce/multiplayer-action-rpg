package com.mygdx.game.pathing;

import com.mygdx.game.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AstarNode {
    PathingNode pathingNode;
    Vector2Int parent = null;
    Double f = Double.MAX_VALUE;
    Double g = Double.MAX_VALUE;
    Double h = Double.MAX_VALUE;

    public static AstarNode of(PathingNode pathingNode) {
        AstarNode astarNode = AstarNode.of();
        astarNode.pathingNode = pathingNode;
        return astarNode;
    }

    public Vector2Int getPos() {
        return pathingNode.getPos();
    }
}
