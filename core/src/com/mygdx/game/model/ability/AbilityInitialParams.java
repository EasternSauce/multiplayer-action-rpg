package com.mygdx.game.model.ability;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor
@Data
public class AbilityInitialParams {
    AbilityId abilityId;
    AreaId areaId;
    CreatureId creatureId;
    Vector2 abilityDirVector;
    Vector2 abilityChainFromPos;
    Vector2 creaturePosWhenSkillPerformed;
    Float abilityWidth;
    Float abilityHeight;
    Float abilityRotationAngle = 0f;
    Set<CreatureId> abilityCreaturesAlreadyHit = new ConcurrentSkipListSet<>();
    Vector2 creaturePosCurrent;
}
