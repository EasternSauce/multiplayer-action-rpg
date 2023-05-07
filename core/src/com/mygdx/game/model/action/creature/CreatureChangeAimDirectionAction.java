package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.effect.CreatureEffect;
import com.mygdx.game.model.util.Vector2;
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
