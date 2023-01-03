package com.mygdx.game.model.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.renderer.DrawingLayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class GameRenderer {
    private OrthographicCamera worldCamera;
    private OrthographicCamera hudCamera;

    private Viewport worldViewport;

    private Viewport hudViewport;

    private DrawingLayer worldDrawingLayer;
    private DrawingLayer hudDrawingLayer;

    private Map<CreatureId, Sprite> creatureSprites;


    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private Map<AreaId, String> mapsToLoad;

    private Map<AreaId, TiledMap> maps;

    private float mapScale;

    private TmxMapLoader mapLoader;
}
