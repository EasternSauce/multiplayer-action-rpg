package com.easternsauce.actionrpg.model.action.creature;

import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffect;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureChangeAimDirectionAction extends GameStateAction {
    private CreatureId creatureId;

    private Vector2 mousePos;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(creatureId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null && creature.isAlive() && !creature.isEffectActive(CreatureEffect.STUN, game)) {
            creature.getParams().setAimDirection(mousePos.normalized());

            creature.getParams().getChangeAimDirectionActionsPerSecondLimiterTimer().restart();
        }
    }

    public static CreatureChangeAimDirectionAction of(CreatureId creatureId, Vector2 mousePos) {
        CreatureChangeAimDirectionAction action = CreatureChangeAimDirectionAction.of();
        action.creatureId = creatureId;
        action.mousePos = mousePos;
        return action;
    }
}
