package com.mygdx.game.renderer;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Constants;
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
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class GameRenderer {
    private OrthographicCamera worldCamera = new OrthographicCamera();
    private OrthographicCamera hudCamera = new OrthographicCamera();
    private OrthographicCamera worldTextCamera = new OrthographicCamera();

    private Viewport worldViewport;
    private Viewport hudViewport;
    private Viewport worldTextViewport;

    private RenderingLayer worldRenderingLayer;
    private RenderingLayer hudRenderingLayer;
    private RenderingLayer worldTextRenderingLayer;

    private float mapScale;

    private TmxMapLoader mapLoader = new TmxMapLoader();

    private TextureAtlas atlas;

    private Map<CreatureId, CreatureRenderer> creatureRenderers = new HashMap<>();
    private Map<AbilityId, AbilityRenderer> abilityRenderers = new HashMap<>();
    private Map<AreaId, AreaRenderer> areaRenderers = new HashMap<>();

    private Set<AreaGateRenderer> areaGateRenderers = new HashSet<>();

    private Map<LootPileId, LootPileRenderer> lootPileRenderers = new HashMap<>();

    public void init(Map<AreaId, TiledMap> maps, GameRenderable game) {
        mapScale = 4.0f;

        worldRenderingLayer = RenderingLayer.of();
        hudRenderingLayer = RenderingLayer.of();
        worldTextRenderingLayer = RenderingLayer.of();

        atlas = new TextureAtlas("assets/atlas/packed_atlas.atlas");

        areaRenderers = maps.keySet().stream().collect(Collectors.toMap(areaId -> areaId, AreaRenderer::of));
        areaRenderers.forEach((areaId, areaRenderer) -> areaRenderer.init(maps.get(areaId), mapScale));
        areaGateRenderers =
                game.getAreaGates()
                    .stream()
                    .map(areaGate -> AreaGateRenderer.of(areaGate, atlas))
                    .collect(Collectors.toSet());

        InventoryHelper.init(atlas);

        initViewports();
    }

    private void initViewports() {
        worldViewport = new FitViewport(Constants.ViewpointWorldWidth / Constants.PPM,
                                        Constants.ViewpointWorldHeight / Constants.PPM,
                                        worldCamera);


        hudViewport = new FitViewport((float) Constants.WindowWidth,
                                      (float) Constants.WindowHeight,
                                      hudCamera);


        worldTextViewport = new FitViewport(Constants.ViewpointWorldWidth,
                                            Constants.ViewpointWorldHeight,
                                            worldTextCamera);
    }

    public void updateViewportsOnResize(int width, int height) {
        worldViewport.update(width, height);
        hudViewport.update(width, height);
        worldTextViewport.update(width, height);
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
            CreatureRenderer creatureRenderer = creatureRenderers.get(creature.id());
            float spriteWidth = creatureRenderer.creatureSprite().getWidth();
            creatureRenderer.creatureStunnedAnimationRenderer().render(renderingLayer, spriteWidth, game);
        }
    }

    private void renderCreatureLifeBarIfPossible(RenderingLayer renderingLayer,
                                                 GameRenderable game,
                                                 Creature creature) {
        if (canCreatureBeRendered(creature, game)) {
            creatureRenderers.get(creature.id()).renderLifeBar(renderingLayer, game);
        }
    }

    private void renderCreatureIfPossible(RenderingLayer renderingLayer, GameRenderable game, Creature creature) {
        if (canCreatureBeRendered(creature, game)) {
            creatureRenderers.get(creature.id()).render(renderingLayer);
        }
    }

    public void renderDeadCreatures(RenderingLayer renderingLayer, GameRenderable game) {
        game.forEachDeadCreature(creature -> renderCreatureIfPossible(renderingLayer, game, creature));
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
            .filter(creature -> creature.isAlive() &&
                                canCreatureBeRendered(creature, game) &&
                                creature instanceof Player)
            .forEach(creature -> creatureRenderers.get(creature.id()).renderCreatureId(worldTextRenderingLayer, game));
    }

    private boolean canCreatureBeRendered(Creature creature, GameRenderable game) {
        return creatureRenderers.containsKey(creature.id()) &&
               GameRendererHelper.isCreatureInCurrentlyVisibleArea(game, creature);
    }

    public void setWorldCameraPosition(float x, float y) {
        worldCamera.position.x = x;
        worldCamera.position.y = y;
    }

    public void setWorldTextCameraPosition(float x, float y) {
        worldTextCamera.position.x = x;
        worldTextCamera.position.y = y;
    }

    public void updateCameras() {
        worldCamera.update();
        worldTextCamera.update();
    }


    public Matrix4 getWorldCameraCombinedProjectionMatrix() {
        return worldCamera.combined;
    }

    public void unprojectHudCamera(Vector3 screenCoords) {
        hudCamera.unproject(screenCoords);
    }

    public TiledMap loadMap(String filePath) {
        return mapLoader.load(filePath);
    }

    public void setHudCameraPosition(float x, float y) {
        hudCamera.position.set(x, y, 0);
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

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    public OrthographicCamera getWorldTextCamera() {
        return worldTextCamera;
    }

    public RenderingLayer getWorldRenderingLayer() {
        return worldRenderingLayer;
    }

    public RenderingLayer getHudRenderingLayer() {
        return hudRenderingLayer;
    }


    public RenderingLayer getWorldTextRenderingLayer() {
        return worldTextRenderingLayer;
    }

}
