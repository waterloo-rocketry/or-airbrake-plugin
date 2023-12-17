package airbrakeplugin.AirbrakePlugin.Controllers;

import net.sf.openrocket.simulation.SimulationStatus;

/**
 * Control airbrake extension during simulation
 */
public interface Controller {

    /**
     * Calculate the target airbrake extension given current flight conditions
     * @return Target airbrake extension (percentage)
     */
    public double calculateTargetExt(SimulationStatus status);
}
