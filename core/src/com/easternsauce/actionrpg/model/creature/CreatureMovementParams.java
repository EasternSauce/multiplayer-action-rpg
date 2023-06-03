package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.SimpleTimer;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureMovementParams {
    private Vector2 movingVector = Vector2.of(0, 0);
    private Vector2 dashingVector = Vector2.of(0, 0);
    private Vector2 movementCommandTargetPos = Vector2.of(0, 0);
    private Boolean reachedTargetPos = true;
    private Boolean isMoving = false;
    private Boolean isDashing = false;
    private SimpleTimer movementActionsPerSecondLimiterTimer = SimpleTimer.getExpiredTimer();
    private SimpleTimer changeAimDirectionActionsPerSecondLimiterTimer = SimpleTimer.getExpiredTimer(); // 10/10 field name
    private com.easternsauce.actionrpg.model.util.SimpleTimer isStillMovingCheckTimer = SimpleTimer.getExpiredTimer();
    private com.easternsauce.actionrpg.model.util.Vector2 facingVector = com.easternsauce.actionrpg.model.util.Vector2.of(0f, 0f);
    private java.lang.Float dashingVelocity = 0f;
    private Vector2 previousPos;
    private Boolean isStillInsideGateAfterTeleport = false;
    private AreaId areaWhenEnteredGate;
    private Vector2 aimDirection = Vector2.of(0f, 0f);
    private SimpleTimer gateTeleportCooldownTimer = SimpleTimer.getExpiredTimer();
}