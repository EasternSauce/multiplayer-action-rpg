package com.mygdx.game.ability;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.SimpleTimer;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityParams {
    AbilityId id;
    AreaId areaId;
    AbilityState state = AbilityState.INACTIVE;
    Vector2 pos;
    CreatureId creatureId;
    SimpleTimer stateTimer = SimpleTimer.of(Float.MAX_VALUE, false);
    Vector2 dirVector;
    String abilityType;
//    AbilityRect rect;

    Float rotationAngle = 0f;

    Boolean isChannelAnimationLooping = false;
    Boolean isActiveAnimationLooping = false;

    public static AbilityParams of(AbilityId abilityId, AreaId areaId, Vector2 pos, String abilityType) {
        AbilityParams params = AbilityParams.of();
        params.id = abilityId;
        params.areaId = areaId;
        params.pos = pos;
        params.abilityType = abilityType;

        return params;
    }

}
