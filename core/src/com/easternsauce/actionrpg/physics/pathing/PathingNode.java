package com.easternsauce.actionrpg.physics.pathing;

import com.easternsauce.actionrpg.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class PathingNode {
    @Getter
    private Vector2Int pos;
    private Integer clearance;
    @Getter
    private List<PathingEdge> outgoingEdges = new ArrayList<>();

    public static PathingNode of(Vector2Int pos, Integer clearance) {
        PathingNode pathingNode = new PathingNode();

        pathingNode.pos = pos;
        pathingNode.clearance = clearance;

        return pathingNode;
    }

    public PathingNode withEdge(Float weight, PathingNode node) {
        PathingEdge newEdge = PathingEdge.of(weight, node.getPos());
        ArrayList<PathingEdge> newOutgoingEdges = new ArrayList<>(outgoingEdges);
        newOutgoingEdges.add(0, newEdge);
        return PathingNode.of(pos, clearance, newOutgoingEdges);
    }
}

