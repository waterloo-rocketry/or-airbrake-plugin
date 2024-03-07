package com.waterloorocketry.airbrakeplugin.test.simulated;

import com.waterloorocketry.airbrakeplugin.simulated.SingleVarLinearInterp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleVarLinearInterpTest {
    @Test
    void compute() {
        SingleVarLinearInterp<Void, SingleVarLinearInterp.ConstantValue> interp = new SingleVarLinearInterp<>(
                new double[] { 0.0, 1.0, 2.0 },
                new SingleVarLinearInterp.ConstantValue[] { new SingleVarLinearInterp.ConstantValue(0.0), new SingleVarLinearInterp.ConstantValue(2.0), new SingleVarLinearInterp.ConstantValue(5.0) }
        );
        assertEquals(interp.compute(null, 0.0), 0.0);
        assertEquals(interp.compute(null, 1.0), 2.0);
        assertEquals(interp.compute(null, 2.0), 5.0);
        assertEquals(interp.compute(null, 0.3), 0.6, 0.00001);
        assertEquals(interp.compute(null, 1.5), 3.5, 0.00001);
        assertThrows(IndexOutOfBoundsException.class, () -> interp.compute(null, -1.0));
        assertThrows(IndexOutOfBoundsException.class, () -> interp.compute(null, 2.01));
        assertThrows(IllegalArgumentException.class, () -> interp.compute(null, Double.NaN));
    }

    @Test
    void constructor() {
        SingleVarLinearInterp.ConstantValue zero = new SingleVarLinearInterp.ConstantValue(0.0);
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp<>(new double[0], new SingleVarLinearInterp.ConstantValue[0]));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp<>(new double[3], new SingleVarLinearInterp.ConstantValue[] { zero, zero, zero, zero }));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp<>(new double[] { Double.NaN, 0.0, 0.2 }, new SingleVarLinearInterp.ConstantValue[] { zero, zero, zero }));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp<>(new double[] { -0.5, 0.0, 0.2 }, new SingleVarLinearInterp.ConstantValue[] { zero, null, zero }));
        assertThrows(IllegalArgumentException.class, () -> new SingleVarLinearInterp<>(new double[] { 0.5, 0.0, 0.2 }, new SingleVarLinearInterp.ConstantValue[] { zero, zero, zero }));
    }
}
