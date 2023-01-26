package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class GameRenderer {
    OrthographicCamera worldCamera = new OrthographicCamera();
    OrthographicCamera hudCamera = new OrthographicCamera();

    Viewport worldViewport;

    Viewport hudViewport;

    DrawingLayer worldDrawingLayer;
    DrawingLayer hudDrawingLayer;
//
//    Map<CreatureId, Sprite> creatureSprites;
//

    OrthogonalTiledMapRenderer tiledMapRenderer;

    Map<AreaId, String> mapsToLoad;

    float mapScale;

    TmxMapLoader mapLoader = new TmxMapLoader();

    TextureAtlas atlas;

    Map<CreatureId, CreatureAnimation> creatureAnimations = new HashMap<>();
    Map<AbilityId, AbilityAnimation> abilityAnimations = new HashMap<>();

}
