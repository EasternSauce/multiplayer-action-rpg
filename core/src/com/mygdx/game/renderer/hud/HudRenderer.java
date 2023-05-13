package com.mygdx.game.renderer.hud;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.util.SkillMenuHelper;
import com.mygdx.game.util.InventoryHelper;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class HudRenderer {
    FpsCounterRenderer fpsCounterRenderer = FpsCounterRenderer.of();
    InventoryRenderer inventoryRenderer = InventoryRenderer.of();
    PlayerStatBarsRenderer playerStatBarsRenderer = PlayerStatBarsRenderer.of();
    RespawnMessageRenderer respawnMessageRenderer = RespawnMessageRenderer.of();

    public void render(CoreGame game) {
        RenderingLayer renderingLayer = game.getEntityManager().getGameEntityRenderer().getHudRenderingLayer();

        renderingLayer.begin();

        game.getChat().render(renderingLayer);

        fpsCounterRenderer.render(renderingLayer);

        if (game.getGameState().getThisClientPlayerId() != null) {
            Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());

            SkillMenuHelper.renderSkillMenu(renderingLayer, game);

            SkillMenuHelper.renderSkillPickerMenu(player, renderingLayer, game);

            respawnMessageRenderer.render(player, renderingLayer);

            playerStatBarsRenderer.render(player, renderingLayer);

        }

        InventoryHelper.render(renderingLayer, game);

        renderingLayer.end();
    }
}
