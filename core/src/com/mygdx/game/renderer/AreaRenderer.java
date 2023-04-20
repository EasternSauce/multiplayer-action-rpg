package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.Constants;
import com.mygdx.game.model.area.AreaId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class AreaRenderer {

    AreaId id;
    OrthogonalTiledMapRenderer tiledMapRenderer;

    public static AreaRenderer of(AreaId id) {
        AreaRenderer areaRenderer = AreaRenderer.of();
        areaRenderer.setId(id);
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
