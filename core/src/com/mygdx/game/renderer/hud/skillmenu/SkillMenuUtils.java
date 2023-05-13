package com.mygdx.game.renderer.hud.skillmenu;

import com.mygdx.game.renderer.util.Rect;

import java.util.HashMap;
import java.util.Map;

public class SkillMenuUtils {
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
        for (int i = 0; i < SkillMenuUtils.TOTAL_SKILL_SLOTS; i++) {
            skillRectangles.put(i,
                                Rect.of(skillSlotPositionX(i),
                                        SkillSlotPositionY(i),
                                        SkillMenuUtils.SLOT_SIZE,
                                        SkillMenuUtils.SLOT_SIZE));
        }
    }

    public static float skillSlotPositionX(Integer index) {
        int currentColumn = index;
        return SkillMenuUtils.SKILL_MENU_POS_X + SkillMenuUtils.MARGIN +
               (SkillMenuUtils.SLOT_SIZE + SkillMenuUtils.SPACE_BETWEEN_SLOTS) * currentColumn;
    }

    public static float SkillSlotPositionY(@SuppressWarnings("unused") Integer index) {
        return SkillMenuUtils.SKILL_MENU_POS_Y - (SkillMenuUtils.SLOT_SIZE + SkillMenuUtils.MARGIN);
    }
}
