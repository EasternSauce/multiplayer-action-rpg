package com.easternsauce.actionrpg.physics.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.World;
import com.easternsauce.actionrpg.Constants;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import com.easternsauce.actionrpg.pathing.Astar;
import com.easternsauce.actionrpg.pathing.PathingNode;
import com.easternsauce.actionrpg.physics.body.TerrainTileBody;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@Data
public class PhysicsWorld {
    TiledMap map;

    TiledMapTileLayer layer;

    World b2world;

    Map<Vector2Int, Boolean> traversables = new HashMap<>();

    Map<Vector2Int, Boolean> traversablesWithMargins = new HashMap<>();

    Map<Vector2Int, Boolean> flyover = new HashMap<>();

    Map<Vector2Int, Integer> clearances = new HashMap<>();

    Float tileWidth;
    Float tileHeight;

    List<TerrainTileBody> terrainTiles = new LinkedList<>();
    List<TerrainTileBody> terrainBorders = new LinkedList<>();

    Map<Vector2Int, PathingNode> pathingGraph;

    public static PhysicsWorld of(TiledMap map) {
        PhysicsWorld world = PhysicsWorld.of();

        world.map = map;

        return world;
    }

    public Integer widthInTiles() {
        return layer.getWidth();
    }

    public Integer heightInTiles() {
        return layer.getHeight();
    }

    public Map<Vector2Int, PathingNode> pathingGraph() {
        return pathingGraph;
    }

    public Vector2 getTileCenter(Vector2Int pos) {

        return Vector2.of(pos.getX() * tileWidth + tileWidth / 2, pos.getY() * tileHeight + tileHeight / 2);
    }

    public Vector2Int getClosestTile(Vector2 pos) {
        return Vector2Int.of((int) (pos.getX() / tileWidth), (int) (pos.getY() / tileHeight));
    }

    public void init() {
        layer = (TiledMapTileLayer) map.getLayers().get(0);

        b2world = new World(new com.badlogic.gdx.math.Vector2(0, 0), true);

        tileWidth = layer.getTileWidth() * Constants.MAP_SCALE / Constants.PPM;
        tileHeight = layer.getTileHeight() * Constants.MAP_SCALE / Constants.PPM;

        for (int i = 0; i < heightInTiles(); i++) {
            for (int j = 0; j < widthInTiles(); j++) {
                traversables.put(Vector2Int.of(j, i), true);
            }
        }

        for (int i = 0; i < heightInTiles(); i++) {
            for (int j = 0; j < widthInTiles(); j++) {
                traversablesWithMargins.put(Vector2Int.of(j, i), true);
            }
        }

        for (int i = 0; i < heightInTiles(); i++) {
            for (int j = 0; j < widthInTiles(); j++) {
                flyover.put(Vector2Int.of(j, i), true);
            }
        }

        createTerrainTiles();
        createBorders();

        pathingGraph = Astar.generatePathingGraph(this);
    }

    private Boolean tileExists(Integer x, Integer y) {
        return x >= 0 && x < widthInTiles() && y >= 0 && y < heightInTiles();
    }

    public void createTerrainTiles() {
        for (int layerNum = 0; layerNum <= 1; layerNum++) {
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerNum);

            for (int y = 0; y < layer.getHeight(); y++) {
                for (int x = 0; x < layer.getWidth(); x++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                    if (cell != null) {
                        Boolean isTileTraversable = (Boolean) cell.getTile().getProperties().get("traversable");
                        Boolean isTileFlyover = (Boolean) cell.getTile().getProperties().get("flyover");

                        if (!isTileTraversable) {
                            traversables.put(Vector2Int.of(x, y), false);

                            traversablesWithMargins.put(Vector2Int.of(x, y), false);

                            List<Vector2Int> combinations = Arrays.asList(Vector2Int.of(0, 1),
                                                                          Vector2Int.of(1, 0),
                                                                          Vector2Int.of(-1, 0),
                                                                          Vector2Int.of(0, -1),
                                                                          Vector2Int.of(1, 1),
                                                                          Vector2Int.of(-1, 1),
                                                                          Vector2Int.of(-1, -1),
                                                                          Vector2Int.of(1, -1));

                            final int _x = x;
                            final int _y = y;

                            combinations
                                .stream()
                                .filter(pos -> tileExists(_x + pos.getX(), _y + pos.getY()))
                                .forEach(tilePos -> traversablesWithMargins.put(Vector2Int.of(_x + tilePos.getX(),
                                                                                              _y + tilePos.getY()), false));

                        }

                        if (!isTileFlyover) {
                            flyover.put(Vector2Int.of(x, y), false);
                        }
                    }

                }
            }

            for (int y = 0; y < layer.getHeight(); y++) {
                for (int x = 0; x < layer.getWidth(); x++) {
                    if (!traversables.get(Vector2Int.of(x, y))) {
                        TerrainTileBody tile = TerrainTileBody.of(Vector2Int.of(x, y),
                                                                  tileWidth,
                                                                  tileHeight,
                                                                  layerNum,
                                                                  flyover.get(Vector2Int.of(x, y)));

                        tile.init(this);
                        terrainTiles.add(tile);
                    }
                }
            }
        }

        calculateClearances(traversables);
    }

    public void createBorders() {

        for (int x = 0; x < widthInTiles(); x++) {
            TerrainTileBody tile1 = TerrainTileBody.of(Vector2Int.of(x, -1), tileWidth, tileHeight, 0, false);
            tile1.init(this);
            terrainBorders.add(tile1);
            TerrainTileBody tile2 = TerrainTileBody.of(Vector2Int.of(x, heightInTiles()), tileWidth, tileHeight, 0, false);
            tile2.init(this);
            terrainBorders.add(tile2);
        }
        for (int y = 0; y < heightInTiles(); y++) {
            TerrainTileBody tile1 = TerrainTileBody.of(Vector2Int.of(-1, y), tileWidth, tileHeight, 0, false);
            tile1.init(this);
            terrainBorders.add(tile1);
            TerrainTileBody tile2 = TerrainTileBody.of(Vector2Int.of(widthInTiles(), y), tileWidth, tileHeight, 0, false);
            tile2.init(this);
            terrainBorders.add(tile2);
        }

    }

    public void tryAddClearance(Vector2Int pos, Integer level) {
        if (!clearances.containsKey(pos) && pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < widthInTiles() &&
            pos.getY() < heightInTiles() && traversables.get(pos)) {
            clearances.put(pos, level);
        }
    }

    public void calculateClearances(Map<Vector2Int, Boolean> traversables) {
        clearances = new HashMap<>();

        for (int y = 0; y < heightInTiles(); y++) {
            for (int x = 0; x < widthInTiles(); x++) {
                if (!traversables.get(Vector2Int.of(x, y))) {
                    tryAddClearance(Vector2Int.of(x - 1, y - 1), 1);
                    tryAddClearance(Vector2Int.of(x, y - 1), 1);
                    tryAddClearance(Vector2Int.of(x + 1, y - 1), 1);
                    tryAddClearance(Vector2Int.of(x - 1, y + 1), 1);
                    tryAddClearance(Vector2Int.of(x, y + 1), 1);
                    tryAddClearance(Vector2Int.of(x + 1, y + 1), 1);
                    tryAddClearance(Vector2Int.of(x - 1, y), 1);
                    tryAddClearance(Vector2Int.of(x + 1, y), 1);
                }
            }
        }

        int currentLevel = 2;

        while (traversables.values().stream().filter(isTraversable -> isTraversable).count() != clearances.size()) {

            final int level = currentLevel;

            List<Vector2Int> lowerLevelClearances = clearances
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == level - 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            lowerLevelClearances.forEach(pos -> {
                int x = pos.getX();
                int y = pos.getY();

                tryAddClearance(Vector2Int.of(x - 1, y - 1), level);
                tryAddClearance(Vector2Int.of(x, y - 1), level);
                tryAddClearance(Vector2Int.of(x + 1, y - 1), level);
                tryAddClearance(Vector2Int.of(x - 1, y + 1), level);
                tryAddClearance(Vector2Int.of(x, y + 1), level);
                tryAddClearance(Vector2Int.of(x + 1, y + 1), level);
                tryAddClearance(Vector2Int.of(x - 1, y), level);
                tryAddClearance(Vector2Int.of(x + 1, y), level);
            });

            currentLevel++;
        }

    }

    public void step() {
        b2world.step(Math.min(Gdx.graphics.getDeltaTime(), 0.15f), 6, 2);
    }

    public Boolean isLineBetweenPointsUnobstructedByTerrain(Vector2 fromPos, Vector2 toPos) {
        float lineWidth = 0.3f;
        com.badlogic.gdx.math.Polygon lineOfSightRect = new com.badlogic.gdx.math.Polygon(new float[]{
            fromPos.getX(),
            fromPos.getY(),
            fromPos.getX() + lineWidth,
            fromPos.getY() + lineWidth,
            toPos.getX() + lineWidth,
            toPos.getY() + lineWidth,
            toPos.getX(),
            toPos.getY()});

        List<com.badlogic.gdx.math.Polygon> terrainPolygons = terrainTiles
            .stream()
            .map(TerrainTileBody::getPolygon)
            .collect(Collectors.toList());

        List<com.badlogic.gdx.math.Polygon> borderPolygons = terrainBorders
            .stream()
            .map(TerrainTileBody::getPolygon)
            .collect(Collectors.toList());

        // TODO: maybe check nearby tiles only?
        for (com.badlogic.gdx.math.Polygon polygon : terrainPolygons) {
            if (Intersector.overlapConvexPolygons(polygon, lineOfSightRect)) {
                return false;
            }
        }

        for (com.badlogic.gdx.math.Polygon polygon : borderPolygons) {
            if (Intersector.overlapConvexPolygons(polygon, lineOfSightRect)) {
                return false;
            }
        }

        return true;
    }
}
