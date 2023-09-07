package com.easternsauce.actionrpg.renderer.hud.inventorywindow;

import com.easternsauce.actionrpg.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PotionMenuConsts {
  public static final float SLOT_SIZE = 40f;
  public static final int SPACE_BETWEEN_SLOTS = 12;

  public static final float MENU_POS_X = 130f;
  public static final float MENU_POS_Y = 60f;

  public static final int TOTAL_POTION_MENU_SLOTS = 4;

  public static final Map<Integer, Rect> slotRectangles = new HashMap<>();

  static {
    for (int i = 0; i < PotionMenuConsts.TOTAL_POTION_MENU_SLOTS; i++) {
      slotRectangles.put(i,
        Rect.of(getSlotPositionX(i), getSlotPositionY(i), PotionMenuConsts.SLOT_SIZE, PotionMenuConsts.SLOT_SIZE));
    }
  }

  public static float getSlotPositionX(Integer index) {
    int currentColumn = index;
    return PotionMenuConsts.MENU_POS_X +
      (PotionMenuConsts.SLOT_SIZE + PotionMenuConsts.SPACE_BETWEEN_SLOTS) * currentColumn;
  }

  public static float getSlotPositionY(@SuppressWarnings("unused") Integer index) {
    return PotionMenuConsts.MENU_POS_Y;
  }

  public static boolean isMenuContainsPos(float x, float y) {
    Rect rect = Rect.of(MENU_POS_X, MENU_POS_Y,
      (PotionMenuConsts.SLOT_SIZE + PotionMenuConsts.SPACE_BETWEEN_SLOTS) * TOTAL_POTION_MENU_SLOTS,
      PotionMenuConsts.SLOT_SIZE);
    return rect.contains(x, y);
  }

  public static Integer getPotionMenuClicked(float x, float y) {
    AtomicReference<Integer> atomicPotionMenuSlotClicked = new AtomicReference<>(null);

    PotionMenuConsts.slotRectangles.entrySet().stream().filter(entry -> entry.getValue().contains(x, y))
      .forEach(entry -> atomicPotionMenuSlotClicked.set(entry.getKey()));

    return atomicPotionMenuSlotClicked.get();
  }
}
