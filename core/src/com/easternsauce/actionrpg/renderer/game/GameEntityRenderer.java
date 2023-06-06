package com.easternsauce.actionrpg.renderer.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureEffect;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Player;
import com.easternsauce.actionrpg.renderer.*;
import com.easternsauce.actionrpg.renderer.creature.CreatureRenderer;
import com.easternsauce.actionrpg.renderer.creature.LifeBarUtils;
import com.easternsauce.actionrpg.renderer.icons.IconRetriever;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class GameEntityRenderer {
    @Getter
    private ViewportsHandler viewportsHandler;

    @Getter
    private RenderingLayer worldElementsRenderingLayer;
    @Getter
    private RenderingLayer hudRenderingLayer;
    @Getter
    private RenderingLayer worldTextRenderingLayer;

    private float mapScale;

    private final TmxMapLoader mapLoader = new TmxMapLoader();

    @Getter
    private final Map<CreatureId, CreatureRenderer> creatureRenderers = new HashMap<>();
    @Getter
    private final Map<AbilityId, AbilityRenderer> abilityRenderers = new HashMap<>();
    @Getter
    private Map<AreaId, AreaRenderer> areaRenderers = new HashMap<>();
    @Getter
    private final Map<AreaGateId, AreaGateRenderer> areaGateRenderers = new HashMap<>();
    @Getter
    private final Map<LootPileId, LootPileRenderer> lootPileRenderers = new HashMap<>();

    @Getter
    private final IconRetriever iconRetriever = IconRetriever.of();

    private TextureRegion poisonedIcon = null;

    @Getter
    private Boolean isAreasLoaded = false;

    public void init(TextureAtlas atlas) {
        mapScale = 4.0f;

        worldElementsRenderingLayer = RenderingLayer.of();
        hudRenderingLayer = RenderingLayer.of();
        worldTextRenderingLayer = RenderingLayer.of();

        iconRetriever.init(atlas);

        viewportsHandler = ViewportsHandler.of();

        viewportsHandler.initViewports();

        poisonedIcon = atlas.findRegion("poisoned");
    }

    public void loadAreaRenderers(Map<AreaId, TiledMap> maps, @SuppressWarnings("unused") CoreGame game) {
        areaRenderers = new HashMap<>();
        areaRenderers.putAll(maps.keySet().stream().collect(Collectors.toMap(areaId -> areaId, AreaRenderer::of)));
        areaRenderers.forEach((areaId, areaRenderer) -> areaRenderer.init(maps.get(areaId), mapScale));
        isAreasLoaded = true;
    }

    public void renderAliveCreatures(RenderingLayer renderingLayer, CoreGame game) {
        game
            .getGameState()
            .accessCreatures()
            .forEachAliveCreature(creature -> renderCreatureIfPossible(renderingLayer, creature, game));
        game
            .getGameState()
            .accessCreatures()
            .forEachAliveCreature(creature -> renderCreatureLifeBarIfPossible(renderingLayer, creature, game));
        game.getGameState().accessCreatures().forEachAliveCreature(creature -> {
            if (canCreatureBeRendered(creature, game)) {
                renderCreatureStunnedAnimation(renderingLayer, creature, game);
                renderCreaturePoisonedIcon(renderingLayer, creature, game);
            }
        });
    }

    private void renderCreaturePoisonedIcon(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        if (creature != null && creature.isEffectActive(CreatureEffect.POISON, game)) {
            CreatureRenderer creatureRenderer = creatureRenderers.get(creature.getId());
            float spriteWidth = creatureRenderer.getCreatureSprite().getWidth();

            float posX = creature.getParams().getPos().getX() - 0.5f;
            float posY = LifeBarUtils.getLifeBarPosY(creature, spriteWidth) + 0.5f;
            renderingLayer.getSpriteBatch().draw(poisonedIcon, posX, posY, 1f, 1f);
        }
    }

    private void renderCreatureStunnedAnimation(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        CreatureRenderer creatureRenderer = creatureRenderers.get(creature.getId());
        float spriteWidth = creatureRenderer.getCreatureSprite().getWidth();
        creatureRenderer.getCreatureStunnedAnimationRenderer().render(renderingLayer, spriteWidth, game);

    }

    private void renderCreatureLifeBarIfPossible(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        if (canCreatureBeRendered(creature, game)) {
            creatureRenderers.get(creature.getId()).renderLifeBar(renderingLayer, game);
        }
    }

    private void renderCreatureIfPossible(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        if (canCreatureBeRendered(creature, game)) {
            creatureRenderers.get(creature.getId()).render(renderingLayer);
        }
    }

    public void renderDeadCreatures(RenderingLayer renderingLayer, CoreGame game) {
        game
            .getGameState()
            .accessCreatures()
            .forEachDeadCreature(creature -> renderCreatureIfPossible(renderingLayer, creature, game));
    }

    public void renderAbilities(RenderingLayer renderingLayer, CoreGame game) {
        getAbilityRenderers().values().forEach(abilityAnimation -> abilityAnimation.render(renderingLayer, game));
    }

    public void renderAreaGates(RenderingLayer renderingLayer, CoreGame game) {
        areaGateRenderers.values().forEach(areaGateRenderer -> areaGateRenderer.render(renderingLayer, game));
    }

    public void renderLootPiles(RenderingLayer renderingLayer, CoreGame game) {
        lootPileRenderers.values().forEach(lootPileRenderer -> lootPileRenderer.render(renderingLayer, game));
    }

    public void renderPlayerNames(RenderingLayer worldTextRenderingLayer, CoreGame game) {
        game
            .getGameState()
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(creature -> creature.isAlive() && canCreatureBeRendered(creature, game) && creature instanceof Player)
            .forEach(creature -> creatureRenderers.get(creature.getId()).renderCreatureId(worldTextRenderingLayer, game));
    }

    private boolean canCreatureBeRendered(Creature creature, CoreGame game) {
        return creatureRenderers.containsKey(creature.getId()) &&
               GameRendererHelper.isCreatureInCurrentlyVisibleArea(creature, game);
    }

    public TiledMap loadMap(String filePath) {
        return mapLoader.load(filePath);
    }

    public void setProjectionMatrices() {
        getWorldElementsRenderingLayer().setProjectionMatrix(getViewportsHandler().getWorldCamera().combined);

        getHudRenderingLayer().setProjectionMatrix(getViewportsHandler().getHudCamera().combined);

        getWorldTextRenderingLayer().setProjectionMatrix(getViewportsHandler().getWorldTextCamera().combined);
    }
}
