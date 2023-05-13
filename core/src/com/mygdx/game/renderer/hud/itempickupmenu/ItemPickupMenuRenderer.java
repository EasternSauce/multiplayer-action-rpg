package com.mygdx.game.renderer.hud.itempickupmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.assets.Assets;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.util.PlayerConfig;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.renderer.RenderingLayer;
import com.mygdx.game.renderer.icons.IconRetriever;
import com.mygdx.game.renderer.util.Rect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(staticName = "of")
@Data
public class ItemPickupMenuRenderer {
    private void renderMenuOption(RenderingLayer renderingLayer, IconRetriever iconRetriever, float x, float y, AtomicInteger i
        , Item item) {
        Rect rect = Rect.of(ItemPickupMenuModel.POS_X,
                            ItemPickupMenuModel.POS_Y + 25f * i.get(),
                            Gdx.graphics.getWidth() / 6f,
                            20f);
        renderingLayer
            .getShapeDrawer()
            .filledRectangle(rect.getX(),
                             rect.getY(),
                             rect.getWidth(),
                             rect.getHeight(),
                             Color.DARK_GRAY.cpy().sub(0, 0, 0, 0.3f));
        if (rect.contains(x, y)) {
            renderingLayer.getShapeDrawer().rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), Color.ORANGE);
        }
        renderingLayer
            .getSpriteBatch()
            .draw(iconRetriever.getIcon(item.getTemplate().getIconPos().getX(), item.getTemplate().getIconPos().getY()),
                  rect.getX() + 10f,
                  rect.getY(),
                  20f,
                  20f);
        Assets.renderSmallFont(renderingLayer,
                               item.getTemplate().getName(),
                               Vector2.of(rect.getX() + 40f, rect.getY() + 17f),
                               Color.CYAN);
        i.getAndIncrement();
    }

    public void render(RenderingLayer renderingLayer, CoreGame game) {
        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (playerConfig.getIsInventoryVisible()) {
            return;
        }

        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        IconRetriever iconRetriever = game.getEntityManager().getGameEntityRenderer().getIconRetriever();

        AtomicInteger i = new AtomicInteger();
        playerConfig
            .getItemPickupMenuLootPiles()
            .stream()
            .filter(lootPileId -> game.getGameState().getLootPiles().containsKey(lootPileId))
            .flatMap(lootPileId -> game.getGameState().getLootPile(lootPileId).getItems().stream())
            .forEach(item -> renderMenuOption(renderingLayer, iconRetriever, x, y, i, item));
    }

}
