package com.mygdx.game.model.creature;

import com.mygdx.game.model.renderer.CreatureAnimationConfig;
import com.mygdx.game.model.util.SimpleTimer;
import com.mygdx.game.model.util.Vector2;
import lombok.*;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class CreatureParams {
    @NonNull
    CreatureId creatureId;

    @NonNull
    Vector2 pos;

    @NonNull
    CreatureAnimationConfig animationConfig;

    @NonNull
    SimpleTimer animationTimer;

    @NonNull
    Vector2 movingVector;

    @NonNull
    Vector2 movementCommandTargetPos;

    @NonNull
    Boolean reachedTargetPos;

    @NonNull
    Boolean isMoving;

    @NonNull
    Float speed;

}
