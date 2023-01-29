package com.mygdx.game.model.creature;

import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.util.SimpleTimer;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureParams {
    CreatureId id;

    AreaId areaId;

    Vector2 pos;
    Vector2 previousPos;

    SimpleTimer animationTimer = SimpleTimer.of(0, true);

    Vector2 movingVector = Vector2.of(0, 0);

    Vector2 movementCommandTargetPos = Vector2.of(0, 0);

    Boolean reachedTargetPos = true;

    Boolean isMoving = false;

    Float speed = 10f;

    CreatureId targetCreatureId = null;

    Boolean forcePathCalculation = false;
    SimpleTimer pathCalculationCooldownTimer = SimpleTimer.of();

    List<Vector2> pathTowardsTarget = null;

    Float life = 100f;
    Float maxLife = 100f;
    Float stamina = 100f;
    Float maxStamina = 100f;
    Float mana = 100f;
    Float maxMana = 100f;
    Float armor = 0f;

    String textureName;

    SimpleTimer movementCommandsPerSecondLimitTimer = SimpleTimer.of(Float.MAX_VALUE, false);
    SimpleTimer attackCommandsPerSecondLimitTimer = SimpleTimer.of(Float.MAX_VALUE, false);

    SimpleTimer isStillMovingTimer = SimpleTimer.of(Float.MAX_VALUE, false);

    SimpleTimer attackCooldownTimer = SimpleTimer.of(Float.MAX_VALUE, false);

    SimpleTimer respawnTimer = SimpleTimer.of(Float.MAX_VALUE, false);
    Float respawnTime = 5f;

    Float attackCooldownTime = 0.7f;

    Boolean justDied = false;
    Boolean isDead = false;
    Boolean awaitingRespawn = false;

    public static CreatureParams of(CreatureId creatureId, AreaId areaId, Vector2 pos, String textureName) {
        CreatureParams params = CreatureParams.of();
        params.id = creatureId;
        params.areaId = areaId;
        params.pos = pos;
        params.textureName = textureName;
        return params;
    }


}
