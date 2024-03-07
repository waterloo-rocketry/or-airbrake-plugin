package com.waterloorocketry.airbrakeplugin.airbrake;

import com.waterloorocketry.airbrakeplugin.simulated.SingleVarLinearInterp;

/**
 * Linear interpolation between simulated data points
 */
public class LinearInterpAirbrakes implements Airbrakes {
    private final SingleVarLinearInterp<Void, SingleVarLinearInterp.ConstantValue> interp;

    /**
     * Constructs an airbrakes object using simulated data points
     * @param exts Extension values, from exactly 0.0 to exactly 1.0
     * @param cds Corresponding CD values
     */
    public LinearInterpAirbrakes(double[] exts, double[] cds) {
        if (!(exts[0] == 0.0 && exts[exts.length - 1] == 1.0)) {
            throw new IllegalArgumentException("extension amounts must go from exactly 0.0 to 1.0");
        }
        SingleVarLinearInterp.ConstantValue[] values = new SingleVarLinearInterp.ConstantValue[cds.length];
        for (int i = 0; i < cds.length; i++) {
            values[i] = new SingleVarLinearInterp.ConstantValue(cds[i]);
        }
        interp = new SingleVarLinearInterp<>(exts, values);
    }

    @Override
    public double calculateCD(double ext) {
        return interp.compute(null, ext);
    }
}
