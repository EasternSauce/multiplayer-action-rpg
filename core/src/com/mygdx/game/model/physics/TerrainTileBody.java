package com.mygdx.game.model.physics;

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

    public static TerrainTileBody of(TilePos pos,
                                     Float tileWidth,
                                     Float tileHeight,
                                     Integer layer,
                                     Boolean flyover) {
        TerrainTileBody body = new TerrainTileBody();
        body.pos = pos;
        body.tileWidth = tileWidth;
        body.tileHeight = tileHeight;
        body.layer = layer;
        body.flyover = flyover;
        return body;
    }

    Body b2Body;
    Polygon polygon;

    public void init(PhysicsWorld world) {
        b2Body = B2BodyFactory.createTerrainTileB2body(world, this);
        System.out.println("initing body");

        polygon = Polygon.of(new float[]{pos.x() * tileWidth, pos.y() * tileWidth, pos.x() * tileWidth + tileWidth,
                pos.y() * tileHeight,
                pos.x() * tileWidth + tileWidth, pos.y() * tileHeight + tileHeight, pos.x() * tileWidth,
                pos.y() * tileHeight + tileHeight});
    }
}
