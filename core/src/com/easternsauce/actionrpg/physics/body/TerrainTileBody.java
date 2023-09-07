package com.easternsauce.actionrpg.physics.body;

import com.badlogic.gdx.physics.box2d.Body;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import com.easternsauce.actionrpg.physics.world.PhysicsWorld;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class TerrainTileBody {
  @Getter
  private Vector2Int pos;
  @Getter
  private Float tileWidth;
  @Getter
  private Float tileHeight;
  @Getter
  private Integer layer;
  @Getter
  private Boolean flyover;
  @Getter
  private Body b2body;
  @Getter
  private com.badlogic.gdx.math.Polygon polygon;

  public static TerrainTileBody of(Vector2Int pos, Float tileWidth, Float tileHeight, Integer layer, Boolean isFlyover) {
    TerrainTileBody body = new TerrainTileBody();
    body.pos = pos;
    body.tileWidth = tileWidth;
    body.tileHeight = tileHeight;
    body.layer = layer;
    body.flyover = isFlyover;
    return body;
  }

  public void init(PhysicsWorld world) {
    b2body = B2BodyFactory.createTerrainTileB2body(world, this);

    polygon = new com.badlogic.gdx.math.Polygon(new float[]{
      pos.getX() * tileWidth,
      pos.getY() * tileWidth,
      pos.getX() * tileWidth + tileWidth,
      pos.getY() * tileHeight,
      pos.getX() * tileWidth + tileWidth,
      pos.getY() * tileHeight + tileHeight, pos.getX() * tileWidth, pos.getY() * tileHeight + tileHeight});
  }
}
