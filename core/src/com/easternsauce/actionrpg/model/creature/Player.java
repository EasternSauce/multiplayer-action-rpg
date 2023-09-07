package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class Player extends Creature {
  @Getter
  CreatureParams params;

  public static Player of(CreatureId playerId, AreaId areaId, Vector2 pos, String textureName, int rngSeed) {
    CreatureParams params = CreatureParams.of(playerId, areaId, pos, textureName, rngSeed);
    // TODO fix later
    params.getStats().setLife(200f);
    params.getStats().setMaxLife(200f);

    params.getStats().setMana(200f);
    params.getStats().setMaxMana(200f);

    Player player = Player.of();
    player.params = params;
    return player;
  }

  @Override
  public boolean isCurrentlyActive(CoreGame game) {
    return game.getGameState().accessCreatures().getActiveCreatureIds().contains(getId());
  }
}
