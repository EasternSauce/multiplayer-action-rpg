package com.easternsauce.actionrpg.model.util;

public class MathHelper {
    public static float findShortestDegAngleRotation(float currentAngleDeg, float targetAngleDeg) {
        float alpha = targetAngleDeg - currentAngleDeg;
        float beta = targetAngleDeg - currentAngleDeg + 360;
        float gamma = targetAngleDeg - currentAngleDeg - 360;

        float result;
        if (Math.abs(alpha) < Math.abs(beta)) {
            if (Math.abs(alpha) < Math.abs(gamma)) {
                result = alpha;
            }
            else {
                result = gamma;
            }
        }
        else {
            if (Math.abs(beta) < Math.abs(gamma)) {
                result = beta;
            }
            else {
                result = gamma;
            }
        }
        return result;
    }
}
