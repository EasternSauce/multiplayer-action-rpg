package com.mygdx.game.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.model.util.Vector2Int;
import com.mygdx.game.physics.world.PhysicsWorld;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class TerrainTileBody {
    Vector2Int pos;
    Float tileWidth;
    Float tileHeight;
    Integer layer;
    Boolean isFlyover;
    Body b2Body;
    com.badlogic.gdx.math.Polygon polygon;

    public static TerrainTileBody of(Vector2Int pos, Float tileWidth, Float tileHeight, Integer layer, Boolean isFlyover) {
        TerrainTileBody body = new TerrainTileBody();
        body.pos = pos;
        body.tileWidth = tileWidth;
        body.tileHeight = tileHeight;
        body.layer = layer;
        body.isFlyover = isFlyover;
        return body;
    }

    public void init(PhysicsWorld world) {
        b2Body = B2BodyFactory.createTerrainTileB2body(world, this);

        polygon = new com.badlogic.gdx.math.Polygon(new float[]{
            pos.getX() * tileWidth,
            pos.getY() * tileWidth,
            pos.getX() * tileWidth + tileWidth,
            pos.getY() * tileHeight,
            pos.getX() * tileWidth + tileWidth,
            pos.getY() * tileHeight + tileHeight, pos.getX() * tileWidth, pos.getY() * tileHeight + tileHeight});
    }
}
