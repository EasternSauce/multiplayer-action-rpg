package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
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
