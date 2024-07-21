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
        Entry<K, V> entry = getMap().lowerEntry(key);
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public K lowerKey(K key) {
        return getMap().lowerKey(key);
    }

    @Override
    public Entry<K, R> floorEntry(K key) {
        Entry<K, V> entry = getMap().floorEntry(key);
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public K floorKey(K key) {
        return getMap().floorKey(key);
    }

    @Override
    public Entry<K, R> ceilingEntry(K key) {
        Entry<K, V> entry = getMap().ceilingEntry(key);
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public K ceilingKey(K key) {
        return getMap().ceilingKey(key);
    }

    @Override
    public Entry<K, R> higherEntry(K key) {
        Entry<K, V> entry = getMap().higherEntry(key);
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public K higherKey(K key) {
        return getMap().higherKey(key);
    }

    @Override
    public Entry<K, R> firstEntry() {
        Entry<K, V> entry = getMap().firstEntry();
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public Entry<K, R> lastEntry() {
        Entry<K, V> entry = getMap().lastEntry();
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public Entry<K, R> pollFirstEntry() {
        Entry<K, V> entry = getMap().pollFirstEntry();
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public Entry<K, R> pollLastEntry() {
        Entry<K, V> entry = getMap().pollLastEntry();
        if (entry != null) {
            return new ComputedEntry<>(entry, getCompute());
        } else {
            return null;
        }
    }

    @Override
    public NavigableMap<K, R> descendingMap() {
        return new LazyNavigableMap<>(getMap().descendingMap(), getCompute());
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        throw new UnsupportedOperationException("not implemented");
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