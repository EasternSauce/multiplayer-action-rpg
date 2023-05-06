package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureRespawnAction extends GameStateAction {
    private CreatureId creatureId;
    private Vector2 pos;

    @Override
    public Vector2 actionObjectPos(CoreGame game) {
        return pos;
    }

    public void applyToGame(CoreGame game) {

        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

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

    public static CreatureRespawnAction of(CreatureId creatureId, Vector2 pos) {
        CreatureRespawnAction action = CreatureRespawnAction.of();
        action.creatureId = creatureId;
        action.pos = pos;
        return action;
    }
}
