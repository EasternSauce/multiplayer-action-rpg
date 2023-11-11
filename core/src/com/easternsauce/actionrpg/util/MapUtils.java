package com.easternsauce.actionrpg.util;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MapUtils {
  public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
    for (Map.Entry<T, E> entry : map.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  public static <T> T getRandomElementOfWeightedMap(Map<T, Integer> map, float randomFloat) {
    AtomicReference<T> pickedRandomElement = new AtomicReference<>(null);

    AtomicReference<Float> totalWeight = new AtomicReference<>((float) 0);

    map.forEach((enemyTemplate, weight) -> totalWeight.set(totalWeight.get() + weight));

    AtomicReference<Float> randValue = new AtomicReference<>(Math.abs(randomFloat) * totalWeight.get());

    map.forEach((element, weight) -> {
      if (pickedRandomElement.get() == null && randValue.get() < weight) {
        pickedRandomElement.set(element);
      }
      randValue.updateAndGet(value -> value - weight);
    });

    if (pickedRandomElement.get() == null) {
      throw new RuntimeException("impossible result!");
    }

    return pickedRandomElement.get();
  }

}
