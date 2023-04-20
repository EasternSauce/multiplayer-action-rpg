package com.mygdx.game.pathing;

import com.mygdx.game.model.util.Vector2Int;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PathingNode {
    Vector2Int pos;
    Integer clearance;
    List<PathingEdge> outgoingEdges = new ArrayList<>();

    public static PathingNode of(Vector2Int pos, Integer clearance) {
        PathingNode pathingNode = new PathingNode();
        pathingNode.setPos(pos);
        pathingNode.setClearance(clearance);
        return pathingNode;
    }

    public PathingNode withEdge(Float weight, PathingNode node) {
        PathingEdge newEdge = PathingEdge.of(weight, node.getPos());
        ArrayList<PathingEdge> newOutgoingEdges = new ArrayList<>(outgoingEdges);
        newOutgoingEdges.add(0, newEdge);
        return PathingNode.of(pos, clearance, newOutgoingEdges);
    }
}

