package com.easternsauce.actionrpg.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class AreaRenderer {
  @Getter
  private EntityId<Area> id;
  @Getter
  private OrthogonalTiledMapRenderer tiledMapRenderer;

  public static AreaRenderer of(EntityId<Area> id) {
    AreaRenderer areaRenderer = AreaRenderer.of();
    areaRenderer.id = id;
    return areaRenderer;
  }

  public void init(TiledMap map, float mapScale) {
    tiledMapRenderer = new OrthogonalTiledMapRenderer(map, mapScale / Constants.PPM);
  }

  public void render(int[] layers) {
    tiledMapRenderer.render(layers);
  }

  public void setView(OrthographicCamera camera) {
    tiledMapRenderer.setView(camera);
  }

  @SuppressWarnings("unused")
  public void dispose() {
    tiledMapRenderer.dispose();
  }

}
