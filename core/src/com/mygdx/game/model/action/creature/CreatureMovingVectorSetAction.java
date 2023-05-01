package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureMovingVectorSetAction extends GameStateAction {
    private Boolean isServerSideOnly = false;
    private CreatureId creatureId;
    private Vector2 movingVector;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, creatureId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature creature = game.getGameState().getCreature(creatureId);

        if (creature != null) {
            creature.getParams().setMovingVector(movingVector);
        }

    }

    public static CreatureMovingVectorSetAction of(CreatureId creatureId, Vector2 movingVector) {
        CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of();
        action.creatureId = creatureId;
        action.movingVector = movingVector;
        return action;
    }
}
