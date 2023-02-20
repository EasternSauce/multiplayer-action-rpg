package com.mygdx.game.pathing;

import com.mygdx.game.physics.util.TilePos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class PathingNode {
    TilePos pos;
    Integer clearance;
    List<PathingEdge> outgoingEdges = new ArrayList<>();

    public static PathingNode of(TilePos pos, Integer clearance) {
        PathingNode pathingNode = new PathingNode();
        pathingNode.pos(pos);
        pathingNode.clearance(clearance);
        return pathingNode;
    }

    public PathingNode withEdge(Float weight, PathingNode node) {
        PathingEdge newEdge = PathingEdge.of(weight, node.pos());
        ArrayList<PathingEdge> newOutgoingEdges = new ArrayList<>(outgoingEdges);
        newOutgoingEdges.add(0, newEdge);
        return PathingNode.of(pos, clearance, newOutgoingEdges);
    }
}

