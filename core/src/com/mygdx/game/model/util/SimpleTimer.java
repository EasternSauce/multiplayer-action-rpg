package com.mygdx.game.model.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
@Builder
public class SimpleTimer {
    @Builder.Default
    float time = 0f;
    @Builder.Default
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

}
