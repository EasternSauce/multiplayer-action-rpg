package com.mygdx.game.physics.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Constants;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.pathing.PathingNode;
import com.mygdx.game.physics.body.TerrainTileBody;
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

        return Vector2.of(pos.x() * tileWidth + tileWidth / 2, pos.y() * tileHeight + tileHeight / 2);
    }

    public Vector2Int getClosestTile(Vector2 pos) {
        return Vector2Int.of((int) (pos.x() / tileWidth), (int) (pos.y() / tileHeight));
    }

    public void init() {
        layer = (TiledMapTileLayer) map.getLayers().get(0);

        b2world = new World(new com.badlogic.gdx.math.Vector2(0, 0), true);

        tileWidth = layer.getTileWidth() * Constants.MapScale / Constants.PPM;
        tileHeight = layer.getTileHeight() * Constants.MapScale / Constants.PPM;

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

                            combinations.stream()
                                        .filter(pos -> tileExists(_x + pos.x(), _y + pos.y()))
                                        .forEach(tilePos -> traversablesWithMargins.put(Vector2Int.of(_x + tilePos.x(),
                                                                                                      _y + tilePos.y()),
                                                                                        false));

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
            TerrainTileBody tile2 =
                    TerrainTileBody.of(Vector2Int.of(x, heightInTiles()), tileWidth, tileHeight, 0, false);
            tile2.init(this);
            terrainBorders.add(tile2);
        }
        for (int y = 0; y < heightInTiles(); y++) {
            TerrainTileBody tile1 = TerrainTileBody.of(Vector2Int.of(-1, y), tileWidth, tileHeight, 0, false);
            tile1.init(this);
            terrainBorders.add(tile1);
            TerrainTileBody tile2 =
                    TerrainTileBody.of(Vector2Int.of(widthInTiles(), y), tileWidth, tileHeight, 0, false);
            tile2.init(this);
            terrainBorders.add(tile2);
        }


    }

    public void tryAddClearance(Vector2Int pos, Integer level) {
        if (!clearances.containsKey(pos) &&
            pos.x() >= 0 &&
            pos.y() >= 0 &&
            pos.x() < widthInTiles() &&
            pos.y() < heightInTiles() &&
            traversables.get(
                    pos)) {
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

            List<Vector2Int> lowerLevelClearances = clearances.entrySet()
                                                              .stream()
                                                              .filter(entry -> entry.getValue() == level - 1)
                                                              .map(Map.Entry::getKey)
                                                              .collect(Collectors.toList());


            lowerLevelClearances.forEach(pos -> {
                int x = pos.x();
                int y = pos.y();

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

    public Boolean isLineOfSight(Vector2 fromPos, Vector2 toPos) {
        float lineWidth = 0.3f;
        com.badlogic.gdx.math.Polygon lineOfSightRect =
                new com.badlogic.gdx.math.Polygon(new float[]{fromPos.x(), fromPos.y(), fromPos.x() +
                                                                                        lineWidth, fromPos.y() +
                                                                                                   lineWidth, toPos.x() +
                                                                                                              lineWidth,
                        toPos.y() +
                        lineWidth, toPos.x(), toPos.y()});

        List<com.badlogic.gdx.math.Polygon> polygons =
                terrainTiles.stream().map(TerrainTileBody::polygon).collect(Collectors.toList());

        boolean overlaps = false;

        for (com.badlogic.gdx.math.Polygon polygon : polygons) {
            if (Intersector.overlapConvexPolygons(polygon, lineOfSightRect)) {
                overlaps = true;
            }
        }

        return !overlaps;
    }
}
