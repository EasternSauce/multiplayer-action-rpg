package com.mygdx.game.ability;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.SimpleTimer;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
public class AbilityParams {
    AbilityId id;
    AreaId areaId;
    AbilityState state = AbilityState.INACTIVE;
    Vector2 pos;
    Float width;
    Float height;
    CreatureId creatureId;
    SimpleTimer stateTimer = SimpleTimer.of(Float.MAX_VALUE, false);
    Vector2 dirVector;
    String textureName;
//    AbilityRect rect;

    Float rotationAngle = 0f;

    Boolean isChannelAnimationLooping = false;
    Boolean isActiveAnimationLooping = false;

    Float range = null;

    Set<CreatureId> creaturesAlreadyHit = new HashSet<>();

    Vector2 velocity = null;
    Float speed = null;

    Float damage = null;

    Float channelTime;
    Float activeTime;

    Boolean attackWithoutMoving = false;

    public static AbilityParams of(AbilityId abilityId, AreaId areaId, Float width, Float height, Float channelTime,
                                   Float activeTime,

                                   Float range,
                                   String textureName) {
        AbilityParams params = AbilityParams.of();
        params.id = abilityId;
        params.areaId = areaId;
        params.width = width;
        params.height = height;
        params.channelTime = channelTime;
        params.activeTime = activeTime;
        params.range = range;
        params.textureName = textureName;

        return params;
    }

}
