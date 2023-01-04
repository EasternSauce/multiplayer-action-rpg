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
    OrthographicCamera worldCamera;
    OrthographicCamera hudCamera;

    Viewport worldViewport;

    Viewport hudViewport;

    DrawingLayer worldDrawingLayer;
    DrawingLayer hudDrawingLayer;

    Map<CreatureId, Sprite> creatureSprites;


    OrthogonalTiledMapRenderer tiledMapRenderer;

    Map<AreaId, String> mapsToLoad;

    Map<AreaId, TiledMap> maps;

    float mapScale;

    TmxMapLoader mapLoader;
}
