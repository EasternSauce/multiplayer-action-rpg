package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.gamestate.GameState;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureRespawnAction extends GameStateAction {
    CreatureId creatureId;
    Vector2 pos;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return pos;
    }

    public void applyToGame(GameActionApplicable game) {

        Creature creature = game.getGameState().getCreature(creatureId);

        if (creature != null) {
            creature.getParams().setIsAwaitingRespawn(false);
            creature.getParams().setIsDead(false);
            creature.getParams().setLife(creature.getParams().getMaxLife());
            creature.getParams().setStamina(creature.getParams().getMaxStamina());
            creature.getParams().setMana(creature.getParams().getMaxMana());

            creature.getParams().setPos(pos);
            game.addTeleportEvent(TeleportEvent.of(creatureId,
                    pos,
                    creature.getParams().getAreaId(),
                    creature.getParams().getAreaId()));
        }

    }
}
