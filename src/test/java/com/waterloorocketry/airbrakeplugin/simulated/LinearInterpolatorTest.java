package com.waterloorocketry.airbrakeplugin.simulated;

import com.waterloorocketry.airbrakeplugin.simulated.LinearInterpolator;
import org.junit.jupiter.api.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LinearInterpolatorTest {
    @Test
    void compute() {
        NavigableMap<Double, Double> data = new TreeMap<>();
        data.put(0.0, 0.0);
        data.put(1.0, 2.0);
        data.put(2.0, 5.0);
        LinearInterpolator interpolator = new LinearInterpolator(data);
        assertEquals(interpolator.compute(0.0), 0.0);
        assertEquals(interpolator.compute(1.0), 2.0);
        assertEquals(interpolator.compute(2.0), 5.0);
        assertEquals(interpolator.compute(0.3), 0.6, 0.00001);
        assertEquals(interpolator.compute(1.5), 3.5, 0.00001);
        assertThrows(IndexOutOfBoundsException.class, () -> interpolator.compute(-1.0));
        assertThrows(IndexOutOfBoundsException.class, () -> interpolator.compute(2.01));
        assertThrows(IllegalArgumentException.class, () -> interpolator.compute(Double.NaN));
    }
}
