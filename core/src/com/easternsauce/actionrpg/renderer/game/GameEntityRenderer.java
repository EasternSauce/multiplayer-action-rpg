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
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.*;
import com.easternsauce.actionrpg.renderer.creature.CreatureRenderer;
import com.easternsauce.actionrpg.renderer.creature.LifeBarUtils;
import com.easternsauce.actionrpg.renderer.icons.IconRetriever;
import com.easternsauce.actionrpg.util.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class GameEntityRenderer {
    private final TmxMapLoader mapLoader = new TmxMapLoader();
    @Getter
    private final Map<CreatureId, CreatureRenderer> creatureRenderers = new HashMap<>();
    @Getter
    private final Map<AbilityId, AbilityRenderer> abilityRenderers = new HashMap<>();
    @Getter
    private final Map<AreaGateId, AreaGateRenderer> areaGateRenderers = new HashMap<>();
    @Getter
    private final Map<LootPileId, LootPileRenderer> lootPileRenderers = new HashMap<>();
    @Getter
    private final Set<DamageNumber> damageNumbers = ConcurrentHashMap.newKeySet();
    @Getter
    private final Set<CreatureHitAnimation> creatureHitAnimations = ConcurrentHashMap.newKeySet();
    @Getter
    private final IconRetriever iconRetriever = IconRetriever.of();
    @Getter
    private ViewportsHandler viewportsHandler;
    @Getter
    private RenderingLayer worldElementsRenderingLayer;
    @Getter
    private RenderingLayer hudRenderingLayer;
    @Getter
    private RenderingLayer worldTextRenderingLayer;
    private float mapScale;
    @Getter
    private Map<AreaId, AreaRenderer> areaRenderers = new HashMap<>();
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
        game.getGameState().accessCreatures().forEachAliveCreature(creature -> {
            if (canCreatureBeRendered(creature, game)) {
                renderCreature(renderingLayer, creature, game);
            }
        });
        game.getGameState().accessCreatures().forEachAliveCreature(creature -> {
            if (canCreatureBeRendered(creature, game)) {
                renderCreatureLifeBar(renderingLayer, creature, game);
            }
        });
        game.getGameState().accessCreatures().forEachAliveCreature(creature -> {
            if (canCreatureBeRendered(creature, game)) {
                renderCreatureStunnedAnimation(renderingLayer, creature, game);
                renderCreaturePoisonedIcon(renderingLayer, creature, game);
            }
        });
    }

    private boolean canCreatureBeRendered(Creature creature, CoreGame game) {
        return creatureRenderers.containsKey(creature.getId()) &&
               GameRendererHelper.isCreatureInCurrentlyVisibleArea(creature, game);
    }

    @SuppressWarnings("unused")
    private void renderCreature(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        creatureRenderers.get(creature.getId()).render(renderingLayer);
    }

    private void renderCreatureLifeBar(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        creatureRenderers.get(creature.getId()).renderLifeBar(renderingLayer, game);

    }

    private void renderCreatureStunnedAnimation(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        CreatureRenderer creatureRenderer = creatureRenderers.get(creature.getId());
        float spriteWidth = creatureRenderer.getCreatureSprite().getWidth();
        creatureRenderer.getCreatureStunnedAnimationRenderer().render(spriteWidth, renderingLayer, game);

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

    public void renderDeadCreatures(RenderingLayer renderingLayer, CoreGame game) {
        game.getGameState().accessCreatures().forEachDeadCreature(creature -> {
            if (canCreatureBeRendered(creature, game)) {
                renderCreature(renderingLayer, creature, game);
            }
        });
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
            .forEach(creature -> creatureRenderers.get(creature.getId()).renderCreatureName(worldTextRenderingLayer, game));
    }

    public TiledMap loadMap(String filePath) {
        return mapLoader.load(filePath);
    }

    public void setProjectionMatrices() {
        getWorldElementsRenderingLayer().setProjectionMatrix(getViewportsHandler().getWorldCamera().combined);

        getHudRenderingLayer().setProjectionMatrix(getViewportsHandler().getHudCamera().combined);

        getWorldTextRenderingLayer().setProjectionMatrix(getViewportsHandler().getWorldTextCamera().combined);
    }

    public void updateDamageNumbers(CoreGame game) {
        Set<DamageNumber> toRemove = damageNumbers
            .stream()
            .filter(damageNumber -> damageNumber.getDamageTime() + Constants.DAMAGE_NUMBER_SHOW_DURATION <
                                    game.getGameState().getTime())
            .collect(Collectors.toSet());
        damageNumbers.removeAll(toRemove);
    }

    public void updateCreatureHitAnimations(CoreGame game) {
        Set<CreatureHitAnimation> toRemove = creatureHitAnimations
            .stream()
            .filter(creatureHitAnimation -> creatureHitAnimation.getHitTime() + Constants.DAMAGE_ANIMATION_DURATION <
                                            game.getGameState().getTime())
            .collect(Collectors.toSet());
        creatureHitAnimations.removeAll(toRemove);
    }

    public void showDamageNumber(float actualDamageTaken, Vector2 pos, AreaId areaId, CoreGame game) {
        Float currentTime = game.getGameState().getTime();

        damageNumbers.add(DamageNumber.of(pos, areaId, actualDamageTaken, currentTime));
    }

    public void startCreatureHitAnimation(CreatureId creatureId, Vector2 vectorTowardsContactPoint, AreaId areaId,
                                          CoreGame game) {
        Float currentTime = game.getGameState().getTime();

        CreatureHitAnimation animation = CreatureHitAnimation.of(creatureId, vectorTowardsContactPoint, areaId, currentTime);
        creatureHitAnimations.add(animation);
    }
}
