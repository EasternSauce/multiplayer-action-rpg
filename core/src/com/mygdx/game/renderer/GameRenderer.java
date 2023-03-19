package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.InventoryHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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


    Map<AreaId, String> mapsToLoad;

    float mapScale;

    TmxMapLoader mapLoader = new TmxMapLoader();

    TextureAtlas atlas;

    Map<CreatureId, CreatureRenderer> creatureRenderers = new HashMap<>();
    Map<AbilityId, AbilityRenderer> abilityRenderers = new HashMap<>();
    Map<AreaId, AreaRenderer> areaRenderers = new HashMap<>();

    Set<AreaGateRenderer> areaGateRenderers = new HashSet<>();

    Map<LootPileId, LootPileRenderer> lootPileRenderers = new HashMap<>();

    public void init(Map<AreaId, TiledMap> maps, GameRenderable game) {
        areaRenderers = maps.keySet().stream().collect(Collectors.toMap(areaId -> areaId, AreaRenderer::of));
        areaRenderers.forEach((areaId, areaRenderer) -> areaRenderer.init(maps.get(areaId), mapScale));
        areaGateRenderers =
                game.getAreaGates()
                    .stream()
                    .map(areaGate -> AreaGateRenderer.of(areaGate, atlas))
                    .collect(Collectors.toSet());

        InventoryHelper.init(atlas);
    }

    public void renderAliveCreatures(DrawingLayer drawingLayer, GameRenderable game) {
        game.getCreatures().entrySet().stream().filter(entry -> entry.getValue().isAlive()).forEach(entry -> {
            if (creatureRenderers().containsKey(entry.getKey()) && entry.getValue()
                                                                        .params()
                                                                        .areaId()
                                                                        .equals(game.getCurrentPlayerAreaId())) {
                creatureRenderers.get(entry.getKey()).render(drawingLayer);

            }
        });

        game.getCreatures().entrySet().stream().filter(entry -> entry.getValue().isAlive()).forEach(entry -> {
            if (creatureRenderers().containsKey(entry.getKey()) && entry.getValue()
                                                                        .params()
                                                                        .areaId()
                                                                        .equals(game.getCurrentPlayerAreaId())) {
                creatureRenderers.get(entry.getKey()).renderLifeBar(drawingLayer, game);

            }
        });
    }

    public void renderDeadCreatures(DrawingLayer drawingLayer, GameRenderable game) {
        game.getCreatures().entrySet().stream().filter(entry -> !entry.getValue().isAlive()).forEach(entry -> {
            if (creatureRenderers().containsKey(entry.getKey()) && entry.getValue()
                                                                        .params()
                                                                        .areaId()
                                                                        .equals(game.getCurrentPlayerAreaId())) {
                creatureRenderers.get(entry.getKey()).render(drawingLayer);

            }
        });
    }

    public void renderAreaGates(DrawingLayer drawingLayer, GameRenderable game) {
        areaGateRenderers.forEach(areaGateRenderer -> areaGateRenderer.render(drawingLayer, game));
    }

    public void renderLootPiles(DrawingLayer drawingLayer, GameRenderable game) {
        lootPileRenderers.values().forEach(lootPileRenderer -> lootPileRenderer.render(drawingLayer, game));
    }
}
