package com.easternsauce.actionrpg.renderer.hud.skillmenu;

import com.badlogic.gdx.Gdx;
import com.easternsauce.actionrpg.command.ActionPerformCommand;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.skillmenu.SkillPickerMenuActivateAction;
import com.easternsauce.actionrpg.model.action.skillmenu.SkillPickerMenuDeactivateAction;
import com.easternsauce.actionrpg.model.action.skillmenu.SkillPickerMenuSlotChangeAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.renderer.util.Rect;
import com.esotericsoftware.kryonet.Client;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(staticName = "of")
@Data
public class SkillMenuController {
    @SuppressWarnings("UnusedReturnValue")
    public boolean performSkillMenuPickerClick(Client client, CoreGame game) {
        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        AtomicInteger i = new AtomicInteger();

        Creature player = game.getGameState().accessCreatures().getCreature(game.getGameState().getThisClientPlayerId());

        player.availableSkills().forEach((skillType, level) -> {
            Rect rect = Rect.of(SkillMenuModel.SKILL_PICKER_MENU_POS_X,
                                SkillMenuModel.SKILL_PICKER_MENU_POS_Y + 25f * i.get(),
                                Gdx.graphics.getWidth() / 6f,
                                20f);

            if (rect.contains(x, y)) {
                client.sendTCP(ActionPerformCommand.of(SkillPickerMenuSlotChangeAction.of(game
                                                                                              .getGameState()
                                                                                              .getThisClientPlayerId(),
                                                                                          skillType)));
                isSuccessful.set(true);
            }

            i.getAndIncrement();
        });

        if (!isSuccessful.get()) {
            client.sendTCP(ActionPerformCommand.of(SkillPickerMenuDeactivateAction.of(game
                                                                                          .getGameState()
                                                                                          .getThisClientPlayerId())));
        }

        return isSuccessful.get();
    }

    public boolean performSkillMenuClick(Client client, CoreGame game) {
        float x = game.hudMousePos().getX();
        float y = game.hudMousePos().getY();

        AtomicBoolean isSuccessful = new AtomicBoolean(false);

        SkillMenuModel.skillRectangles.forEach((slotNum, rect) -> {
            if (rect.contains(x, y)) {
                client.sendTCP(ActionPerformCommand.of(SkillPickerMenuActivateAction.of(game
                                                                                            .getGameState()
                                                                                            .getThisClientPlayerId(), slotNum)));
                isSuccessful.set(true);
            }
        });

        return isSuccessful.get();

    }
}
