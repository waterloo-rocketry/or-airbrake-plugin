package com.waterloorocketry.airbrakeplugin.util;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.function.Function;

/**
 * A {@link ComputedMap} which is also a {@link SortedMap}
 */
public class ComputedSortedMap<K, V, R> extends ComputedMap<K, V, R> implements SortedMap<K, R> {
    public ComputedSortedMap(SortedMap<K, V> map, Function<V, R> compute) {
        super(map, compute);
    }

    @Override
    protected SortedMap<K, V> getMap() {
        return (SortedMap<K, V>) super.getMap();
    }

    @Override
    public Comparator<? super K> comparator() {
        return getMap().comparator();
    }

    @Override
    public SortedMap<K, R> subMap(K fromKey, K toKey) {
        return new ComputedSortedMap<>(getMap().subMap(fromKey, toKey), getCompute());
    }

    @Override
    public SortedMap<K, R> headMap(K toKey) {
        return new ComputedSortedMap<>(getMap().headMap(toKey), getCompute());
    }

    @Override
    public SortedMap<K, R> tailMap(K fromKey) {
        return new ComputedSortedMap<>(getMap().tailMap(fromKey), getCompute());
    }

    @Override
    public K firstKey() {
        return getMap().firstKey();
    }

    @Override
    public K lastKey() {
        return getMap().lastKey();
    }
}
