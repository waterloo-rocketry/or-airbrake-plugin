package com.waterloorocketry.airbrakeplugin.util;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.function.Function;

/**
 * A {@link LazyMap} which is also a {@link SortedMap}.
 * This map wraps an inner map from {@param <K>} to {@param <V>} and
 * a computation function from {@param <V>} to {@param <R>}, acting as
 * a map from {@param <K>} to {@param <R>} by computing values lazily.
 */
public class LazySortedMap<K, V, R> extends LazyMap<K, V, R> implements SortedMap<K, R> {
    public LazySortedMap(SortedMap<K, V> map, Function<V, R> compute) {
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
        return new LazySortedMap<>(getMap().subMap(fromKey, toKey), getCompute());
    }

    @Override
    public SortedMap<K, R> headMap(K toKey) {
        return new LazySortedMap<>(getMap().headMap(toKey), getCompute());
    }

    @Override
    public SortedMap<K, R> tailMap(K fromKey) {
        return new LazySortedMap<>(getMap().tailMap(fromKey), getCompute());
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
