package com.mygdx.game.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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

    public Integer widthInTiles() {
        return layer.getWidth();
    }

    public Integer heightInTiles() {
        return layer.getHeight();
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
    }

    public void createBorders() {

        for (int x = 0; x < widthInTiles(); x++) {
            terrainBorders.add(TerrainTileBody.of(TilePos.of(x, -1), tileWidth, tileHeight, null, null));
            terrainBorders.add(TerrainTileBody.of(TilePos.of(x, heightInTiles()), tileWidth, tileHeight, null, null));
        }
        for (int y = 0; y < heightInTiles(); y++) {
            terrainBorders.add(TerrainTileBody.of(TilePos.of(-1, y), tileWidth, tileHeight, null, null));
            terrainBorders.add(TerrainTileBody.of(TilePos.of(widthInTiles(), y), tileWidth, tileHeight, null, null));
        }

    }


    public static PhysicsWorld of(TiledMap map) {
        PhysicsWorld world = PhysicsWorld.of();

        world.map = map;

        return world;
    }
}
