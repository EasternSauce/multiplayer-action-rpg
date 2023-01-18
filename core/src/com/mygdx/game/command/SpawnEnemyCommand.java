package com.mygdx.game.command;

import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data

public class SpawnEnemyCommand implements GameCommand {
    CreatureId creatureId;
    String enemyType;
    Vector2 pos;

}
