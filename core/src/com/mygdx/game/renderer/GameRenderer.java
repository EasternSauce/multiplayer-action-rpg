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
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.renderer.creature.CreatureRenderer;
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
    OrthographicCamera worldTextCamera = new OrthographicCamera();

    Viewport worldViewport;
    Viewport hudViewport;
    Viewport worldTextViewport;

    RenderingLayer worldRenderingLayer;
    RenderingLayer hudRenderingLayer;
    RenderingLayer worldTextRenderingLayer;

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

    public void renderAliveCreatures(RenderingLayer renderingLayer, GameRenderable game) {
        game.getCreatures().values().stream().filter(Creature::isAlive).forEach(creature -> {
            if (creatureRenderers().containsKey(creature.id()) && isCreatureInCurrentlyVisibleArea(game, creature)) {
                creatureRenderers.get(creature.id()).render(renderingLayer);
            }
        });

        game.getCreatures().values().stream().filter(Creature::isAlive).forEach(creature -> {
            if (creatureRenderers().containsKey(creature.id()) && isCreatureInCurrentlyVisibleArea(game, creature)) {
                creatureRenderers.get(creature.id()).renderLifeBar(renderingLayer, game);
            }
        });


        game.getCreatures().values().stream().filter(Creature::isAlive).forEach(creature -> {
            if (creatureRenderers().containsKey(creature.id()) && isCreatureInCurrentlyVisibleArea(game, creature)) {
                CreatureRenderer creatureRenderer = creatureRenderers.get(creature.id());
                float spriteWidth = creatureRenderer.creatureSprite().getWidth();
                creatureRenderer.creatureStunnedAnimationRenderer().render(renderingLayer, spriteWidth, game);
            }
        });
    }

    public void renderDeadCreatures(RenderingLayer renderingLayer, GameRenderable game) {
        game.getCreatures().values().stream().filter(creature -> !creature.isAlive()).forEach(creature -> {
            if (creatureRenderers().containsKey(creature.id()) && isCreatureInCurrentlyVisibleArea(game, creature)) {
                creatureRenderers.get(creature.id()).render(renderingLayer);

            }
        });
    }

    private static boolean isCreatureInCurrentlyVisibleArea(GameRenderable game, Creature creature) {
        return creature.params().areaId().equals(game.getCurrentPlayerAreaId());
    }

    public void renderAreaGates(RenderingLayer renderingLayer, GameRenderable game) {
        areaGateRenderers.forEach(areaGateRenderer -> areaGateRenderer.render(renderingLayer, game));
    }

    public void renderLootPiles(RenderingLayer renderingLayer, GameRenderable game) {
        lootPileRenderers.values().forEach(lootPileRenderer -> lootPileRenderer.render(renderingLayer, game));
    }

    public void renderPlayerNames(RenderingLayer worldTextRenderingLayer, GameRenderable game) {
        game.getCreatures()
            .values()
            .stream()
            .filter(creature -> canCreatureBeRendered(game, creature) && creature instanceof Player)
            .forEach(creature -> creatureRenderers.get(creature.id()).renderCreatureId(worldTextRenderingLayer, game));
    }

    private boolean canCreatureBeRendered(GameRenderable game, Creature creature) {
        return creature.isAlive() &&
               creatureRenderers().containsKey(creature.id()) &&
               isCreatureInCurrentlyVisibleArea(game, creature);
    }
}
