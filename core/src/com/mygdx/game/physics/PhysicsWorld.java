package com.mygdx.game.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Constants;
import com.mygdx.game.pathing.Astar;
import com.mygdx.game.pathing.PathingNode;
import com.mygdx.game.util.Vector2;
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

    Map<TilePos, Boolean> traversables = new HashMap<>();

    Map<TilePos, Boolean> traversablesWithMargins = new HashMap<>();

    Map<TilePos, Boolean> flyover = new HashMap<>();

    Map<TilePos, Integer> clearances = new HashMap<>();

    Float tileWidth;
    Float tileHeight;

    List<TerrainTileBody> terrainTiles = new LinkedList<>();
    List<TerrainTileBody> terrainBorders = new LinkedList<>();

    Map<TilePos, PathingNode> pathingGraph;

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

    public Float tileWidth() {
        return tileWidth;
    }

    public Float tileHeight() {
        return tileHeight;
    }

    public Map<TilePos, PathingNode> pathingGraph() {
        return pathingGraph;
    }

    public Vector2 getTileCenter(TilePos pos) {

        return Vector2.of(pos.x() * tileWidth + tileWidth / 2, pos.y() * tileHeight + tileHeight / 2);
    }

    public TilePos getClosestTile(Vector2 pos) {
        return TilePos.of((int) (pos.x() / tileWidth), (int) (pos.y() / tileHeight));
    }

    public void init() {
        layer = (TiledMapTileLayer) map.getLayers().get(0);

        b2world = new World(new com.badlogic.gdx.math.Vector2(0, 0), true);

        tileWidth = layer.getTileWidth() * Constants.MapScale / Constants.PPM;
        tileHeight = layer.getTileHeight() * Constants.MapScale / Constants.PPM;

        for (int i = 0; i < heightInTiles(); i++) {
            for (int j = 0; j < widthInTiles(); j++) {
                traversables.put(TilePos.of(j, i), true);
            }
        }

        for (int i = 0; i < heightInTiles(); i++) {
            for (int j = 0; j < widthInTiles(); j++) {
                traversablesWithMargins.put(TilePos.of(j, i), true);
            }
        }

        for (int i = 0; i < heightInTiles(); i++) {
            for (int j = 0; j < widthInTiles(); j++) {
                flyover.put(TilePos.of(j, i), true);
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
                            traversables.put(TilePos.of(x, y), false);

                            traversablesWithMargins.put(TilePos.of(x, y), false);

                            List<TilePos> combinations =
                                    Arrays.asList(TilePos.of(0, 1), TilePos.of(1, 0), TilePos.of(-1, 0),
                                            TilePos.of(0, -1), TilePos.of(1, 1), TilePos.of(-1, 1), TilePos.of(-1, -1),
                                            TilePos.of(1, -1));

                            final int _x = x;
                            final int _y = y;

                            combinations.stream().filter(pos -> tileExists(_x + pos.x(), _y + pos.y()))
                                    .forEach(tilePos -> {
                                        traversablesWithMargins.put(TilePos.of(_x + tilePos.x(), _y + tilePos.y()),
                                                false);
                                    });

                        }

                        if (!isTileFlyover) flyover.put(TilePos.of(x, y), false);
                    }


                }
            }

            for (int y = 0; y < layer.getHeight(); y++) {
                for (int x = 0; x < layer.getWidth(); x++) {
                    if (!traversables.get(TilePos.of(x, y))) {
                        TerrainTileBody tile = TerrainTileBody.of(TilePos.of(x, y), tileWidth, tileHeight, layerNum,
                                flyover.get(TilePos.of(x, y)));

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
            TerrainTileBody tile1 = TerrainTileBody.of(TilePos.of(x, -1), tileWidth, tileHeight, null, null);
            tile1.init(this);
            terrainBorders.add(tile1);
            TerrainTileBody tile2 =
                    TerrainTileBody.of(TilePos.of(x, heightInTiles()), tileWidth, tileHeight, null, null);
            tile2.init(this);
            terrainBorders.add(tile2);
        }
        for (int y = 0; y < heightInTiles(); y++) {
            TerrainTileBody tile1 = TerrainTileBody.of(TilePos.of(-1, y), tileWidth, tileHeight, null, null);
            tile1.init(this);
            terrainBorders.add(tile1);
            TerrainTileBody tile2 =
                    TerrainTileBody.of(TilePos.of(widthInTiles(), y), tileWidth, tileHeight, null, null);
            tile2.init(this);
            terrainBorders.add(tile2);
        }


    }

    public void tryAddClearance(TilePos pos, Integer level) {
        if (!clearances.containsKey(pos) && pos.x() >= 0 && pos.y() >= 0 && pos.x() < widthInTiles() &&
                pos.y() < heightInTiles() && traversables.get(pos)) {
            clearances.put(pos, level);
        }
    }

    public void calculateClearances(Map<TilePos, Boolean> traversables) {
        clearances = new HashMap<>();

        for (int y = 0; y < heightInTiles(); y++) {
            for (int x = 0; x < widthInTiles(); x++) {
                if (!traversables.get(TilePos.of(x, y))) {
                    tryAddClearance(TilePos.of(x - 1, y - 1), 1);
                    tryAddClearance(TilePos.of(x, y - 1), 1);
                    tryAddClearance(TilePos.of(x + 1, y - 1), 1);
                    tryAddClearance(TilePos.of(x - 1, y + 1), 1);
                    tryAddClearance(TilePos.of(x, y + 1), 1);
                    tryAddClearance(TilePos.of(x + 1, y + 1), 1);
                    tryAddClearance(TilePos.of(x - 1, y), 1);
                    tryAddClearance(TilePos.of(x + 1, y), 1);
                }
            }
        }

        int currentLevel = 2;

        while (traversables.values().stream().filter(isTraversable -> isTraversable).count() != clearances.size()) {

            final int level = currentLevel;

            List<TilePos> lowerLevelClearances =
                    clearances.entrySet().stream().filter(entry -> entry.getValue() == level - 1)
                            .map(Map.Entry::getKey).collect(Collectors.toList());


            lowerLevelClearances.forEach(pos -> {
                int x = pos.x();
                int y = pos.y();

                tryAddClearance(TilePos.of(x - 1, y - 1), level);
                tryAddClearance(TilePos.of(x, y - 1), level);
                tryAddClearance(TilePos.of(x + 1, y - 1), level);
                tryAddClearance(TilePos.of(x - 1, y + 1), level);
                tryAddClearance(TilePos.of(x, y + 1), level);
                tryAddClearance(TilePos.of(x + 1, y + 1), level);
                tryAddClearance(TilePos.of(x - 1, y), level);
                tryAddClearance(TilePos.of(x + 1, y), level);
            });

            currentLevel++;
        }

    }

    public void step() {
        b2world.step(Math.min(Gdx.graphics.getDeltaTime(), 0.15f), 6, 2);
    }

    public Boolean isLineOfSight(Vector2 fromPos, Vector2 toPos) {
        float lineWidth = 0.3f;
        com.badlogic.gdx.math.Polygon lineOfSightRect = new com.badlogic.gdx.math.Polygon(new float[]{
                fromPos.x(),
                fromPos.y(),
                fromPos.x() + lineWidth,
                fromPos.y() + lineWidth,
                toPos.x() + lineWidth,
                toPos.y() + lineWidth,
                toPos.x(),
                toPos.y()
        });

        List<com.badlogic.gdx.math.Polygon> polygons =
                terrainTiles.stream().map(TerrainTileBody::polygon).collect(Collectors.toList());

        boolean overlaps = false;

        for (com.badlogic.gdx.math.Polygon polygon : polygons) {
            if (Intersector.overlapConvexPolygons(polygon, lineOfSightRect)) overlaps = true;
        }

        return !overlaps;
    }
}
