package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerRemoveAction extends GameStateAction {
    private CreatureId playerId;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return getActionCreaturePos(playerId, game);
    }

    @Override
    public void applyToGame(CoreGame game) {
        game.getGameState().getRemovedCreatures().put(playerId, game.getGameState().getCreature(playerId));

        game.getEventProcessor().getCreatureModelsToBeRemoved().add(playerId);
    }

    public static PlayerRemoveAction of(CreatureId playerId) {
        PlayerRemoveAction action = PlayerRemoveAction.of();
        action.playerId = playerId;
        return action;
    }
}
