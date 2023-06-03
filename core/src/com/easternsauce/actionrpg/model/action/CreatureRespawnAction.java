package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureRespawnAction extends GameStateAction {
    private CreatureId creatureId;
    private Vector2 pos;
    private AreaId areaId;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(creatureId);
    }

    public void applyToGame(CoreGame game) {

        Creature creature = game.getGameState().accessCreatures().getCreature(creatureId);

        if (creature != null) {
            creature.getParams().setIsAwaitingRespawn(false);
            creature.getParams().setIsDead(false);
            creature.getParams().getStats().setLife(creature.getParams().getStats().getMaxLife());
            creature.getParams().getStats().setStamina(creature.getParams().getStats().getMaxStamina());
            creature.getParams().getStats().setMana(creature.getParams().getStats().getMaxMana());

            creature.getParams().setPos(pos);
            game.addTeleportEvent(TeleportEvent.of(creatureId, pos, creature.getParams().getAreaId(), areaId, false));
        }

    }

    public static CreatureRespawnAction of(CreatureId creatureId, Vector2 pos, AreaId areaId) {
        CreatureRespawnAction action = CreatureRespawnAction.of();
        action.creatureId = creatureId;
        action.pos = pos;
        action.areaId = areaId;
        return action;
    }
}
