package com.mygdx.game.physics;

import com.badlogic.gdx.physics.box2d.Body;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class TerrainTileBody {
    TilePos pos;
    Float tileWidth;
    Float tileHeight;
    Integer layer;
    Boolean flyover;
    Body b2Body;
    com.badlogic.gdx.math.Polygon polygon;

    public static TerrainTileBody of(TilePos pos, Float tileWidth, Float tileHeight, Integer layer, Boolean flyover) {
        TerrainTileBody body = new TerrainTileBody();
        body.pos = pos;
        body.tileWidth = tileWidth;
        body.tileHeight = tileHeight;
        body.layer = layer;
        body.flyover = flyover;
        return body;
    }

    public void init(PhysicsWorld world) {
        b2Body = B2BodyFactory.createTerrainTileB2body(world, this);

        polygon = new com.badlogic.gdx.math.Polygon(new float[]{
                pos.x() * tileWidth, pos.y() * tileWidth, pos.x() * tileWidth + tileWidth, pos.y() * tileHeight,
                pos.x() * tileWidth + tileWidth, pos.y() * tileHeight + tileHeight, pos.x() * tileWidth, pos.y() *
                                                                                                         tileHeight +
                                                                                                         tileHeight});
    }
}
