package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.assets.Assets;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.Vector2Int;
import com.easternsauce.actionrpg.renderer.RenderingLayer;
import com.easternsauce.actionrpg.renderer.icons.IconRetriever;
import com.easternsauce.actionrpg.renderer.util.Rect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(staticName = "of")
@Data
public class PotionMenuRenderer {
    public void renderMenu(RenderingLayer renderingLayer, CoreGame game) {

        Creature player = game.getGameState().accessCreatures().getCreature(game
            .getGameState()
            .getThisClientPlayerId());

        PlayerConfig playerConfig = game.getGameState().getPlayerConfig(game.getGameState().getThisClientPlayerId());

        if (player == null || playerConfig == null) {
            return;
        }

        Map<Integer, Item> potionMenuItems = player.getParams().getPotionMenuItems();

        Map<Integer, String> keys = new HashMap<>();
        keys.put(0, "1");
        keys.put(1, "2");
        keys.put(2, "3");
        keys.put(3, "4");
        keys.put(4, "5");

        IconRetriever iconRetriever = game.getEntityManager().getGameEntityRenderer().getIconRetriever();

        AtomicInteger i = new AtomicInteger();
        PotionMenuConsts.slotRectangles.values().forEach(rect -> {
            renderSlot(renderingLayer, rect);

            renderSlotIndex(renderingLayer, keys, i, rect);

            if (potionMenuItems.containsKey(i.get())) {
                Item item = potionMenuItems.get(i.get());

                if (!isPotionMenuItemBeingMoved(playerConfig, i.get())) {
                    renderSlotItem(renderingLayer, iconRetriever, item, rect);
                }

            }

            i.getAndIncrement();
        });
    }

    private static void renderSlot(RenderingLayer renderingLayer, Rect rect) {
        renderingLayer.getShapeDrawer().filledRectangle(rect.getX() - 3,
            rect.getY() - 3,
            rect.getWidth() + 6,
            rect.getHeight() + 6,
            Color.WHITE
        );
        renderingLayer.getShapeDrawer().filledRectangle(rect.getX(),
            rect.getY(),
            rect.getWidth(),
            rect.getHeight(),
            Color.BLACK
        );
    }

    private static void renderSlotIndex(RenderingLayer renderingLayer,
                                        Map<Integer, String> keys,
                                        AtomicInteger i,
                                        Rect rect) {
        Assets.renderVerySmallFont(renderingLayer,
            keys.get(i.get()),
            Vector2.of(rect.getX() + PotionMenuConsts.SLOT_SIZE - 14f, rect.getY() + PotionMenuConsts.SLOT_SIZE - 5f),
            Color.CYAN
        );
    }

    private boolean isPotionMenuItemBeingMoved(PlayerConfig playerConfig, Integer index) {
        boolean isPotionMenuItemBeingMoved = false;
        if (playerConfig.getPotionMenuItemBeingMoved() != null) {
            isPotionMenuItemBeingMoved = Objects.equals(playerConfig.getPotionMenuItemBeingMoved(), index);
        }
        return isPotionMenuItemBeingMoved;
    }

    private void renderSlotItem(RenderingLayer renderingLayer, IconRetriever iconRetriever, Item item, Rect rect) {
        Vector2Int iconPos = item.getTemplate().getIconPos();
        TextureRegion textureRegion = iconRetriever.getIcon(iconPos.getX(), iconPos.getY());

        renderingLayer.getSpriteBatch().draw(textureRegion,
            rect.getX(),
            rect.getY(),
            PotionMenuConsts.SLOT_SIZE,
            PotionMenuConsts.SLOT_SIZE
        );

        if (item.getQuantity() > 1) {
            renderItemQuantity(item, rect.getX(), rect.getY(), renderingLayer);
        }
    }

    private void renderItemQuantity(Item item, float slotX, float slotY, RenderingLayer renderingLayer) {
        Assets.renderSmallFont(renderingLayer,
            item.getQuantity().toString(),
            Vector2.of(slotX, slotY + 15),
            Color.WHITE
        );
    }
}
