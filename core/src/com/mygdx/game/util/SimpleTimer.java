package com.mygdx.game.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class SimpleTimer {
    float time = 0f;
    boolean isRunning = false;

    public void start() {
        isRunning = true;
    }

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

}
