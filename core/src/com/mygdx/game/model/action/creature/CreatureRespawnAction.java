package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class CreatureRespawnAction implements GameStateAction {
    CreatureId creatureId;
    Vector2 pos;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return pos;
    }

    public void applyToGame(GameActionApplicable game) {

        Creature creature = game.getCreature(creatureId);

        if (creature != null) {
            creature.params().awaitingRespawn(false);
            creature.params().isDead(false);
            creature.params().life(creature.params().maxLife());
            creature.params().stamina(creature.params().maxStamina());
            creature.params().mana(creature.params().maxMana());

            creature.params().pos(pos);
            game.addTeleportEvent(TeleportEvent.of(creatureId,
                                                   pos,
                                                   creature.params().areaId(),
                                                   creature.params().areaId()));
        }

    }
}
