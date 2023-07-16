package com.easternsauce.actionrpg.physics.pathing;

import com.easternsauce.actionrpg.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("SpellCheckingInspection")
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AstarNode {
    private PathingNode pathingNode;
    private Vector2Int parent = null;
    private Double f = Double.MAX_VALUE;
    private Double g = Double.MAX_VALUE;
    private Double h = Double.MAX_VALUE;

    public static AstarNode of(PathingNode pathingNode) {
        AstarNode astarNode = AstarNode.of();
        astarNode.pathingNode = pathingNode;
        return astarNode;
    }

    public Vector2Int getPos() {
        return pathingNode.getPos();
    }
}
