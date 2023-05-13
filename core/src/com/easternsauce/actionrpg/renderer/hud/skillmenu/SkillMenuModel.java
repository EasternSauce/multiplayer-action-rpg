package com.easternsauce.actionrpg.renderer.hud.skillmenu;

import com.easternsauce.actionrpg.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;

public class SkillMenuModel {
    public static final float SLOT_SIZE = 40f;
    public static final int MARGIN = 20;
    public static final int SPACE_BETWEEN_SLOTS = 12;

    public static final float SKILL_MENU_POS_X = 110f;
    public static final float SKILL_MENU_POS_Y = 69f;

    public static final int TOTAL_SKILL_SLOTS = 3;

    public static final float SKILL_PICKER_MENU_POS_X = 110f;
    public static final float SKILL_PICKER_MENU_POS_Y = 69f;

    public static final Map<Integer, Rect> skillRectangles = new HashMap<>();

    static {
        for (int i = 0; i < SkillMenuModel.TOTAL_SKILL_SLOTS; i++) {
            skillRectangles.put(i,
                                Rect.of(skillSlotPositionX(i),
                                        SkillSlotPositionY(i),
                                        SkillMenuModel.SLOT_SIZE,
                                        SkillMenuModel.SLOT_SIZE));
        }
    }

    public static float skillSlotPositionX(Integer index) {
        int currentColumn = index;
        return SkillMenuModel.SKILL_MENU_POS_X + SkillMenuModel.MARGIN +
               (SkillMenuModel.SLOT_SIZE + SkillMenuModel.SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    public static float SkillSlotPositionY(@SuppressWarnings("unused") Integer index) {
        return SkillMenuModel.SKILL_MENU_POS_Y - (SkillMenuModel.SLOT_SIZE + SkillMenuModel.MARGIN);
    }
}
