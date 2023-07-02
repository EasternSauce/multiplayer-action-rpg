package com.easternsauce.actionrpg.renderer.hud.potionmenu;

import com.easternsauce.actionrpg.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PotionMenuConsts {
    public static final float SLOT_SIZE = 40f;
    public static final int MARGIN = 20;
    public static final int SPACE_BETWEEN_SLOTS = 12;

    public static final float SKILL_MENU_POS_X = 110f;
    public static final float SKILL_MENU_POS_Y = 40f;

    public static final int TOTAL_SKILL_SLOTS = 3;

    public static final Map<Integer, Rect> slotRectangles = new HashMap<>();

    static {
        for (int i = 0; i < PotionMenuConsts.TOTAL_SKILL_SLOTS; i++) {
            slotRectangles.put(i,
                Rect.of(getSlotPositionX(i),
                    getSlotPositionY(i),
                    PotionMenuConsts.SLOT_SIZE,
                    PotionMenuConsts.SLOT_SIZE
                )
            );
        }
    }

    public static float getSlotPositionX(Integer index) {
        int currentColumn = index;
        return PotionMenuConsts.SKILL_MENU_POS_X +
            PotionMenuConsts.MARGIN +
            (PotionMenuConsts.SLOT_SIZE + PotionMenuConsts.SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    public static float getSlotPositionY(@SuppressWarnings("unused") Integer index) {
        return PotionMenuConsts.SKILL_MENU_POS_Y + PotionMenuConsts.MARGIN;
    }

    public static boolean isMenuContainsPos(float x, float y) {
        Rect rect = Rect.of(
            SKILL_MENU_POS_X,
            SKILL_MENU_POS_Y,
            (PotionMenuConsts.SLOT_SIZE + PotionMenuConsts.SPACE_BETWEEN_SLOTS) * TOTAL_SKILL_SLOTS,
            PotionMenuConsts.MARGIN * 2 + PotionMenuConsts.SLOT_SIZE
        );
        return rect.contains(x, y);
    }

    public static Integer getPotionMenuClicked(float x, float y) {
        AtomicReference<Integer> atomicPotionMenuSlotClicked = new AtomicReference<>(null);

        PotionMenuConsts.slotRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(x, y)).forEach(
            entry -> atomicPotionMenuSlotClicked.set(entry.getKey()));

        return atomicPotionMenuSlotClicked.get();
    }
}
