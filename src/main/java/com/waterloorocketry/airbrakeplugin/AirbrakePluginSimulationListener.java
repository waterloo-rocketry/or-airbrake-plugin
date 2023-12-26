package com.waterloorocketry.airbrakeplugin;

import com.waterloorocketry.airbrakeplugin.Airbrakes.Airbrakes;
import com.waterloorocketry.airbrakeplugin.Controllers.Controller;

import net.sf.openrocket.simulation.FlightDataBranch;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationStatus;
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
	 * Called each timestep to override the sim's thrust before the sim can calculate it.
	 * Returning NaN makes the sim ignore this plugin and calculate thrust normally instead.
	 * @param status
	 * @return The thrust to override with, or NaN if we don't want to override.
	 */
	@Override
	public double preSimpleThrustCalculation(SimulationStatus status) {
		// Get latest flight conditions and airbrake extension
		FlightDataBranch flightData = status.getFlightData();

		final double velocity = flightData.getLast(FlightDataType.TYPE_VELOCITY_Z);
		final double airbrakeExt = flightData.getLast(airbrakeExtDataType);

		// Calculate and override thrust. No coast check here since it's done in preStep
		if (!Double.isNaN(airbrakeExt)) {
            return airbrakes.calculateDragForce(controller, velocity, airbrakeExt);
		} else {
			return Double.NaN;
		}
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