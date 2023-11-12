package com.easternsauce.actionrpg.util;

import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class OrderedMap<K, V> extends ConcurrentSkipListMap<K, V> {
  public OrderedMap() {
  }

  public OrderedMap(Comparator<? super K> comparator) {
    super(comparator);
  }

  public OrderedMap(Map<? extends K, ? extends V> m) {
    super(m);
  }

  public OrderedMap(SortedMap<K, ? extends V> m) {
    super(m);
  }

  @Override
  public V get(Object key) {
    if (!containsKey(key)) throw new NoSuchElementException();
    if (key == null) throw new NullPointerException();
    return super.get(key);
  }

  @Override
  public V put(K key, V value) {
    if (value == null) throw new RuntimeException("tried to put null element in map");
    return super.put(key, value);
  }
}
