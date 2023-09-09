package com.easternsauce.actionrpg.model.ability.util;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.util.Vector2;

public class PointTargetedAbilityUtils {
  public static Vector2 calculatePos(Vector2 targetPos, Vector2 creaturePos, AreaId areaId, float maxRange, CoreGame game) {
    Vector2 vectorTowards = creaturePos.vectorTowards(targetPos);

    Vector2 destinationPos = creaturePos;

    int parts = 7;

    boolean obstructed = false;
    for (int i = 1; i <= parts; i++) {
      if (!obstructed) {
        Vector2 vector = vectorTowards.multiplyBy((float) i / parts);
        Vector2 point = creaturePos.add(vector);

        if (game.isLineBetweenPointsUnobstructedByTerrain(areaId, creaturePos, point)) {
          destinationPos = point;
        } else {
          obstructed = true;
        }
      }
    }

    if (creaturePos.distance(destinationPos) > maxRange) {
      destinationPos = creaturePos.add(vectorTowards.normalized().multiplyBy(maxRange));
    }

    return destinationPos;
  }
}
