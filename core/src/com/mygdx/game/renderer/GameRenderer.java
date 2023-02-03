package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    Map<AreaId, String> mapsToLoad;

    float mapScale;

    TmxMapLoader mapLoader = new TmxMapLoader();

    TextureAtlas atlas;

    Map<CreatureId, CreatureRenderer> creatureRenderers = new HashMap<>();
    Map<AbilityId, AbilityRenderer> abilityRenderers = new HashMap<>();
    Map<AreaId, AreaRenderer> areaRenderers = new HashMap<>();

    public void init(Map<AreaId, TiledMap> maps) {
        areaRenderers = maps.keySet().stream().collect(Collectors.toMap(areaId -> areaId, AreaRenderer::of));
        areaRenderers.forEach((areaId, areaRenderer) -> areaRenderer.init(maps.get(areaId), mapScale));
    }

    public void renderAliveCreatures(DrawingLayer drawingLayer, GameState gameState) {
        gameState.creatures().entrySet().stream().filter(entry -> entry.getValue().isAlive()).forEach(entry -> {
            if (creatureRenderers().containsKey(entry.getKey()) && entry.getValue().params().areaId()
                                                                        .equals(gameState.currentAreaId())) {
                creatureRenderers.get(entry.getKey()).render(drawingLayer);

            }
        });

        gameState.creatures().entrySet().stream().filter(entry -> entry.getValue().isAlive()).forEach(entry -> {
            if (creatureRenderers().containsKey(entry.getKey()) && entry.getValue().params().areaId()
                                                                        .equals(gameState.currentAreaId())) {
                creatureRenderers.get(entry.getKey()).renderLifeBar(drawingLayer, gameState);

            }
        });
    }

    public void renderDeadCreatures(DrawingLayer drawingLayer, GameState gameState) {
        gameState.creatures().entrySet().stream().filter(entry -> !entry.getValue().isAlive()).forEach(entry -> {
            if (creatureRenderers().containsKey(entry.getKey()) && entry.getValue().params().areaId()
                                                                        .equals(gameState.currentAreaId())) {
                creatureRenderers.get(entry.getKey()).render(drawingLayer);

            }
        });
    }
}
