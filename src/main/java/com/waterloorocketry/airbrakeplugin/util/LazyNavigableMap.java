package com.waterloorocketry.airbrakeplugin.util;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.function.Function;

/**
 * A {@link LazySortedMap} which is also a {@link NavigableMap}.
 * This map wraps an inner map from {@param <K>} to {@param <V>} and
 * a computation function from {@param <V>} to {@param <R>}, acting as
 * a map from {@param <K>} to {@param <R>} by computing values lazily.
 */
public class LazyNavigableMap<K, V, R> extends LazySortedMap<K, V, R> implements NavigableMap<K, R> {
    public LazyNavigableMap(NavigableMap<K, V> map, Function<V, R> compute) {
        super(map, compute);
    }

    @Override
    protected NavigableMap<K, V> getMap() {
        return (NavigableMap<K, V>) super.getMap();
    }

    @Override
    public Entry<K, R> lowerEntry(K key) {
        return new ComputedEntry<>(getMap().lowerEntry(key), getCompute());
    }

    @Override
    public K lowerKey(K key) {
        return getMap().lowerKey(key);
    }

    @Override
    public Entry<K, R> floorEntry(K key) {
        return new ComputedEntry<>(getMap().floorEntry(key), getCompute());
    }

    @Override
    public K floorKey(K key) {
        return getMap().floorKey(key);
    }

    @Override
    public Entry<K, R> ceilingEntry(K key) {
        return new ComputedEntry<>(getMap().ceilingEntry(key), getCompute());
    }

    @Override
    public K ceilingKey(K key) {
        return getMap().ceilingKey(key);
    }

    @Override
    public Entry<K, R> higherEntry(K key) {
        return new ComputedEntry<>(getMap().higherEntry(key), getCompute());
    }

    @Override
    public K higherKey(K key) {
        return getMap().higherKey(key);
    }

    @Override
    public Entry<K, R> firstEntry() {
        return new ComputedEntry<>(getMap().firstEntry(), getCompute());
    }

    @Override
    public Entry<K, R> lastEntry() {
        return new ComputedEntry<>(getMap().lastEntry(), getCompute());
    }

    @Override
    public Entry<K, R> pollFirstEntry() {
        return new ComputedEntry<>(getMap().pollFirstEntry(), getCompute());
    }

    @Override
    public Entry<K, R> pollLastEntry() {
        return new ComputedEntry<>(getMap().pollLastEntry(), getCompute());
    }

    @Override
    public NavigableMap<K, R> descendingMap() {
        return new LazyNavigableMap<>(getMap().descendingMap(), getCompute());
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        // TODO
        throw new RuntimeException("not implemented");
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        // TODO
        throw new RuntimeException("not implemented");
    }

    @Override
    public NavigableMap<K, R> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return new LazyNavigableMap<>(getMap().subMap(fromKey, fromInclusive, toKey, toInclusive), getCompute());
    }

    @Override
    public NavigableMap<K, R> headMap(K toKey, boolean inclusive) {
        return new LazyNavigableMap<>(getMap().headMap(toKey, inclusive), getCompute());
    }

    @Override
    public NavigableMap<K, R> tailMap(K fromKey, boolean inclusive) {
        return new LazyNavigableMap<>(getMap().headMap(fromKey, inclusive), getCompute());
    }
}