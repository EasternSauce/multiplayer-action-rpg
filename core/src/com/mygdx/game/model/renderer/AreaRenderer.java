package com.mygdx.game.model.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class AreaRenderer {

    OrthogonalTiledMapRenderer tiledMapRenderer;

    public void init(TiledMap map, float mapScale) {
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, mapScale / Constants.PPM);
    }

    public void render(int[] layers) {
        tiledMapRenderer.render(layers);
    }

    public void setView(OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
    }

    public void dispose() {
        tiledMapRenderer.dispose();
    }
}
