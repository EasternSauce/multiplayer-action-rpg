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
import com.easternsauce.actionrpg.renderer.animationconfig.CreatureAnimationConfig;
import com.easternsauce.actionrpg.renderer.creature.*;
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
    private final CreatureStunnedAnimationRenderer creatureStunnedAnimationRenderer = CreatureStunnedAnimationRenderer.of();
    @Getter
    private final CreatureSlowedAnimationRenderer creatureSlowedAnimationRenderer = CreatureSlowedAnimationRenderer.of();
    @Getter
    private final CreatureHitAnimationRenderer creatureHitAnimationRenderer = CreatureHitAnimationRenderer.of();
    @Getter
    private final Map<String, CreatureModelAnimation> creatureModelAnimations = new HashMap<>();
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

        creatureStunnedAnimationRenderer.getAnimationRenderer().loadAnimation(atlas);
        creatureSlowedAnimationRenderer.getAnimationRenderer().loadAnimation(atlas);
        creatureHitAnimationRenderer.getAnimationRenderer().loadAnimation(atlas);

        CreatureAnimationConfig.configs.forEach((name, config) -> {
            CreatureModelAnimation modelAnimation = CreatureModelAnimation.of();
            modelAnimation.prepareRunningAnimations(config, atlas);
            modelAnimation.prepareFacingTextures(config, atlas);
            creatureModelAnimations.put(name, modelAnimation);
        });
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
                renderCreatureStunnedAnimation(creature.getId(), renderingLayer, game);
                renderCreatureSlowedAnimation(creature.getId(), renderingLayer, game);
                renderCreaturePoisonedIcon(renderingLayer, creature, game);
            }
        });
    }

    private boolean canCreatureBeRendered(Creature creature, CoreGame game) {
        return creatureRenderers.containsKey(creature.getId()) && isCreatureInCurrentlyVisibleArea(creature, game);
    }

    @SuppressWarnings("unused")
    private void renderCreature(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        creatureRenderers.get(creature.getId()).render(renderingLayer);
    }

    private void renderCreatureStunnedAnimation(CreatureId creatureId, RenderingLayer renderingLayer, CoreGame game) {
        CreatureRenderer creatureRenderer = creatureRenderers.get(creatureId);
        float spriteWidth = creatureRenderer.getCreatureSprite().getWidth();
        getCreatureStunnedAnimationRenderer().render(creatureId, spriteWidth, renderingLayer, game);

    }

    private void renderCreatureSlowedAnimation(CreatureId creatureId, RenderingLayer renderingLayer, CoreGame game) {
        CreatureRenderer creatureRenderer = creatureRenderers.get(creatureId);
        float spriteWidth = creatureRenderer.getCreatureSprite().getWidth();
        getCreatureSlowedAnimationRenderer().render(creatureId, spriteWidth, renderingLayer, game);
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

    private boolean isCreatureInCurrentlyVisibleArea(Creature creature, CoreGame game) {
        return creature.getParams().getAreaId().equals(game.getGameState().getCurrentAreaId());
    }

    public void renderCreatureLifeBars(RenderingLayer renderingLayer, CoreGame game) {
        game.getGameState().accessCreatures().forEachAliveCreature(creature -> {
            if (canCreatureBeRendered(creature, game)) {
                renderCreatureLifeBar(renderingLayer, creature, game);
            }
        });
    }

    private void renderCreatureLifeBar(RenderingLayer renderingLayer, Creature creature, CoreGame game) {
        creatureRenderers.get(creature.getId()).renderLifeBar(renderingLayer, game);

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
        game.getGameState().accessCreatures().getCreatures().values().stream().filter(creature -> creature.isAlive() &&
            canCreatureBeRendered(creature, game) &&
            creature instanceof Player).forEach(creature -> creatureRenderers
            .get(creature.getId())
            .renderCreatureName(worldTextRenderingLayer, game));
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
        Set<DamageNumber> toRemove = damageNumbers.stream().filter(damageNumber -> damageNumber.getDamageTime() +
            Constants.DAMAGE_NUMBER_SHOW_DURATION < game.getGameState().getTime()).collect(Collectors.toSet());
        damageNumbers.removeAll(toRemove);
    }

    public void updateCreatureHitAnimations(CoreGame game) {
        Set<CreatureHitAnimation> toRemove = creatureHitAnimations.stream().filter(creatureHitAnimation ->
            creatureHitAnimation.getHitTime() + Constants.DAMAGE_ANIMATION_DURATION <
                game.getGameState().getTime()).collect(Collectors.toSet());
        creatureHitAnimations.removeAll(toRemove);
    }

    public void showDamageNumber(float actualDamageTaken, Vector2 pos, AreaId areaId, CoreGame game) {
        Float currentTime = game.getGameState().getTime();

        float xNoise = ((float) Math.random() * 2f - 1f) * 0.7f;
        float yNoise = ((float) Math.random() * 2f - 1f) * 0.7f;

        damageNumbers.add(DamageNumber.of(Vector2.of(pos.getX() + xNoise, pos.getY() + yNoise),
            areaId,
            actualDamageTaken,
            currentTime,
            Math.abs((float) Math.random()) * 0.3f + 0.7f,
            Math.abs((float) Math.random()) * 0.6f,
            Math.abs((float) Math.random()) * 0.6f
        ));

    }

    public void startCreatureHitAnimation(CreatureId creatureId,
                                          Vector2 vectorTowardsContactPoint,
                                          AreaId areaId,
                                          CoreGame game) {
        Float currentTime = game.getGameState().getTime();

        CreatureHitAnimation animation = CreatureHitAnimation.of(creatureId,
            vectorTowardsContactPoint,
            areaId,
            currentTime
        );
        creatureHitAnimations.add(animation);
    }
}
