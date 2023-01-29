package com.mygdx.game.command;

import com.mygdx.game.ability.AbilityId;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class SpawnAbilityCommand implements GameCommand {
    AbilityId abilityId;
    AreaId areaId;
    CreatureId creatureId;
    String abilityType;
    Vector2 pos;
    Vector2 dirVector;
}
