package com.waterloorocketry.airbrakeplugin.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A map that wraps an inner map and a computation.
 * Each value of the map is used to compute another value, which
 * is the value returned. The map does not support mutation directly (only
 * through the inner map).
 * @param <K> Key type of this map and the inner map
 * @param <V> Value type of the inner map, input type of the computation
 * @param <R> Return type of the computation
 */
public class ComputedMap<K, V, R> implements Map<K, R> {
    private final Map<K, V> map;
    private final Function<V, R> compute;

    public ComputedMap(Map<K, V> map, Function<V, R> compute) {
        this.map = map;
        this.compute = compute;
    }

    protected Map<K, V> getMap() {
        return map;
    }

    protected Function<V, R> getCompute() {
        return compute;
    }

    protected static class ComputedEntry<K, V, R> implements Entry<K, R> {
        private final Entry<K, V> entry;
        private final Function<V, R> compute;

        protected ComputedEntry(Entry<K, V> entry, Function<V, R> compute) {
            this.entry = entry;
            this.compute = compute;
        }

        @Override
        public K getKey() {
            return entry.getKey();
        }

        @Override
        public R getValue() {
            return compute.apply(entry.getValue());
        }

        @Override
        public R setValue(R value) {
            throw new UnsupportedOperationException("cannot write to a ComputedNavigableMap");
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R get(Object key) {
        return compute.apply(map.get(key));
    }

    @Override
    public R put(K key, R value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R remove(Object key) {
        return compute.apply(map.remove(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends R> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<R> values() {
        // TODO
        throw new RuntimeException("not implemented");
    }

    @Override
    public Set<Entry<K, R>> entrySet() {
        // TODO
        throw new RuntimeException("not implemented");
    }
}
