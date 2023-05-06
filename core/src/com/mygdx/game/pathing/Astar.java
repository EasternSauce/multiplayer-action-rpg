package com.mygdx.game.pathing;

import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.physics.world.PhysicsWorld;

import java.util.*;
import java.util.stream.Collectors;

public class Astar {
    public static void tryAddEdge(Map<Vector2Int, PathingNode> pathingNodes, PhysicsWorld world, Vector2Int fromPos,
                                  Vector2Int toPos, Float weight) {
        if (0 <= toPos.getY() && toPos.getY() < world.heightInTiles() && 0 <= toPos.getX() &&
            toPos.getX() < world.widthInTiles()) {
            if (world.getTraversables().get(fromPos) && world.getTraversables().get(toPos)) {
                PathingNode targetNode = pathingNodes.get(toPos);
                pathingNodes.put(fromPos, pathingNodes.get(fromPos).withEdge(weight, targetNode));
            }
        }
    }

    public static Map<Vector2Int, PathingNode> generatePathingGraph(PhysicsWorld world) {
        Map<Vector2Int, PathingNode> pathingNodes = new HashMap<>();
        for (int y = 0; y < world.heightInTiles(); y++) {
            for (int x = 0; x < world.widthInTiles(); x++) {
                pathingNodes.put(Vector2Int.of(x, y), PathingNode.of(Vector2Int.of(x, y), world.getClearances()
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

                if (x - 1 >= 0 && y - 1 >= 0 && world.getTraversables().get(Vector2Int.of(x - 1, y)) &&
                    world.getTraversables().get(Vector2Int.of(x, y - 1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x - 1, y - 1), diagonalWeight);
                }

                if (x + 1 < world.widthInTiles() && y - 1 >= 0 && world.getTraversables().get(Vector2Int.of(x + 1, y)) &&
                    world.getTraversables().get(Vector2Int.of(x, y - 1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x + 1, y - 1), diagonalWeight);
                }

                if (x - 1 >= 0 && y + 1 < world.heightInTiles() && world.getTraversables().get(Vector2Int.of(x - 1, y)) &&
                    world.getTraversables().get(Vector2Int.of(x, y + 1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x - 1, y + 1), diagonalWeight);
                }

                if (x + 1 < world.widthInTiles() && y + 1 < world.heightInTiles() &&
                    world.getTraversables().get(Vector2Int.of(x + 1, y)) &&
                    world.getTraversables().get(Vector2Int.of(x, y + 1))) {
                    tryAddEdge(pathingNodes, world, Vector2Int.of(x, y), Vector2Int.of(x + 1, y + 1), diagonalWeight);
                }
            }
        }
        return pathingNodes;
    }

    public static AstarState traverse(AstarState astarState, Vector2Int finishTilePos, PhysicsWorld world, Integer capability) {
        while (!astarState.getIsGaveUp() && !astarState.getOpenSet().isEmpty() && !astarState.getFoundPath()) {
            AstarState finalAstarState = astarState;
            Vector2Int minimumTile = Collections.min(astarState.getOpenSet(), (o1, o2) -> {
                if (Objects.equals(finalAstarState.getAstarGraph().get(o1).getF(), finalAstarState.getAstarGraph()
                        .get(o2)
                        .getF())) {
                    return 0;
                }
                if (finalAstarState.getAstarGraph().get(o1).getF() >= finalAstarState.getAstarGraph().get(o2).getF()) {
                    return 1;
                }
                return -1;
            });
            AstarNode currentNode = astarState.getAstarGraph().get(minimumTile);

            AstarState resultingAstarState = AstarState.of(astarState.getAstarGraph(), astarState.getOpenSet(),
                                                           astarState.getClosedSet(), astarState.getFinishPos(),
                                                           astarState.getFoundPath(), false);

            if (astarState.getClosedSet().size() > 80) { // give up once you process enough tiles [PERFORMANCE SAVER]
                resultingAstarState.setIsGaveUp(true);
            }

            if (currentNode.getPos().equals(finishTilePos)) {
                resultingAstarState.setFoundPath(true);
            }
            else {
                HashSet<Vector2Int> modifiedOpenSet = new HashSet<>(resultingAstarState.getOpenSet());
                modifiedOpenSet.remove(currentNode.getPos());
                HashSet<Vector2Int> modifiedClosedSet = new HashSet<>(resultingAstarState.getClosedSet());
                modifiedClosedSet.add(currentNode.getPos());
                resultingAstarState.setOpenSet(modifiedOpenSet);
                resultingAstarState.setClosedSet(modifiedClosedSet);

                for (int i = 0; i < currentNode.getPathingNode().getOutgoingEdges().size(); i++) {
                    PathingEdge pathingEdge = currentNode.getPathingNode().getOutgoingEdges().get(i);
                    resultingAstarState = processNeighbor(resultingAstarState, currentNode.getPos(), pathingEdge,
                                                          pathingEdge.getWeight(), world, capability);
                }

            }

            astarState = resultingAstarState;
        }
        return astarState;
    }

    public static Double calculateHeuristic(Vector2Int startPos, Vector2Int finishPos) {
        return (Math.abs(finishPos.getX() - startPos.getX()) + Math.abs(finishPos.getY() - startPos.getY())) * 10.0;
    }

    public static List<Vector2Int> reconstructPath(AstarNode lastNode, AstarState result) {
        if (lastNode.getParent() != null) {
            List<Vector2Int> list = new LinkedList<>(reconstructPath(result.getAstarGraph().get(lastNode.getParent()), result));
            list.add(0, lastNode.getPos());
            return list;
        }
        return new LinkedList<>();
    }

    public static AstarState processNeighbor(AstarState astarState, Vector2Int originNodePos, PathingEdge pathingEdge,
                                             Float distanceBetweenNodes, PhysicsWorld world, Integer capability) {
        if (astarState.getClosedSet().contains(pathingEdge.getNeighborPos()) ||
            Astar.calculateHeuristic(originNodePos, astarState.getFinishPos()) >= 60 &&
            world.getClearances().get(pathingEdge.getNeighborPos()) < capability) {
            return astarState;
        }

        AstarNode originNode = astarState.getAstarGraph().get(originNodePos);
        AstarNode neighborNode = astarState.getAstarGraph().get(pathingEdge.getNeighborPos());

        Double tentativeGScore = originNode.getG() + distanceBetweenNodes;

        AstarNode updatedNode = AstarNode.of(neighborNode.getPathingNode(), neighborNode.getParent(), neighborNode.getF(),
                                             neighborNode.getG(), neighborNode.getH());

        if (!astarState.getOpenSet().contains(neighborNode.getPos())) {
            updatedNode.setH(Astar.calculateHeuristic(neighborNode.getPos(), astarState.getFinishPos()));
            updatedNode.setParent(originNodePos);
            updatedNode.setG(tentativeGScore);
            updatedNode.setF(updatedNode.getG() + updatedNode.getH());

            AstarState updatedAstarState = AstarState.of(astarState.getAstarGraph(), astarState.getOpenSet(),
                                                         astarState.getClosedSet(), astarState.getFinishPos(),
                                                         astarState.getFoundPath(), astarState.getIsGaveUp());

            Map<Vector2Int, AstarNode> updatedAstarGraph = new HashMap<>(astarState.getAstarGraph());
            updatedAstarGraph.put(neighborNode.getPos(), updatedNode);
            updatedAstarState.setAstarGraph(updatedAstarGraph);

            Set<Vector2Int> updatedOpenSet = new HashSet<>(astarState.getOpenSet());
            updatedOpenSet.add(neighborNode.getPos());
            updatedAstarState.setOpenSet(updatedOpenSet);

            return updatedAstarState;
        }

        if (tentativeGScore < neighborNode.getG()) {
            updatedNode.setParent(originNodePos);
            updatedNode.setG(tentativeGScore);
            updatedNode.setF(updatedNode.getG() + updatedNode.getH());

            AstarState updatedAstarState = AstarState.of(astarState.getAstarGraph(), astarState.getOpenSet(),
                                                         astarState.getClosedSet(), astarState.getFinishPos(),
                                                         astarState.getFoundPath(), astarState.getIsGaveUp());

            Map<Vector2Int, AstarNode> updatedAstarGraph = new HashMap<>(astarState.getAstarGraph());
            updatedAstarGraph.put(neighborNode.getPos(), updatedNode);
            updatedAstarState.setAstarGraph(updatedAstarGraph);

            return updatedAstarState;
        }

        return astarState;

    }

    public static AstarResult findPath(PhysicsWorld world, Vector2 startPos, Vector2 finishPos, Integer capability) {
        Vector2Int startTilePos = world.getClosestTile(startPos);
        Vector2Int finishTilePos = world.getClosestTile(finishPos);

        Map<Vector2Int, AstarNode> freshAstarGraph = Astar.getAstarGraph(world.pathingGraph());
        freshAstarGraph.get(startTilePos).setG(0.0);

        AstarState astarState = AstarState.of(freshAstarGraph, new HashSet<>(Collections.singletonList(startTilePos)),
                                              new HashSet<>(), finishTilePos, false, false);

        AstarState result = traverse(astarState, finishTilePos, world, capability);

        AstarNode lastNode = result.getAstarGraph().get(result.getFinishPos());


        List<Vector2Int> path = reconstructPath(lastNode, result);

        Collections.reverse(path);

        List<Vector2> resultPath = path.stream().map(world::getTileCenter).collect(Collectors.toList());

        return AstarResult.of(resultPath, result.getIsGaveUp());
    }

    public static Map<Vector2Int, AstarNode> getAstarGraph(Map<Vector2Int, PathingNode> pathingGraph) {
        return pathingGraph.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, stuff -> AstarNode.of(stuff.getValue())));
    }
}
