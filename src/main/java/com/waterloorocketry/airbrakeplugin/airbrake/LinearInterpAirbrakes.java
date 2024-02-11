package com.waterloorocketry.airbrakeplugin.airbrake;

import com.waterloorocketry.airbrakeplugin.simulated.SingleVarLinearInterp;

/**
 * Linear interpolation between simulated data points
 */
public class LinearInterpAirbrakes implements Airbrakes {
    private final SingleVarLinearInterp interp;

    /**
     * Constructs an airbrakes object using simulated data points
     * @param exts Extension values, from exactly 0.0 to exactly 1.0
     * @param cds Corresponding CD values
     */
    public LinearInterpAirbrakes(double[] exts, double[] cds) {
        if (!(exts[0] == 0.0 && exts[exts.length - 1] == 1.0)) {
            throw new IllegalArgumentException("extension amounts must go from exactly 0.0 to 1.0");
        }
        interp = new SingleVarLinearInterp(exts, cds);
    }

    @Override
    public double calculateCD(double ext) {
        return interp.compute(ext);
    }
}
