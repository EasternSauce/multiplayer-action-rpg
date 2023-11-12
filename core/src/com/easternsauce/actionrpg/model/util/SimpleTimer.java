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
  private boolean running = false;

  public static SimpleTimer getExpiredTimer() {
    SimpleTimer simpleTimer = SimpleTimer.of();
    simpleTimer.time = Float.MAX_VALUE;
    simpleTimer.running = false;
    return simpleTimer;
  }

  public static SimpleTimer getStartedTimer() {
    SimpleTimer simpleTimer = SimpleTimer.of();
    simpleTimer.time = 0;
    simpleTimer.running = true;
    return simpleTimer;
  }

  public static SimpleTimer of(SimpleTimer other) {
    SimpleTimer simpleTimer = SimpleTimer.of();

    simpleTimer.time = other.time;
    simpleTimer.running = other.running;

    return simpleTimer;
  }

  @SuppressWarnings("unused")
  public void start() {
    running = true;
  }

  @SuppressWarnings("unused")
  public void stop() {
    time = 0f;
    running = false;
  }

  public void restart() {
    time = 0f;
    running = true;
  }

  public void update(float delta) {
    if (running) {
      time = time + delta;
    }
  }

}
