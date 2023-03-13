package com.mygdx.game.pathing;

import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.physics.world.PhysicsWorld;

import java.util.*;
import java.util.stream.Collectors;

public class Astar {
    public static void tryAddEdge(Map<Vector2Int, PathingNode> pathingNodes,
                                  PhysicsWorld world,
                                  Vector2Int fromPos,
                                  Vector2Int toPos,
                                  Float weight) {
        if (0 <= toPos.y() && toPos.y() < world.heightInTiles() && 0 <= toPos.x() && toPos.x() < world.widthInTiles()) {
            if (world.traversables().get(fromPos) && world.traversables().get(toPos)) {
                PathingNode targetNode = pathingNodes.get(toPos);
                pathingNodes.put(fromPos, pathingNodes.get(fromPos).withEdge(weight, targetNode));
            }
        }
    }

    public static Map<Vector2Int, PathingNode> generatePathingGraph(PhysicsWorld world) {
        Map<Vector2Int, PathingNode> pathingNodes = new HashMap<>();
        for (int y = 0; y < world.heightInTiles(); y++) {
            for (int x = 0; x < world.widthInTiles(); x++) {
                pathingNodes.put(Vector2Int.of(x, y),
                                 PathingNode.of(Vector2Int.of(x, y),
                                                world.clearances()
                                                     .getOrDefault(Vector2Int.of(x, y), Integer.MAX_VALUE)));
            }
        }

        Float straightWeight = 10f;
        Float diagonalWeight = 14.1421356237f;

        for (int y = 0; y < world.heightInTiles(); y++) {
            for (int x = 0; x < world.widthInTiles(); x++) {

                tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x - 1, y), straightWeight);
                tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x + 1, y), straightWeight);
                tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x, y - 1), straightWeight);
                tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x, y + 1), straightWeight);

                if (x - 1 >= 0 &&
                    y - 1 >= 0 &&
                    world.traversables().get(Vector2Int.of(x - 1, y)) &&
                    world.traversables()
                         .get(Vector2Int.of(x,
                                            y -
                                            1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x - 1, y - 1), diagonalWeight);
                }

                if (x + 1 < world.widthInTiles() && y - 1 >= 0 && world.traversables()
                                                                       .get(Vector2Int.of(x + 1,
                                                                                          y)) && world.traversables()
                                                                                                      .get(Vector2Int.of(
                                                                                                              x,
                                                                                                              y -
                                                                                                              1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x + 1, y - 1), diagonalWeight);
                }

                if (x - 1 >= 0 && y + 1 < world.heightInTiles() && world.traversables()
                                                                        .get(Vector2Int.of(x - 1,
                                                                                           y)) && world.traversables()
                                                                                                       .get(Vector2Int.of(
                                                                                                               x,
                                                                                                               y +
                                                                                                               1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x - 1, y + 1), diagonalWeight);
                }

                if (x + 1 < world.widthInTiles() &&
                    y + 1 < world.heightInTiles() &&
                    world.traversables()
                         .get(Vector2Int.of(x + 1,
                                            y)) &&
                    world.traversables()
                         .get(Vector2Int.of(
                                 x,
                                 y + 1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x + 1, y + 1), diagonalWeight);
                }
            }
        }
        return pathingNodes;
    }

    public static AstarState traverse(AstarState astarState,
                                      Vector2Int finishTilePos,
                                      PhysicsWorld world,
                                      Integer capability) {
        while (!astarState.gaveUp() && !astarState.openSet().isEmpty() && !astarState.foundPath()) {
            AstarState finalAstarState = astarState;
            Vector2Int minimumTile = Collections.min(astarState.openSet(), (o1, o2) -> {
                if (Objects.equals(finalAstarState.astarGraph().get(o1).f(),
                                   finalAstarState.astarGraph().get(o2).f())) {
                    return 0;
                }
                if (finalAstarState.astarGraph().get(o1).f() >= finalAstarState.astarGraph().get(o2).f()) {
                    return 1;
                }
                return -1;
            });
            AstarNode currentNode = astarState.astarGraph().get(minimumTile);

            AstarState resultingAstarState = AstarState.of(astarState.astarGraph(),
                                                           astarState.openSet(),
                                                           astarState.closedSet(),
                                                           astarState.finishPos(),
                                                           astarState.foundPath(),
                                                           false);

            if (astarState.closedSet().size() > 80) { // give up once you process enough tiles [PERFORMANCE SAVER]
                resultingAstarState.gaveUp(true);
            }

            if (currentNode.pos().equals(finishTilePos)) {
                resultingAstarState.foundPath(true);
            }
            else {
                HashSet<Vector2Int> modifiedOpenSet = new HashSet<>(resultingAstarState.openSet());
                modifiedOpenSet.remove(currentNode.pos());
                HashSet<Vector2Int> modifiedClosedSet = new HashSet<>(resultingAstarState.closedSet());
                modifiedClosedSet.add(currentNode.pos());
                resultingAstarState.openSet(modifiedOpenSet);
                resultingAstarState.closedSet(modifiedClosedSet);

                for (int i = 0; i < currentNode.pathingNode().outgoingEdges().size(); i++) {
                    PathingEdge pathingEdge = currentNode.pathingNode().outgoingEdges().get(i);
                    resultingAstarState = processNeighbor(resultingAstarState,
                                                          currentNode.pos(),
                                                          pathingEdge,
                                                          pathingEdge.weight(),
                                                          world,
                                                          capability);
                }

            }

            astarState = resultingAstarState;
        }
        return astarState;
    }

    public static Double calculateHeuristic(Vector2Int startPos, Vector2Int finishPos) {
        return (Math.abs(finishPos.x() - startPos.x()) + Math.abs(finishPos.y() - startPos.y())) * 10.0;
    }

    public static List<Vector2Int> reconstructPath(AstarNode lastNode, AstarState result) {
        if (lastNode.parent() != null) {
            List<Vector2Int>
                    list =
                    new LinkedList<>(reconstructPath(result.astarGraph().get(lastNode.parent()), result));
            list.add(0, lastNode.pos());
            return list;
        }
        return new LinkedList<>();
    }

    public static AstarState processNeighbor(AstarState astarState,
                                             Vector2Int originNodePos,
                                             PathingEdge pathingEdge,
                                             Float distanceBetweenNodes,
                                             PhysicsWorld world,
                                             Integer capability) {
        if (astarState.closedSet().contains(pathingEdge.neighborPos()) || Astar.calculateHeuristic(originNodePos,
                                                                                                   astarState.finishPos()) >=
                                                                          60 && world.clearances()
                                                                                     .get(pathingEdge.neighborPos()) <
                                                                                capability) {
            return astarState;
        }

        AstarNode originNode = astarState.astarGraph().get(originNodePos);
        AstarNode neighborNode = astarState.astarGraph().get(pathingEdge.neighborPos());

        Double tentativeGScore = originNode.g() + distanceBetweenNodes;

        AstarNode updatedNode = AstarNode.of(neighborNode.pathingNode(),
                                             neighborNode.parent(),
                                             neighborNode.f(),
                                             neighborNode.g(),
                                             neighborNode.h());

        if (!astarState.openSet().contains(neighborNode.pos())) {
            updatedNode.h(Astar.calculateHeuristic(neighborNode.pos(), astarState.finishPos()));
            updatedNode.parent(originNodePos);
            updatedNode.g(tentativeGScore);
            updatedNode.f(updatedNode.g() + updatedNode.h());

            AstarState updatedAstarState = AstarState.of(astarState.astarGraph(),
                                                         astarState.openSet(),
                                                         astarState.closedSet(),
                                                         astarState.finishPos(),
                                                         astarState.foundPath(),
                                                         astarState.gaveUp());

            Map<Vector2Int, AstarNode> updatedAstarGraph = new HashMap<>(astarState.astarGraph());
            updatedAstarGraph.put(neighborNode.pos(), updatedNode);
            updatedAstarState.astarGraph(updatedAstarGraph);

            Set<Vector2Int> updatedOpenSet = new HashSet<>(astarState.openSet());
            updatedOpenSet.add(neighborNode.pos());
            updatedAstarState.openSet(updatedOpenSet);

            return updatedAstarState;
        }

        if (tentativeGScore < neighborNode.g()) {
            updatedNode.parent(originNodePos);
            updatedNode.g(tentativeGScore);
            updatedNode.f(updatedNode.g() + updatedNode.h());

            AstarState updatedAstarState = AstarState.of(astarState.astarGraph(),
                                                         astarState.openSet(),
                                                         astarState.closedSet(),
                                                         astarState.finishPos(),
                                                         astarState.foundPath(),
                                                         astarState.gaveUp());

            Map<Vector2Int, AstarNode> updatedAstarGraph = new HashMap<>(astarState.astarGraph());
            updatedAstarGraph.put(neighborNode.pos(), updatedNode);
            updatedAstarState.astarGraph(updatedAstarGraph);

            return updatedAstarState;
        }

        return astarState;

    }

    public static AstarResult findPath(PhysicsWorld world, Vector2 startPos, Vector2 finishPos, Integer capability) {
        Vector2Int startTilePos = world.getClosestTile(startPos);
        Vector2Int finishTilePos = world.getClosestTile(finishPos);

        Map<Vector2Int, AstarNode> freshAstarGraph = Astar.getAstarGraph(world.pathingGraph());
        freshAstarGraph.get(startTilePos).g(0.0);

        AstarState astarState = AstarState.of(freshAstarGraph,
                                              new HashSet<>(Collections.singletonList(startTilePos)),
                                              new HashSet<>(),
                                              finishTilePos,
                                              false,
                                              false);

        AstarState result = traverse(astarState, finishTilePos, world, capability);

        AstarNode lastNode = result.astarGraph().get(result.finishPos());


        List<Vector2Int> path = reconstructPath(lastNode, result);

        Collections.reverse(path);

        List<Vector2> resultPath = path.stream().map(world::getTileCenter).collect(Collectors.toList());

        return AstarResult.of(resultPath, result.gaveUp());
    }

    public static Map<Vector2Int, AstarNode> getAstarGraph(Map<Vector2Int, PathingNode> pathingGraph) {
        return pathingGraph.entrySet()
                           .stream()
                           .collect(Collectors.toMap(Map.Entry::getKey, stuff -> AstarNode.of(stuff.getValue())));
    }
}
