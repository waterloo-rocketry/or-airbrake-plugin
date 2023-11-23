package net.sf.openrocket.BrakeOR;

import net.sf.openrocket.aerodynamics.AerodynamicForces;
import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;

/**
 * Connect to a simulation and listen for various events during the simulation.
 */
public class AirbrakePluginSimulationListener extends AbstractSimulationListener {
	double airbrakeExtension = 0;
	boolean burnout = false;
	// TODO: talk to richard enough to determine how to modify the Cd accurately
	double airbrakeCd = 100;

	public AirbrakePluginSimulationListener() {
		super();
	}

	/**
	 * Called when the simulation starts.
	 * @param status Holds flight status data during simulation.
	 */
	@Override
	public void startSimulation(SimulationStatus status) {
    	System.out.println("Starting simulation");
	}

	/**
	 * Called each timestep after the aerodynamic forces are calculated but before the forces are applied to the rocket.
	 * @param status
	 * @param forces
	 * @return
	 */
	@Override
	public AerodynamicForces postAerodynamicCalculation(SimulationStatus status, AerodynamicForces forces) {
		// Modify rocket Cd between motor burnout and apogee
		if (burnout && !status.isApogeeReached()) {
			forces.setCDaxial(forces.getCDaxial() + airbrakeCd);
		}

		return forces;
	}

	/**
	 * Called when motor burnout occurs. This is handled as an event.
	 * @param status
	 * @param event
	 * @return
	 */
	@Override
	public boolean handleFlightEvent(SimulationStatus status, FlightEvent event) {
		if (event.getType() == FlightEvent.Type.BURNOUT) {
			burnout = true;
		}
		return true;
	}
}