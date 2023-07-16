package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureMovementParams {
    @NonNull
    private Vector2 movingVector = Vector2.of(0, 0);
    @NonNull
    private Vector2 dashingVector = Vector2.of(0, 0);
    @NonNull
    private Vector2 movementCommandTargetPos = Vector2.of(0, 0);
    @NonNull
    private Boolean reachedTargetPos = true;
    @NonNull
    private Boolean moving = false;
    @NonNull
    private Boolean dashing = false;
    @NonNull
    private SimpleTimer movementActionsPerSecondLimiterTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private SimpleTimer changeAimDirectionActionsPerSecondLimiterTimer = SimpleTimer.getExpiredTimer(); // 10/10 field name
    @NonNull
    private SimpleTimer stillMovingCheckTimer = SimpleTimer.getExpiredTimer();
    @NonNull
    private Vector2 facingVector = Vector2.of(0f, 0f);
    @NonNull
    private java.lang.Float dashingVelocity = 0f;
    @NonNull
    private Vector2 previousPos;
    @NonNull
    private Boolean stillInsideGateAfterTeleport = false;
    @NonNull
    private AreaId areaWhenEnteredGate;
    @NonNull
    private Vector2 aimDirection = Vector2.of(0f, 0f);
    @NonNull
    private SimpleTimer gateTeleportCooldownTimer = SimpleTimer.getExpiredTimer();

    private Vector2 lastStoppedPos = null;
}