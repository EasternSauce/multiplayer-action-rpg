package com.mygdx.game.renderer.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.game.game.interface_.GameRenderable;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.Player;
import com.mygdx.game.renderer.*;
import com.mygdx.game.renderer.creature.CreatureRenderer;
import com.mygdx.game.util.InventoryHelper;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class GameRenderer {
    private ViewportsHandler viewportsHandler;

    private RenderingLayer worldElementsRenderingLayer;
    private RenderingLayer hudRenderingLayer;
    private RenderingLayer worldTextRenderingLayer;

    private float mapScale;

    private final TmxMapLoader mapLoader = new TmxMapLoader();

    private TextureAtlas atlas;

    private final Map<CreatureId, CreatureRenderer> creatureRenderers = new HashMap<>();
    private final Map<AbilityId, AbilityRenderer> abilityRenderers = new HashMap<>();
    private final Map<AreaId, AreaRenderer> areaRenderers = new HashMap<>();
    private final Set<AreaGateRenderer> areaGateRenderers = new HashSet<>();
    private final Map<LootPileId, LootPileRenderer> lootPileRenderers = new HashMap<>();

    public void init(Map<AreaId, TiledMap> maps, GameRenderable game) {
        mapScale = 4.0f;

        worldElementsRenderingLayer = RenderingLayer.of();
        hudRenderingLayer = RenderingLayer.of();
        worldTextRenderingLayer = RenderingLayer.of();

        atlas = new TextureAtlas("assets/atlas/packed_atlas.atlas");

        areaRenderers.putAll(maps.keySet().stream().collect(Collectors.toMap(areaId -> areaId, AreaRenderer::of)));
        areaRenderers.forEach((areaId, areaRenderer) -> areaRenderer.init(maps.get(areaId), mapScale));

        areaGateRenderers.addAll(game.getGameState().getAreaGates()
                .stream()
                .map(areaGate -> AreaGateRenderer.of(areaGate, atlas))
                .collect(Collectors.toSet()));

        InventoryHelper.init(atlas);

        viewportsHandler = ViewportsHandler.of();

        viewportsHandler.initViewports();
    }

    public void renderAliveCreatures(RenderingLayer renderingLayer, GameRenderable game) {
        game.forEachAliveCreature(creature -> renderCreatureIfPossible(renderingLayer, game, creature));
        game.forEachAliveCreature(creature -> renderCreatureLifeBarIfPossible(renderingLayer, game, creature));
        game.forEachAliveCreature(creature -> renderCreatureStunnedAnimationIfPossible(renderingLayer, game, creature));
    }

    private void renderCreatureStunnedAnimationIfPossible(RenderingLayer renderingLayer,
                                                          GameRenderable game,
                                                          Creature creature) {
        if (canCreatureBeRendered(creature, game)) {
            CreatureRenderer creatureRenderer = creatureRenderers.get(creature.getId());
            float spriteWidth = creatureRenderer.getCreatureSprite().getWidth();
            creatureRenderer.getCreatureStunnedAnimationRenderer().render(renderingLayer, spriteWidth, game);
        }
    }

    private void renderCreatureLifeBarIfPossible(RenderingLayer renderingLayer,
                                                 GameRenderable game,
                                                 Creature creature) {
        if (canCreatureBeRendered(creature, game)) {
            creatureRenderers.get(creature.getId()).renderLifeBar(renderingLayer, game);
        }
    }

    private void renderCreatureIfPossible(RenderingLayer renderingLayer, GameRenderable game, Creature creature) {
        if (canCreatureBeRendered(creature, game)) {
            creatureRenderers.get(creature.getId()).render(renderingLayer);
        }
    }

    public void renderDeadCreatures(RenderingLayer renderingLayer, GameRenderable game) {
        game.forEachDeadCreature(creature -> renderCreatureIfPossible(renderingLayer, game, creature));
    }

    public void renderAbilities(RenderingLayer renderingLayer, GameRenderable game) {
        getAbilityRenderers().values().forEach(abilityAnimation -> abilityAnimation.render(renderingLayer, game));
    }

    public void renderAreaGates(RenderingLayer renderingLayer, GameRenderable game) {
        areaGateRenderers.forEach(areaGateRenderer -> areaGateRenderer.render(renderingLayer, game));
    }

    public void renderLootPiles(RenderingLayer renderingLayer, GameRenderable game) {
        lootPileRenderers.values().forEach(lootPileRenderer -> lootPileRenderer.render(renderingLayer, game));
    }

    public void renderPlayerNames(RenderingLayer worldTextRenderingLayer, GameRenderable game) {
        game.getGameState().getCreatures()
                .values()
                .stream()
                .filter(creature -> creature.isAlive() &&
                        canCreatureBeRendered(creature, game) &&
                        creature instanceof Player)
                .forEach(creature -> creatureRenderers.get(creature.getId())
                        .renderCreatureId(worldTextRenderingLayer, game));
    }

    private boolean canCreatureBeRendered(Creature creature, GameRenderable game) {
        return creatureRenderers.containsKey(creature.getId()) &&
                GameRendererHelper.isCreatureInCurrentlyVisibleArea(game, creature);
    }

    public TiledMap loadMap(String filePath) {
        return mapLoader.load(filePath);
    }

    public Map<CreatureId, CreatureRenderer> getCreatureRenderers() {
        return creatureRenderers;
    }

    public Map<AbilityId, AbilityRenderer> getAbilityRenderers() {
        return abilityRenderers;
    }

    public Map<AreaId, AreaRenderer> getAreaRenderers() {
        return areaRenderers;
    }

    public Map<LootPileId, LootPileRenderer> getLootPileRenderers() {
        return lootPileRenderers;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public RenderingLayer getWorldElementsRenderingLayer() {
        return worldElementsRenderingLayer;
    }

    public RenderingLayer getHudRenderingLayer() {
        return hudRenderingLayer;
    }


    public RenderingLayer getWorldTextRenderingLayer() {
        return worldTextRenderingLayer;
    }

    public ViewportsHandler getViewportsHandler() {
        return viewportsHandler;
    }
}
