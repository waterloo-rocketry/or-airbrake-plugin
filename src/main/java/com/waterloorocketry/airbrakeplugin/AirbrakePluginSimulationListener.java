package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.Airbrakes.Airbrakes;
import com.waterloorocketry.airbrakeplugin.Controllers.Controller;

import net.sf.openrocket.aerodynamics.AerodynamicForces;
import net.sf.openrocket.simulation.*;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.unit.UnitGroup;

/**
 * Connect to a simulation and listen for various events during the simulation.
 */
public class AirbrakePluginSimulationListener extends AbstractSimulationListener {
	private final Airbrakes airbrakes;
	private final Controller controller;
	private final FlightDataType airbrakeExtDataType = FlightDataType.getType("airbrakeExt", "airbrakeExt", UnitGroup.UNITS_RELATIVE);
	private boolean burnout = false;

	public AirbrakePluginSimulationListener(Airbrakes airbrakes, Controller controller) {
		super();
		this.airbrakes = airbrakes;
		this.controller = controller;
	}

	/**
	 * Runs before each timestep.
	 * @param status
	 * @return
	 */
	@Override
	public boolean preStep(SimulationStatus status) {
		if (burnout && !status.isApogeeReached()) {
			FlightDataBranch flightData = status.getFlightData();

			// Run controller, set in flightData
			double ext = controller.calculateTargetExt(status);
			flightData.setValue(airbrakeExtDataType, ext);
		}

		return true;
	}

	/**
	 * Overrides the coefficient of drag after the aerodynamic calculations are done each timestep.
	 */
	@Override
	public AerodynamicForces postAerodynamicCalculation(SimulationStatus status, AerodynamicForces forces) throws SimulationException {
		// Get latest flight conditions and airbrake extension
		FlightDataBranch flightData = status.getFlightData();

		final double velocity = flightData.getLast(FlightDataType.TYPE_VELOCITY_Z);
		final double airbrakeExt = flightData.getLast(airbrakeExtDataType);

		// Calculate and override cd. No coast check here since it's done in preStep
		if (!Double.isNaN(airbrakeExt)) {
			forces.setCD(airbrakes.calculateCD(controller, velocity, airbrakeExt));
		}
		System.out.println("cd " + forces.getCD());

		return forces;
	}

	// TODO: do burnout in a nicer way than practically being global var?
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