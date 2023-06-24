package com.easternsauce.actionrpg.renderer.hud.itempickupmenu;

import com.badlogic.gdx.graphics.Color;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.icons.IconRetriever;
import com.easternsauce.actionrpg.renderer.util.Rect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(staticName = "of")
@Data
public class ItemPickupMenuRenderer {
    public void render(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig == null || playerConfig.getIsInventoryVisible()) {
            return;
        }

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        IconRetriever iconRetriever = game.getEntityManager().getGameEntityRenderer().getIconRetriever();

        AtomicInteger i = new AtomicInteger();
        playerConfig.getItemPickupMenuLootPiles().stream().filter(lootPileId -> game
            .getGameState()
            .getLootPiles()
            .containsKey(lootPileId)).flatMap(lootPileId -> game
            .getGameState()
            .getLootPile(lootPileId)
            .getParams()
            .getItems()
            .stream()).forEach(item -> renderMenuOption(
            renderingLayer,
            iconRetriever,
            x,
            y,
            i,
            item
        ));
    }

    private void renderMenuOption(
        RenderingLayer renderingLayer,
        IconRetriever iconRetriever,
        float x,
        float y,
        AtomicInteger i,
        Item item
    ) {
        Rect rect = ItemPickupMenuPositioning.getMenuOptionRect(i.get());
        renderingLayer.getShapeDrawer().filledRectangle(
            rect.getX(),
            rect.getY(),
            rect.getWidth(),
            rect.getHeight(),
            Color.DARK_GRAY.cpy().sub(
                0,
                0,
                0,
                0.3f
            )
        );
        if (rect.contains(
            x,
            y
        )) {
            renderingLayer.getShapeDrawer().rectangle(
                rect.getX(),
                rect.getY(),
                rect.getWidth(),
                rect.getHeight(),
                Color.ORANGE
            );
        }
        renderingLayer.getSpriteBatch().draw(
            iconRetriever.getIcon(
                item.getTemplate().getIconPos().getX(),
                item.getTemplate().getIconPos().getY()
            ),
            rect.getX() + 10f,
            rect.getY(),
            20f,
            20f
        );
        Assets.renderSmallFont(
            renderingLayer,
            item.getTemplate().getName(),
            Vector2.of(
                rect.getX() + 40f,
                rect.getY() + 17f
            ),
            Color.CYAN
        );
        i.getAndIncrement();
    }

}
