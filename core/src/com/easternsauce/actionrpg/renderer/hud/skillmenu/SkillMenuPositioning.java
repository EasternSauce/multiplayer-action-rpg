package com.easternsauce.actionrpg.renderer.hud.skillmenu;

import com.badlogic.gdx.Gdx;
import com.easternsauce.actionrpg.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;

public class SkillMenuPositioning {
    public static final float SLOT_SIZE = 40f;
    public static final int MARGIN = 20;
    public static final int SPACE_BETWEEN_SLOTS = 12;

    public static final float SKILL_MENU_POS_X = 110f;
    public static final float SKILL_MENU_POS_Y = 69f;

    public static final int TOTAL_SKILL_SLOTS = 3;

    public static final float SKILL_PICKER_MENU_POS_X = 110f;
    public static final float SKILL_PICKER_MENU_POS_Y = 69f;
    public static final Map<Integer, Rect> skillRectangles = new HashMap<>();
    private static final float SKILL_PICKER_MENU_WIDTH = Gdx.graphics.getWidth() / 6f;
    private static final float SKILL_PICKER_MENU_HEIGHT = 20f;

    static {
        for (int i = 0; i < SkillMenuPositioning.TOTAL_SKILL_SLOTS; i++) {
            skillRectangles.put(i,
                                Rect.of(getSkillSlotPositionX(i),
                                        getSkillSlotPositionY(i),
                                        SkillMenuPositioning.SLOT_SIZE,
                                        SkillMenuPositioning.SLOT_SIZE));
        }
    }

    public static float getSkillSlotPositionX(Integer index) {
        int currentColumn = index;
        return SkillMenuPositioning.SKILL_MENU_POS_X + SkillMenuPositioning.MARGIN +
               (SkillMenuPositioning.SLOT_SIZE + SkillMenuPositioning.SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    public static float getSkillSlotPositionY(@SuppressWarnings("unused") Integer index) {
        return SkillMenuPositioning.SKILL_MENU_POS_Y - (SkillMenuPositioning.SLOT_SIZE + SkillMenuPositioning.MARGIN);
    }

    public static Rect getSkillPickerRect(int index) {
        return Rect.of(SkillMenuPositioning.SKILL_PICKER_MENU_POS_X,
                       SkillMenuPositioning.SKILL_PICKER_MENU_POS_Y + 25f * index,
                       SkillMenuPositioning.SKILL_PICKER_MENU_WIDTH,
                       SkillMenuPositioning.SKILL_PICKER_MENU_HEIGHT);
    }
}
