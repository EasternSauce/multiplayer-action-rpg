package com.easternsauce.actionrpg.renderer.hud;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.hud.inventorywindow.InventoryWindowRenderer;
import com.easternsauce.actionrpg.renderer.hud.itempickupmenu.ItemPickupMenuRenderer;
import com.easternsauce.actionrpg.renderer.hud.potionmenu.PotionMenuRenderer;
import com.easternsauce.actionrpg.renderer.hud.skillmenu.SkillMenuRenderer;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class HudRenderer {
    FpsCounterRenderer fpsCounterRenderer = FpsCounterRenderer.of();
    InventoryWindowRenderer inventoryWindowRenderer = InventoryWindowRenderer.of();
    PlayerStatBarsRenderer playerStatBarsRenderer = PlayerStatBarsRenderer.of();
    RespawnMessageRenderer respawnMessageRenderer = RespawnMessageRenderer.of();
    SkillMenuRenderer skillMenuRenderer = SkillMenuRenderer.of();
    ItemPickupMenuRenderer pickUpMenuRenderer = ItemPickupMenuRenderer.of();
    PotionMenuRenderer potionMenuRenderer = PotionMenuRenderer.of();

    public void init(TextureAtlas atlas) {
        inventoryWindowRenderer.init(atlas);
    }

    public void render(CoreGame game) {
        RenderingLayer renderingLayer = game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer();

        renderingLayer.begin();

        game.getChat().render(renderingLayer);

        fpsCounterRenderer.render(renderingLayer);

        if (game.getGameState().getThisClientPlayerId() != null) {
            Creature player = game.getGameState().accessCreatures().getCreature(game
                .getGameState()
                .getThisClientPlayerId());

            skillMenuRenderer.renderMenu(
                renderingLayer,
                game
            );

            skillMenuRenderer.renderPicker(
                player,
                renderingLayer,
                game
            );

            respawnMessageRenderer.render(
                player,
                renderingLayer
            );

            playerStatBarsRenderer.render(
                player,
                renderingLayer
            );

        }

        inventoryWindowRenderer.render(
            renderingLayer,
            game
        );

        pickUpMenuRenderer.render(
            renderingLayer,
            game
        );

        potionMenuRenderer.renderMenu(
            renderingLayer,
            game
        );

        renderingLayer.end();
    }

}
