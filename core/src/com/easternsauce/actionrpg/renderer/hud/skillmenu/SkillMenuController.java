package com.easternsauce.actionrpg.renderer.hud.skillmenu;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.command.ActionPerformCommand;
import com.easternsauce.actionrpg.model.action.SkillPickerMenuActivateAction;
import com.easternsauce.actionrpg.model.action.SkillPickerMenuDeactivateAction;
import com.easternsauce.actionrpg.model.action.SkillPickerMenuSlotChangeAction;
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
            Rect rect = SkillMenuPositioning.getSkillPickerRect(i.get());

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

        SkillMenuPositioning.skillRectangles.forEach((slotNum, rect) -> {
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
