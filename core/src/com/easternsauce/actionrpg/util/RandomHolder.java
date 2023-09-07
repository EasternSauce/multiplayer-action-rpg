package com.easternsauce.actionrpg.util;

public class RandomHolder {
  private static final DeterministicRandom deterministicRandom = new DeterministicRandom();

  public static DeterministicRandom getRandom() {
    return deterministicRandom;
  }

}
