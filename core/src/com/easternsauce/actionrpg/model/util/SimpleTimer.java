package com.easternsauce.actionrpg.model.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(staticName = "of")
public class SimpleTimer {
    @Getter
    @Setter
    private float time = 0f;
    @Getter
    private boolean isRunning = false;

    public static SimpleTimer getExpiredTimer() {
        SimpleTimer simpleTimer = SimpleTimer.of();
        simpleTimer.time = Float.MAX_VALUE;
        simpleTimer.isRunning = false;
        return simpleTimer;
    }

    public static SimpleTimer getStartedTimer() {
        SimpleTimer simpleTimer = SimpleTimer.of();
        simpleTimer.time = 0;
        simpleTimer.isRunning = true;
        return simpleTimer;
    }

    @SuppressWarnings("unused")
    public void start() {
        isRunning = true;
    }

    @SuppressWarnings("unused")
    public void stop() {
        time = 0f;
        isRunning = false;
    }

    public void restart() {
        time = 0f;
        isRunning = true;
    }

    public void update(float delta) {
        if (isRunning) {
            time = time + delta;
        }
    }

}
