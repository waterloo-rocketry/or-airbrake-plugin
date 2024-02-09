package com.waterloorocketry.airbrakeplugin.test.simulated;

import com.waterloorocketry.airbrakeplugin.simulated.SingleVarLinearInterp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleVarLinearInterpTest {
    @Test
    void compute() {
        SingleVarLinearInterp interp = new SingleVarLinearInterp(
                new double[] { 0.0, 1.0, 2.0 },
                new double[] { 0.0, 2.0, 5.0 }
        );
        assertEquals(interp.compute(0.0), 0.0);
        assertEquals(interp.compute(1.0), 2.0);
        assertEquals(interp.compute(2.0), 5.0);
        assertEquals(interp.compute(0.3), 0.6, 0.00001);
        assertEquals(interp.compute(1.5), 3.5, 0.00001);
        assertThrows(IndexOutOfBoundsException.class, () -> interp.compute(-1.0));
        assertThrows(IndexOutOfBoundsException.class, () -> interp.compute(2.01));
        assertThrows(IllegalArgumentException.class, () -> interp.compute(Double.NaN));
    }

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp(new double[0], new double[0]));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp(new double[3], new double[4]));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp(new double[] { Double.NaN, 0.0, 0.2 }, new double[] { 0.0, 0.0, 0.0 }));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp(new double[] { -0.5, 0.0, 0.2 }, new double[] { 0.0, Double.NaN, 0.0 }));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp(new double[] { 0.5, 0.0, 0.2 }, new double[] { 0.0, 0.0, 0.0 }));
    }
}
