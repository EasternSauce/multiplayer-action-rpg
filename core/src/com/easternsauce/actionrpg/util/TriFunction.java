package com.easternsauce.actionrpg.util;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
  @SuppressWarnings("unused")
  default <K> TriFunction<T, U, V, K> andThen(Function<? super R, ? extends K> after) {
    Objects.requireNonNull(after);
    return (T t, U u, V v) -> after.apply(apply(t, u, v));
  }

  R apply(T t, U u, V v);
}